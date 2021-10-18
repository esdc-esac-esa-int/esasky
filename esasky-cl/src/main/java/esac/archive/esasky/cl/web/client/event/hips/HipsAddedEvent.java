package esac.archive.esasky.cl.web.client.event.hips;

import com.google.gwt.event.shared.GwtEvent;
import esac.archive.esasky.ifcs.model.client.HiPS;
import esac.archive.esasky.ifcs.model.client.HipsWavelength;

public class HipsAddedEvent extends GwtEvent<HipsAddedEventHandler> {

    public static final Type<HipsAddedEventHandler> TYPE = new Type<>();

    private HiPS hips;
    private HipsWavelength hipsWavelength;
    private boolean addIfAlreadyExist;

    public HipsAddedEvent(final HiPS inputHips, final HipsWavelength hipsWavelength) {
    	this(inputHips, hipsWavelength, true);
    }

    public HipsAddedEvent(final HiPS inputHips, final HipsWavelength hipsWavelength, final boolean addIfAlreadyExist) {
    	this.hips = inputHips;
    	this.hipsWavelength = hipsWavelength;
    	if(this.hips != null) {
    		this.hips.setHipsWavelength(hipsWavelength);
    	}
    	this.addIfAlreadyExist = addIfAlreadyExist;
    }

    public final HiPS getHiPS() {
        return hips;
    }
    
    public final HipsWavelength getHipsWavelength() {
    	return hipsWavelength;
    }

    public final boolean getAddIfAlreadyExist() {
    	return addIfAlreadyExist;
    }

    @Override
    public final Type<HipsAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final HipsAddedEventHandler handler) {
        handler.onEvent(this);
    }
}
