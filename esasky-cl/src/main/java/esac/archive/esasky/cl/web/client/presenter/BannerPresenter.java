package esac.archive.esasky.cl.web.client.presenter;



import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.esasky.ifcs.model.descriptor.BannerMessage;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.Modules;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.CheckForServerMessagesEvent;
import esac.archive.esasky.cl.web.client.event.banner.CheckForServerMessagesEventHandler;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemFoundEvent;
import esac.archive.esasky.cl.web.client.event.banner.ServerProblemSolvedEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleServerProblemBannerEvent;
import esac.archive.esasky.cl.web.client.event.banner.ToggleServerProblemBannerEventHandler;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;
import esac.archive.esasky.cl.web.client.utility.JSONUtils.IJSONRequestCallback;

public class BannerPresenter {

	private View view;
	private boolean anyActicitySinceLastCheck;
	private boolean bannerMessageIsActive;
	private BannerMessage lastBannerMessage;

	public interface View {
		void setText(String text);
		String getText();
		void show();
		void hide();
		boolean isShowing();
		void setIsWarning(boolean isWarning);
		void addCloseButtonClickHandler(ClickHandler handler);
	}

	public BannerPresenter(final View inputView) {
		this.view = inputView;
		bind();
		hide();
	}
	public interface BannerMessageMapper extends ObjectMapper<BannerMessage> {}
	private Timer checkBackendMessageTimer = new Timer() {

		@Override
		public void run() {
			if(anyActicitySinceLastCheck || bannerMessageIsActive) {
				checkBackendMessages();
			}
			anyActicitySinceLastCheck = false;
		}
	};

	private void bind() {

		view.addCloseButtonClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
				if(lastBannerMessage != null && !lastBannerMessage.getIsWarning()) {
					bannerMessageIsActive = false;
				}
			}
		});

		CommonEventBus.getEventBus().addHandler(CheckForServerMessagesEvent.TYPE,
				new CheckForServerMessagesEventHandler() {

			@Override
			public void onEvent(CheckForServerMessagesEvent pushEvent) {
				checkBackendMessages();
			}
		});
		
		CommonEventBus.getEventBus().addHandler(ProgressIndicatorPushEvent.TYPE,
				new ProgressIndicatorPushEventHandler() {
			
			@Override
			public void onPushEvent(ProgressIndicatorPushEvent pushEvent) {
				anyActicitySinceLastCheck = true;
			}
		});
		
		CommonEventBus.getEventBus().addHandler(AladinLiteCoordinatesChangedEvent.TYPE, new AladinLiteCoordinatesChangedEventHandler() {

			@Override
			public void onCoordsChanged(AladinLiteCoordinatesChangedEvent changeEvent) {
				anyActicitySinceLastCheck = true;
			}

		});
		
		CommonEventBus.getEventBus().addHandler(ToggleServerProblemBannerEvent.TYPE, new ToggleServerProblemBannerEventHandler() {
			
			@Override
			public void onEvent(ToggleServerProblemBannerEvent event) {
				if(view.isShowing()) {
					hide();
				} else {
					show();
				}
			}
		});
		
		checkBackendMessageTimer.scheduleRepeating(30*1000); // Every thirty seconds
		checkBackendMessages();
		
		
		if(Modules.bannersOnAllSides){
			String newMessage = "Drag me to resize";
			view.setText(newMessage);
			show();
			CommonEventBus.getEventBus().fireEvent(new ServerProblemFoundEvent());
			bannerMessageIsActive = true;
		}

	}
	
	private void hide() {
		view.hide();
	}

	private void show() {
		view.show();
	}

	private void checkBackendMessages() {
		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.BANNER_MESSAGE_URL + "?lang=" + URL.encodeQueryString(GUISessionStatus.getCurrentLanguage()), new IJSONRequestCallback() {

			@Override
			public void onSuccess(String responseText) {
				BannerMessageMapper mapper = GWT.create(BannerMessageMapper.class);
				BannerMessage bannerMessage = mapper.read(responseText);
				String message = bannerMessage.getMessage();
				if(message == null || message.isEmpty() || (GUISessionStatus.hideBannerInfo && !bannerMessage.getIsWarning())) {
					if(!view.getText().isEmpty()) {
						CommonEventBus.getEventBus().fireEvent(new ServerProblemSolvedEvent());
					}
					bannerMessageIsActive = false;
					view.setText("");
					hide();
					return;
				}
				view.setIsWarning(bannerMessage.getIsWarning());
				if(!view.getText().equals(new HTML(message).getHTML()) || bannerMessage.getIsWarning() != lastBannerMessage.getIsWarning()) {
					view.setText(message);
					show();
					if(bannerMessage.getIsWarning()) {
						CommonEventBus.getEventBus().fireEvent(new ServerProblemFoundEvent());
					} else {
						CommonEventBus.getEventBus().fireEvent(new ServerProblemSolvedEvent());
					}
					lastBannerMessage = bannerMessage;
					bannerMessageIsActive = true;
				}
			}

			@Override
			public void onError(String errorCause) {
			}

		});

	}
}
