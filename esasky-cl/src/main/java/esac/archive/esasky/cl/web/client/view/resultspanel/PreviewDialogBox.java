package esac.archive.esasky.cl.web.client.view.resultspanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.AutoHidingMovablePanel;
import esac.archive.esasky.cl.web.client.view.common.LoadingSpinner;
import esac.archive.esasky.cl.web.client.view.common.buttons.CloseButton;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;
import esac.archive.esasky.cl.web.client.CommonEventBus;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPopEvent;
import esac.archive.esasky.cl.web.client.event.ProgressIndicatorPushEvent;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.status.GUISessionStatus;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;

public class PreviewDialogBox extends AutoHidingMovablePanel {
    private final Resources resources = GWT.create(Resources.class);
    private CssResource style;


    public interface Resources extends ClientBundle {
        @Source("previewDialogBox.css")
        @CssResource.NotStrict
        CssResource style();
    }


    private final CloseButton closeButton;
    private final PreviewContentPanel previewContent;
    
    private final Image image;
    private final LoadingSpinner loadingSpinner = new LoadingSpinner(true);
    private int originalImageWidth;
    private int originalImageHeight;
    private boolean fullSizeMode = false;
    private boolean imageHasLoaded = false;
    private Label previewIdLabel;
    
    private int mousePositionX;
    private int mousePositionY;
    private boolean mouseButtonIsPressed = false;
    
    private final int PREVIEW_CONTENT_PADDING = 15;
    private final int MIN_PREVIEW_CONTENT_HEIGHT = 150;
    private final int MIN_PREVIEW_CONTENT_WIDTH = 150;
    

    public class PreviewContentPanel extends FocusPanel{
        public PreviewContentPanel() {
            super();
			addMouseDownHandler(new MouseDownHandler() {
				
				@Override
				public void onMouseDown(MouseDownEvent event) {
					mouseButtonIsPressed = true;
					DOM.setCapture(PreviewContentPanel.this.getElement());
				}
			});
			addMouseUpHandler(new MouseUpHandler() {
				
				@Override
				public void onMouseUp(MouseUpEvent event) {
					mouseButtonIsPressed = false;
					DOM.releaseCapture(PreviewContentPanel.this.getElement());
				}
			});
			addMouseMoveHandler(new MouseMoveHandler() {
				
				@Override
				public void onMouseMove(MouseMoveEvent event) {
					mousePositionX = event.getClientX() - MainLayoutPanel.getMainAreaAbsoluteLeft();
                	mousePositionY = event.getClientY() - MainLayoutPanel.getMainAreaAbsoluteTop();
				}
			});
        }
    }

	private Timer postcardLoadFailedNotificationTimer = new Timer() {

		@Override
		public void run() {
			CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("postcardLoadFailed"));
		}
	};
    
    public PreviewDialogBox(String url, String observationId) {
    	super(GoogleAnalytics.CAT_Preview);
        this.style = this.resources.style();
        this.style.ensureInjected();

        setSnapping(false);

        if(Window.Location.getProtocol().contains("https") && !url.startsWith("https")) {
        	image = new Image(EsaSkyWebConstants.IMAGE_LOADER_URL + "?" + EsaSkyConstants.IMAGELOADER_URL_PARAM + "=" + URL.encodeQueryString(url));
        } else {
        	image = new Image(url);
        }
        image.addStyleName("previewImage");
        image.setVisible(false);

        previewContent = new PreviewContentPanel();
        previewContent.getElement().setId("previewContent");
        previewContent.addStyleName("previewContent");
        previewContent.getElement().getStyle().setPropertyPx("minHeight", MIN_PREVIEW_CONTENT_HEIGHT);
        previewContent.getElement().getStyle().setPropertyPx("minWidth", MIN_PREVIEW_CONTENT_WIDTH);

        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                imageHasLoaded = true;
                MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("Loading preview"));
                image.setVisible(true);
                removeStyleName("displayNone");
                originalImageWidth = image.getWidth();
                originalImageHeight = image.getHeight();
                updateMaxSize();
                
                setFullImageSize();
                setDownScaledImageSize();
                centreDialogBox();
                image.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        if(fullSizeMode) {
                            setDownScaledImageSize();
                        } else {
                            setFullImageSize();
                            ensureDialogFitsInsideWindow();
                        }
                    }
                });
                addResizeHandler();
            }
        });
        
        image.addErrorHandler(new ErrorHandler() {
			
			@Override
			public void onError(ErrorEvent event) {
				Log.debug("Failed to load postcard: " + image.getUrl());
				
                MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
                CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("Loading preview"));
                CommonEventBus.getEventBus().fireEvent(
                		new ProgressIndicatorPushEvent("postcardLoadFailed", TextMgr.getInstance().getText("Preview_postcardLoadFailed"), true));
                hide();
                if(postcardLoadFailedNotificationTimer.isRunning()) {
                	postcardLoadFailedNotificationTimer.run();
                }
                postcardLoadFailedNotificationTimer.schedule(5000);
                GoogleAnalytics.sendEvent(GoogleAnalytics.CAT_Preview, GoogleAnalytics.ACT_Preview_PostcardLoadFailed, "Failed to load postcard: " + image.getUrl());
			}
		});

        loadingSpinner.addStyleName("previewLoadingSpinner");
        MainLayoutPanel.addElementToMainArea(loadingSpinner);
        CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPushEvent("Loading preview", 
        		TextMgr.getInstance().getText("PreviewDialog_loading_postcard"),
        		url));
        
        previewContent.add(image);
        
        closeButton = new CloseButton();
        closeButton.addStyleName("previewCloseButton");
        closeButton.addClickHandler(new ClickHandler() {
        	public void onClick(final ClickEvent event) {
        		hide();
        	}
        });
        
        previewIdLabel = new Label(observationId.replace("_", " "));
        previewIdLabel.setStyleName("previewIdLabel");
        
        FlowPanel contentAndCloseButton = new FlowPanel();
        contentAndCloseButton.add(previewIdLabel);
        contentAndCloseButton.add(closeButton);
        contentAndCloseButton.add(previewContent);
        add(contentAndCloseButton);
        
        addStyleName("previewDialogBox");
    	addStyleName("displayNone");

    	addElementNotAbleToInitiateMoveOperation(previewContent.getElement());
    	show();

        MainLayoutPanel.addMainAreaResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent arg0) {
                updateMaxSize();
            }
        });
    }

    private void setFullImageSize() {
        fullSizeMode = true;
        image.addStyleName("cursorZoomOut");
        image.removeStyleName("cursorZoomIn");
        image.setHeight(originalImageHeight + "px");
        image.setWidth(originalImageWidth + "px");

        previewContent.setHeight(originalImageHeight + PREVIEW_CONTENT_PADDING + "px");
        previewContent.setWidth(originalImageWidth + PREVIEW_CONTENT_PADDING + "px");
        updateMaxSize();
    }

    private void setDownScaledImageSize() {
        fullSizeMode = false;
        image.addStyleName("cursorZoomIn");
        image.removeStyleName("cursorZoomOut");

        int newHeight = MainLayoutPanel.getMainAreaHeight()
        		- GUISessionStatus.getCurrentHeightForExpandedDataPanel() 
        		- getAbsoluteTop()
        		;

        if(newHeight <= MIN_PREVIEW_CONTENT_HEIGHT) {
            newHeight = MIN_PREVIEW_CONTENT_HEIGHT;
        }
        previewContent.setHeight(newHeight + "px");
        previewContent.setWidth("auto");
        
        if(previewContent.getOffsetWidth() > MainLayoutPanel.getMainAreaWidth()) {
        	previewContent.setWidth(MainLayoutPanel.getMainAreaWidth() + "px");
        	previewContent.setHeight("auto");
        	resizeImageToFitPreviewContentWidth();
        } else {
        	resizeImageToFitPreviewContentHeight();
        }
                
        updateMaxSize();
    }

    private void updateMaxSize() {
        setMaxWidth();
        setMaxHeight();

        if(!fullSizeMode) {
            resizeImageToFitPreviewContent();
        }

        centreDialogBox();
    }

    private void centreDialogBox() {
        int leftCenter = ((MainLayoutPanel.getMainAreaWidth() / 2) - (previewContent.getOffsetWidth() / 2));
        if(leftCenter < 0) {
            leftCenter = 0;
        }
        setSuggestedPosition(leftCenter, 35);
    }

    private void resizeImageToFitPreviewContent() {
        if(originalImageHeight == 0) {
            return;
        }
       
        if(mouseButtonIsPressed && 
        		(mouseIsInTopRightDiagonal() || (mouseBelow()  && hasReachedMaxHeight() && imageWidthCanFitInsidePreviewContent()) )) {
        	resizeImageToFitPreviewContentHeight();
        } else if(mouseButtonIsPressed) {
        	resizeImageToFitPreviewContentWidth();
        }
    }
    
    private boolean mouseBelow() {
    	return mousePositionY > image.getAbsoluteTop() + image.getOffsetHeight() - MainLayoutPanel.getMainAreaAbsoluteTop();
    }
    
    private boolean mouseLeft() {
    	return mousePositionX < getAbsoluteLeft() + getOffsetWidth() - MainLayoutPanel.getMainAreaAbsoluteLeft();
    }
    
    private boolean imageWidthCanFitInsidePreviewContent() {
    	return (double)calculateNewHeight() * (double) originalImageWidth / (double) originalImageHeight < calculateNewWidth();
    }
    
    private boolean hasReachedMaxHeight() {
    	return calculateNewHeight() >= MainLayoutPanel.getMainAreaHeight() - image.getAbsoluteTop() + MainLayoutPanel.getMainAreaAbsoluteTop() - PREVIEW_CONTENT_PADDING;
    }
    
    private void resizeImageToFitPreviewContentWidth() {
    	image.setWidth(calculateNewWidth() + "px");
    	image.setHeight("auto");
    	setMaxWidth();
    	setMaxHeight(image.getOffsetHeight() + PREVIEW_CONTENT_PADDING);
    }
    
    private void resizeImageToFitPreviewContentHeight() {
        image.setHeight(calculateNewHeight() + "px");
        image.setWidth("auto");
        setMaxHeight();
        setMaxWidth(image.getOffsetWidth() + PREVIEW_CONTENT_PADDING);
    }
    
    private void setMaxHeight() {
    	setMaxHeight(originalImageHeight + PREVIEW_CONTENT_PADDING);
    }
    
    private void setMaxWidth() {
    	setMaxWidth(originalImageWidth + PREVIEW_CONTENT_PADDING);
    }

    private void setMaxWidth(int width) {
    	if(width > MainLayoutPanel.getMainAreaWidth()) {
    		width = MainLayoutPanel.getMainAreaWidth() - PREVIEW_CONTENT_PADDING;
    	}
    	previewContent.getElement().getStyle().setPropertyPx("maxWidth", width);
    }
    
    private void setMaxHeight(int height) {
    	if(height > MainLayoutPanel.getMainAreaHeight() - 30 - 2 - previewIdLabel.getOffsetHeight()) {
    		height = MainLayoutPanel.getMainAreaHeight() - 30 - 2 - previewIdLabel.getOffsetHeight();
    	}
    	previewContent.getElement().getStyle().setPropertyPx("maxHeight", height);
    }
    
    private boolean mouseIsInTopRightDiagonal() {
    	double ratio = (double) originalImageWidth / (double) originalImageHeight;
    	double yWhenXIsZero = getYWhenXisZero();
    	return mousePositionY - yWhenXIsZero < (mousePositionX / ratio);
    }
    
    private double getYWhenXisZero() {
    	double ratio = (double) originalImageWidth / (double) originalImageHeight;
    	return (double)(image.getAbsoluteTop()) - (double)(image.getAbsoluteLeft() - MainLayoutPanel.getMainAreaAbsoluteLeft()) / ratio - MainLayoutPanel.getMainAreaAbsoluteTop();
    }
    
    private int calculateNewHeight() {
    	int newHeight = previewContent.getOffsetHeight();
    	String previewContentHeight = previewContent.getElement().getStyle().getHeight();
    	if(!previewContentHeight.equals("") && !previewContentHeight.equals("auto")) {
    		newHeight = new Integer(previewContentHeight.replace("px", ""));
    		newHeight = Math.min(newHeight, MainLayoutPanel.getMainAreaHeight() - image.getAbsoluteTop() + MainLayoutPanel.getMainAreaAbsoluteTop());
    	}
    	if(newHeight > originalImageHeight) {
    		return originalImageHeight;
    	}
    	if(newHeight < MIN_PREVIEW_CONTENT_HEIGHT) {
    		return MIN_PREVIEW_CONTENT_HEIGHT;
    	}
    	newHeight -= PREVIEW_CONTENT_PADDING;

    	return newHeight;
    }
    
    private int calculateNewWidth() {
    	int newWidth = previewContent.getOffsetWidth();
    	String previewContentWidth = previewContent.getElement().getStyle().getWidth();
    	if(!previewContentWidth.equals("") && !previewContentWidth.equals("auto")) {
    		newWidth = new Integer(previewContentWidth.replace("px", ""));
    		newWidth = Math.min(newWidth, MainLayoutPanel.getMainAreaWidth() - image.getAbsoluteLeft() + MainLayoutPanel.getMainAreaAbsoluteLeft());
    	}
    	if(newWidth > originalImageWidth) {
    		return originalImageWidth;
    	}	
    	if(newWidth < MIN_PREVIEW_CONTENT_WIDTH) {
    		return MIN_PREVIEW_CONTENT_WIDTH;
    	}
    	newWidth -= PREVIEW_CONTENT_PADDING ;
    	return newWidth;
    }
    
	private native void addResizeHandler() /*-{
		var previewDialogBox = this;
		new $wnd.ResizeSensor($doc.getElementById('previewContent'), function() {
    		previewDialogBox.@esac.archive.esasky.cl.web.client.view.resultspanel.PreviewDialogBox::onResizeFromUser()();
		});
	}-*/; 
	
	private Timer resizeTimer = new Timer() {
		
		@Override
		public void run() {
			resizeImageToFitPreviewContent();
		}
	};

	public void onResizeFromUser() {
		definePositionFromTopAndLeft();
		if(!fullSizeMode) {
			if(!resizeTimer.isRunning()) {
				resizeTimer.schedule(5);
			}
		}
	}
	
	public void hide() {
		if(!imageHasLoaded) {
			MainLayoutPanel.removeElementFromMainArea(loadingSpinner);
			CommonEventBus.getEventBus().fireEvent(new ProgressIndicatorPopEvent("Loading preview"));
		}
		super.hide();
	}

}
