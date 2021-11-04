package esac.archive.esasky.cl.web.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import esac.archive.esasky.cl.web.client.api.Api;
import esac.archive.esasky.cl.web.client.api.ApiConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.utility.UrlUtils;
import esac.archive.esasky.cl.web.client.view.common.MovablePanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class JupyterDownloadDialog extends Composite {

    private String downloadUrl;

    public final JupyterDownloadDialog.Resources resources = GWT.create(JupyterDownloadDialog.Resources.class);
    private final CssResource style;

    private static final int DIALOG_WIDTH = 475;

    private final FlowPanel downloadButtonContainer;
    private final EsaSkyStringButton jupyterButton;
    private final EsaSkyStringButton computerButton;

    public interface Resources extends ClientBundle {

        @Source("downloadDialog.css")
        @CssResource.NotStrict
        CssResource style();
    }

    public JupyterDownloadDialog() {

        this.style = this.resources.style();
        this.style.ensureInjected();

        Label title = new Label("Download file");
        title.addStyleName("downloadTitle");

        Label descriptionText = new Label("Choose where to download");
        descriptionText.addStyleName("downloadDescription");

        final MovablePanel downloadDialogContainer = new MovablePanel(GoogleAnalytics.CAT_DOWNLOADDIALOG, true);
        downloadDialogContainer.addHideOnEscapeKeyBehavior(this::close);
        downloadDialogContainer.add(title);
        downloadDialogContainer.add(descriptionText);

        jupyterButton = new EsaSkyStringButton("Jupyter");
        jupyterButton.setMediumStyle();
        jupyterButton.addStyleName("downloadButton");
        jupyterButton.addClickHandler(event -> {
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                JSONObject msg = new JSONObject();
                msg.put("type", new JSONString(ApiConstants.JUPYTER_ACTION_DOWNLOAD));
                msg.put("url", new JSONString(downloadUrl));
                Api.getInstance().expediteMessageToWidget(msg);
                close();
            }
        });

        computerButton = new EsaSkyStringButton("Computer");
        computerButton.setMediumStyle();
        computerButton.addStyleName("downloadButton");
        computerButton.addClickHandler(event -> {
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                close();
                UrlUtils.openUrl(downloadUrl);
            }
        });

        downloadButtonContainer = new FlowPanel();
        downloadButtonContainer.addStyleName("downloadButtonContainer");
        downloadButtonContainer.add(jupyterButton);
        downloadButtonContainer.add(computerButton);

        downloadDialogContainer.add(downloadButtonContainer);


        downloadDialogContainer.add(createClosingButtons());

        initWidget(downloadDialogContainer);
        getElement().getStyle().setWidth(DIALOG_WIDTH, Unit.PX);
        addStyleName("downloadDialogBox");

        MainLayoutPanel.addMainAreaResizeHandler(event -> {
            setMaxSize();
            setButtonPositions();
        });
    }


    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    @Override
    protected void onLoad() {
        super.onLoad();
        setButtonPositions();
    }

    private Widget createClosingButtons() {
        EsaSkyStringButton closeButton = new EsaSkyStringButton("Close");
        closeButton.setMediumStyle();
        closeButton.addClickHandler(event -> close());

        return closeButton;
    }

    private void close() {
        MainLayoutPanel.removeElementFromMainArea(this);
    }

    private void setButtonPositions() {
        if (DIALOG_WIDTH - 100 >= getMaxPossibleWidth()) {
            setSmallScreenButtonLayout();
        } else {
            setDefaultButtonLayout();
        }
    }

    private void setSmallScreenButtonLayout() {
        downloadButtonContainer.getElement().getStyle().setDisplay(Display.BLOCK);
        jupyterButton.getElement().getStyle().setProperty("display", "flex");
        computerButton.getElement().getStyle().setProperty("display", "flex");
    }

    private void setDefaultButtonLayout() {
        downloadButtonContainer.getElement().getStyle().setProperty("display", "inline-flex");
        jupyterButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        computerButton.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

    private void setMaxSize() {
        getElement().getStyle().setPropertyPx("maxWidth", getMaxPossibleWidth());
    }

    private int getMaxPossibleWidth() {
        return MainLayoutPanel.getMainAreaWidth();
    }


    public void show() {
        setMaxSize();
        setButtonPositions();
        MainLayoutPanel.addElementToMainArea(this);
    }
}
