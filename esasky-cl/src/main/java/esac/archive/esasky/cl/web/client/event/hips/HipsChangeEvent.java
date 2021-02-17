package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.absi.modules.cl.aladinlite.widget.client.model.ColorPalette;
// import esac.archive.esasky.ifcs.model.client.ColorPalette;
import esac.archive.esasky.ifcs.model.client.HiPS;

/**
 * Event to record closing results event tab sent by Tab components.
 */
public class HipsChangeEvent extends GwtEvent<HipsChangeEventHandler> {

    /** Event type. */
    public final static Type<HipsChangeEventHandler> TYPE = new Type<HipsChangeEventHandler>();

    private HiPS hips;
    private ColorPalette colorPalette;
    private boolean isBaseImage;
    private double opacity;

    public HipsChangeEvent(final HiPS inputHips, final ColorPalette colorPalette, final boolean isBaseImage, final double opacity) {
        this.hips = inputHips;
        this.colorPalette = colorPalette;
        this.isBaseImage = isBaseImage;
        this.opacity = opacity;
    }

    public final HiPS getHiPS() {
        return hips;
    }
    
    public final boolean isBaseImage() {
    	return isBaseImage;
    }

    public final ColorPalette getColorPalette() {
        return colorPalette;
    }
    
    public final double getOpacity() {
    	return opacity;
    }

    @Override
    public final Type<HipsChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsChangeEventHandler handler) {
        handler.onChangeEvent(this);
    }
}
