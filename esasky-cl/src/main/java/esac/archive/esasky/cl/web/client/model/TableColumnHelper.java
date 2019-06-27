package esac.archive.esasky.cl.web.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public final class TableColumnHelper {

    private TableColumnHelper() {
    }

    public interface Resources extends ClientBundle {

        @Source("send_small.png")
        ImageResource sendToSamp();
        
        @Source("recenter.png")
        ImageResource recenter();

        @Source("download_small.png")
        ImageResource download();

        @Source("preview.png")
        ImageResource previewIcon();
        
        @Source("filter.png")
        ImageResource filterIcon();
        
        @Source("calendar.png")
        ImageResource calendarIcon();
        
        @Source("target_list.png")
        ImageResource targetListIcon();
        
        @Source("loader.gif")
        ImageResource loaderGif();
        
    }

    /** resources Interface. */
    public static final Resources resources = GWT.create(Resources.class);
    /** COLUMN_WIDTH_TEXT_DEFAULT_SIZE. */
    public static final int COLUMN_WIDTH_ICON_DEFAULT_SIZE = 37;
    /** COLUMN_WIDTH_TEXT_DEFAULT_SIZE. */
    public static final int COLUMN_WIDTH_CHECKBOX_DEFAULT_SIZE = 30;

    /** Delay in ms to show the pop up panel when placing the mouse over in ImageResource cells. */
    public static final int TOOLTIP_DELAY_MS = 500;

    /** Format for completing with leading zeros. */
    public static final NumberFormat SCIENTIFIC_NUMBER_FORMAT = NumberFormat.getFormat("0.00E0");

    /** Integer Number format. */
    public static final NumberFormat INTEGER_NUMBER_FORMAT = NumberFormat.getFormat("###0");

    /** Detection significance number format. */
    public static final NumberFormat DETECTION_SIGNIFICANCE_NUMBER_FORMAT = NumberFormat.getFormat("##0.0");

    /** Total counts number format. */
    public static final NumberFormat TOTAL_COUNTS_NUMBER_FORMAT = NumberFormat.getFormat("##0.0");

    /** Count Rate number format. */
    public static final NumberFormat COUNT_RATE_NUMBER_FORMAT = NumberFormat.getFormat("##0.0000");

    /** Hardness ratio number format. */
    public static final NumberFormat HARDNESS_RATIO_NUMBER_FORMAT = NumberFormat.getFormat("0.00");

    /** Poserr number format. */
    public static final NumberFormat POSERR_NUMBER_FORMAT = NumberFormat.getFormat("0.0");
}
