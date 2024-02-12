package esac.archive.esasky.cl.web.client.login;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import esac.archive.esasky.cl.web.client.event.ErrorEvent;
import esac.archive.esasky.cl.web.client.event.ErrorEventHandler;
import esac.archive.esasky.cl.web.client.internationalization.TextMgr;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.cl.web.client.view.MainLayoutPanel;
import esac.archive.esasky.cl.web.client.view.common.BaseMovablePopupPanel;
import esac.archive.esasky.cl.web.client.view.common.buttons.EsaSkyStringButton;

public class UploadTablePopupPanel extends BaseMovablePopupPanel {
    private TextBox tableNameTextBox;
    private VerticalPanel formContainer;
    private FormPanel formPanel;
    private static final String ALLOWED_EXTENSIONS = "xml|vot|votable|csv";
    private static final String ALLOWED_EXTENSIONS_MIME =
            "application/xml,text/xml," +  // MIME types for XML
            "text/csv,text/plain," +        // MIME types for CSV
            "application/x-votable+xml" +    // MIME type for VOTable
            ".xml,.vot,.votable,.csv";       // To be on the safe side, also define extensions


    public UploadTablePopupPanel() {
        super(GoogleAnalytics.CAT_USERAREA, TextMgr.getInstance().getText("userArea__uploadTablePopup_title"), TextMgr.getInstance().getText("userArea__uploadTablePopup_help"));
        initView();
    }

    public void initView() {

        formContainer = new VerticalPanel();

        formPanel = new FormPanel();
        formPanel.setAction(EsaSkyWebConstants.UPLOAD_TABLE_URL);
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.getElement().getStyle().setProperty("zIndex", "100");
        formPanel.add(formContainer);

        Hidden fileFormat = new Hidden();
        fileFormat.setName("FORMAT");

        VerticalPanel uploadContainer = new VerticalPanel();
        uploadContainer.getElement().getStyle().setProperty("border", "1px solid grey");
        uploadContainer.getElement().getStyle().setProperty("borderRadius", "10px");
        uploadContainer.getElement().getStyle().setProperty("width", "100%");
        uploadContainer.getElement().getStyle().setProperty("padding", "5px");
        uploadContainer.setSpacing(10);
        Label uploadLabel = new Label("Select Table");
        FileUpload fileUpload = new FileUpload();
        fileUpload.getElement().setAttribute("accept", ALLOWED_EXTENSIONS_MIME);
        fileUpload.setName("FILE");
        fileUpload.addChangeHandler(event -> {
            String fileName = fileUpload.getFilename();
            if (fileName != null) {
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                fileFormat.setValue(fileExtension);
            }
        });

        uploadContainer.add(uploadLabel);
        uploadContainer.add(fileUpload);
        formContainer.add(uploadContainer);

        VerticalPanel tableNameContainer = new VerticalPanel();
        tableNameContainer.getElement().getStyle().setProperty("border", "1px solid grey");
        tableNameContainer.getElement().getStyle().setProperty("borderRadius", "10px");
        tableNameContainer.getElement().getStyle().setProperty("width", "100%");
        tableNameContainer.getElement().getStyle().setProperty("padding", "5px");
        tableNameContainer.setSpacing(10);
        Label tableNameLabel = new Label("Table Name");
        tableNameTextBox = new TextBox();
        tableNameTextBox.setName("TABLE_NAME");
        tableNameTextBox.setStyleName("tableName__textBox");
        tableNameTextBox.getElement().setPropertyString("placeholder", "my_table_name");
        tableNameTextBox.addKeyPressHandler(event -> {
            char charPressed = event.getCharCode();

            // Convert the character to lowercase
            char charLowercase = Character.toLowerCase(charPressed);

            if (!Character.isLetterOrDigit(charPressed) && charPressed != '_') {
                tableNameTextBox.cancelKey();
            } else if (charPressed != charLowercase) {
                tableNameTextBox.cancelKey();
                tableNameTextBox.setValue(tableNameTextBox.getValue() + charLowercase);
            }
        });

        tableNameTextBox.addValueChangeHandler(event -> {
            // Convert the text to lowercase
            String lowercaseText = event.getValue().toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");


            // Set the lowercase text back to the TextArea
            tableNameTextBox.setValue(lowercaseText, true); // true to fire another ValueChangeEvent

        });

        tableNameContainer.add(tableNameLabel);
        tableNameContainer.add(tableNameTextBox);
        tableNameContainer.add(fileFormat);
        formContainer.add(tableNameContainer);

        formContainer.setSpacing(20);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.getElement().getStyle().setProperty("margin", "0 auto");
        buttonPanel.setSpacing(10);
        formContainer.add(buttonPanel);

        EsaSkyStringButton uploadButton = new EsaSkyStringButton("Upload");

        EsaSkyStringButton closeButton = new EsaSkyStringButton("Close");
        buttonPanel.add(uploadButton);
        buttonPanel.add(closeButton);

        uploadButton.addClickHandler(event -> {
            String filename = fileUpload.getFilename();
            if (!filename.isEmpty() && tableNameTextBox.getText().length() > 3) {
                String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
                if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                    fireEvent(new ErrorEvent("Incorrect file type provided.", "Only [" + ALLOWED_EXTENSIONS.replace('|', ',') + "] files are allowed"));
                } else {
                    formPanel.submit();
                }
            } else
                fireEvent(new ErrorEvent("No file or table name provided", "Table names must be a minimum of four (4) characters long"));
        });

        closeButton.addClickHandler(event -> hide());

        container.add(formPanel);

    }

    public void addSubmitHandler(FormPanel.SubmitHandler handler) {
        formPanel.addSubmitHandler(handler);
    }

    public void addSubmitCompleteHandler(FormPanel.SubmitCompleteHandler handler) {
        formPanel.addSubmitCompleteHandler(handler);
    }

    public HandlerRegistration addErrorHandler(ErrorEventHandler handler) {
        return addHandler(handler, ErrorEvent.TYPE);
    }

    @Override
    public void setMaxSize() {
        getElement().getStyle().setPropertyPx("maxHeight", 400);
        getElement().getStyle().setPropertyPx("maxWidth", 320);
    }

}
