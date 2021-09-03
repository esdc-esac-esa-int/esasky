package esac.archive.esasky.cl.web.client.view.resultspanel;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.callback.StandardCallback;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.JSONUtils;

public class DatalinkDownloadDialogBox extends AutoHidingMovablePanel {
	private final Resources resources = GWT.create(Resources.class);
	private CssResource style;

	public interface Resources extends ClientBundle {
		@Source("datalinkDialogBox.css")
		@CssResource.NotStrict
		CssResource style();
	}

	private final CloseButton closeButton;

	private final LoadingSpinner loadingSpinner = new LoadingSpinner(true);
	private Label datalinkIdLabel;
	private final FlowPanel datalinkContent = new FlowPanel();

	private Timer datalinkLoadFailedNotificationTimer = new Timer() {

		@Override
		public void run() {
			CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("datalinkLoadFailed"));
		}
	};

	public DatalinkDownloadDialogBox(final String url, String title) {
		super(GoogleAnalytics.CAT_DATALINK);
		
		if(title == null || title.isEmpty()) {
			title = "Datalink";
		}
		final String observationId = title;
		
		this.style = this.resources.style();
		this.style.ensureInjected();

		JSONUtils.getJSONFromUrl(EsaSkyWebConstants.DATALINK_URL + "?url=" + URL.encodeQueryString(url),
				new StandardCallback("Datalink", TextMgr.getInstance().getText("datalinkDownload_loading"), new StandardCallback.OnComplete() {

					@Override
					public void onComplete(String responseText) {
						MainLayoutPanel.removeElementFromMainArea(loadingSpinner);

						DatalinkJson json = JsonUtils.safeEval(responseText);
						List<DatalinkLinks> listOfDatalinkLinks = new LinkedList<DatalinkLinks>();
						removeStyleName("displayNone");
						for (String[] data : json.getData()) {
							listOfDatalinkLinks.add(new DatalinkLinks(data, json.getMetadata()));
						}
						boolean lastItemWasBright = false;
						for (final DatalinkLinks links : listOfDatalinkLinks) {
							
							FlowPanel container = new FlowPanel();
							container.addStyleName("datalinklinkContainer");
							if (!links.getErrorMessage().isEmpty()) {
								Label label = new Label(links.getErrorMessage());
								label.addStyleName("datalinkError");
								datalinkContent.add(label);
								continue;
							}
							if (!links.getServiceDef().isEmpty()) {
								//TODO handle service_def 
//								Anchor anchor = new Anchor(links.service_def, "#", "_blank");
//								datalinkContent.add(anchor);
								continue;
							}
							else if (!links.getAccessUrl().isEmpty()) {
								String anchorName = "Download";
								if (!links.getDescription().isEmpty()) {
									anchorName = links.getDescription();
								}
								if (!links.getContentType().isEmpty()) {
									anchorName += links.getTypeAndSizeDisplayText();
								}
								Anchor anchor = new Anchor(anchorName, links.getAccessUrl(), "_blank");
								if(anchorName.toLowerCase().contains("datalink")
								        || anchorName.toLowerCase().contains(TextMgr.getInstance().getText("datalink_linkedProducts").toLowerCase())) {
									anchor.setTarget("");
									anchor.setHref("javascript:;");
									anchor.addClickHandler(new ClickHandler() {
										
										@Override
										public void onClick(ClickEvent event) {
											new DatalinkDownloadDialogBox(links.getAccessUrl(), observationId);
										}
									});
								}
								anchor.addClickHandler(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DATALINK, "Download", links.getAccessUrl());
									}
								});
								anchor.addStyleName("datalinkAnchor");
								container.add(anchor);
								datalinkContent.add(container);
								if (links.getSemantics().equalsIgnoreCase("#this")) {
									anchor.addStyleName("datalinkAnchor__main");
								}
								if (!links.getSemantics().isEmpty()) {
//								Label label = new Label(links.semantics);
//								datalinkContent.add(label);
								}
								container.getElement().getStyle().setBorderColor("white");
								for (String otherInfo : links.getOthers()) {
									if (otherInfo.toLowerCase().contains("eso_datalink")
											|| otherInfo.toLowerCase().contains("readable: ")) {
										continue;
									}
									otherInfo = otherInfo.replaceAll("(_|\\.)", " ");
									otherInfo = otherInfo.replaceAll("eso ", "ESO ");
									Label label = new Label(otherInfo);
									label.addStyleName("datalinkOther");
									container.add(label);
								}
								
								if(lastItemWasBright) {
									container.addStyleName("datalinklinkContainer__dark");
									lastItemWasBright = false;
								} else {
									container.addStyleName("datalinklinkContainer__bright");
									lastItemWasBright = true;
								}
							} 
						}
						setMaxHeight(MainLayoutPanel.getMainAreaHeight());
						setSuggestedPositionCenter();

					}
				}, new StandardCallback.OnFailure() {

					@Override
					public void onFailure() {
						Log.debug("Failed to load datalink: " + url);
						
		                MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
		                CommonEventBus.getEventBus().fireEvent(
		                		new ProgressIndicatorPushEvent("datalinkLoadFailed", TextMgr.getInstance().getText("datalink_loadFailed"), true));
		                hide();
		                if(datalinkLoadFailedNotificationTimer.isRunning()) {
		                	datalinkLoadFailedNotificationTimer.run();
		                }
		                datalinkLoadFailedNotificationTimer.schedule(5000);
		                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_DATALINK, GoogleAnalytics.ACT_DATALINK_LOADFAILED, "Failed to load datalink: " + url);

					}
				}));

		datalinkContent.getElement().setId("datalinkContent");
		datalinkContent.addStyleName("datalinkContent");

        loadingSpinner.addStyleName("datalinkLoadingSpinner");
        MainLayoutPanel.addElementToMainArea(loadingSpinner);

		closeButton = new CloseButton();
		closeButton.addStyleName("datalinkCloseButton");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
			}
		});


		datalinkIdLabel = new Label(observationId.replace("_", " "));
		datalinkIdLabel.setStyleName("datalinkIdLabel");

		FlowPanel contentAndCloseButton = new FlowPanel();
		contentAndCloseButton.add(datalinkIdLabel);
		contentAndCloseButton.add(closeButton);
		contentAndCloseButton.add(datalinkContent);
		add(contentAndCloseButton);

		addStyleName("datalinkDialogBox");
		addStyleName("displayNone");

		addElementNotAbleToInitiateMoveOperation(datalinkContent.getElement());
		show();

		MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent arg0) {
				setMaxHeight(arg0.getHeight());
			}
		});
	}

	@Override
	public void hide() {
		MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
		CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("Loading datalink"));
		super.hide();
	}

	private void setMaxHeight(int height) {
		if (height > MainLayoutPanel.getMainAreaHeight() - 30 - 2 - datalinkIdLabel.getOffsetHeight()) {
			height = MainLayoutPanel.getMainAreaHeight() - 30 - 2 - datalinkIdLabel.getOffsetHeight();
		}
		datalinkContent.getElement().getStyle().setPropertyPx("maxHeight", height);
	}
	
}
