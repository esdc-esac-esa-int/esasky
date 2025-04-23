/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.presenter;



import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEvent;
import esac.archive.absi.modules.cl.aladinlite.widget.client.event.AladinLiteCoordinatesChangedEventHandler;
import esac.archive.esasky.ifcs.model.descriptor.AdvertisingMessage;
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

import java.util.Date;

public class BannerPresenter {

	private View view;
	private boolean anyActicitySinceLastCheck;
	private boolean bannerMessageIsActive;
	private boolean showingIsAd = false;
	private BannerMessage lastBannerMessage;
	private AdvertisingMessage lastAdvertisingMessage;
	private final String BANNER_COOKIE_NAME = "esaSkyBannerCookie";


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
	public interface AdvertisingMessageMapper extends ObjectMapper<AdvertisingMessage> {}
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
				if (lastBannerMessage != null && lastBannerMessage.getIsWarning()) {
					return;
				}
				String message;
				if (lastBannerMessage != null && lastBannerMessage.getMessage() != null && !lastBannerMessage.getMessage().isEmpty()) {
					message = lastBannerMessage.getMessage();
				} else if (lastAdvertisingMessage != null) {
					message = lastAdvertisingMessage.getMessage();
				} else {
					return;
				}
				Date expires = new Date();
				long milliseconds = ((long) 7)  * 24 * 60 * 60 * 1000;
				expires.setTime(expires.getTime() + milliseconds);
				Cookies.setCookie(BANNER_COOKIE_NAME, message, expires);
				bannerMessageIsActive = false;
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

		if(Modules.getModule(EsaSkyWebConstants.MODULE_BANNERS_ALL_SIDE)){
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
				BannerMessage bannerMessage;
				bannerMessage = mapper.read(responseText);
				lastBannerMessage = bannerMessage;
				String message = bannerMessage.getMessage();
				if (message == null || message.isEmpty()) {
					if (!showingIsAd) {
						resolveAndHide();
					}
					return;
				}
				if(GUISessionStatus.getShouldHideBannerInfo() && !bannerMessage.getIsWarning()) {
					resolveAndHide();
					return;
				}
				showingIsAd = false;
				maybeSetNewMessage(message, bannerMessage.getIsWarning());
			}
		});

		if (!runningInIframe()) {
			JSONUtils.getJSONFromUrl(EsaSkyWebConstants.ADVERTISING_MESSAGE_URL + "?lang=" + URL.encodeQueryString(GUISessionStatus.getCurrentLanguage()), responseText -> {
                AdvertisingMessageMapper mapper = GWT.create(AdvertisingMessageMapper.class);
				AdvertisingMessage advertisingMessage;
				advertisingMessage = mapper.read(responseText);
                String message = advertisingMessage.getMessage();
				if(lastBannerMessage != null && lastBannerMessage.getMessage() != null && !lastBannerMessage.getMessage().isEmpty()) {
					return;
				}
                if(GUISessionStatus.getShouldHideBannerInfo()) {
                    resolveAndHide();
                    return;
                }
				lastAdvertisingMessage = advertisingMessage;
                if(message != null && !message.isEmpty()) {
					showingIsAd = true;
                    maybeSetNewMessage(advertisingMessage.getMessage(), false);
                } else {
					resolveAndHide();
				}
            });

		}
	}

	private native boolean runningInIframe() /*-{
		try {
			return $wnd.self !== $wnd.top;
		} catch (e) {
			return true;
		}
	}-*/;

	private void maybeSetNewMessage(String message, boolean warning) {
		String bannerCookie = Cookies.getCookie(BANNER_COOKIE_NAME);
		if (bannerCookie != null && bannerCookie.equals(message)) {
			return;
		}
		if(view.getText().equals(new HTML(message).getHTML())) {
			return;
		}
		view.setIsWarning(warning);
		view.setText(message);
		show();
		if(warning) {
			CommonEventBus.getEventBus().fireEvent(new ServerProblemFoundEvent());
		} else {
			CommonEventBus.getEventBus().fireEvent(new ServerProblemSolvedEvent());
		}
		bannerMessageIsActive = true;
	}

	private void resolveAndHide() {
		if(!view.getText().isEmpty()) {
			CommonEventBus.getEventBus().fireEvent(new ServerProblemSolvedEvent());
		}
		bannerMessageIsActive = false;
		view.setText("");
		hide();
	}
}
