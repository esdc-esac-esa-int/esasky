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
    public static Type<HipsChangeEventHandler> TYPE = new Type<HipsChangeEventHandler>();

    private HiPS hips;
    private ColorPalette colorPalette;

    public HipsChangeEvent(final HiPS inputHips, final ColorPalette colorPalette) {
        this.hips = inputHips;
        this.colorPalette = colorPalette;
    }

    public final HiPS getHiPS() {
        return hips;
    }

    public final ColorPalette getColorPalette() {
        return colorPalette;
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
