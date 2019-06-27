package esac.archive.esasky.cl.web.client.event.sso;

import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.ifcs.model.shared.ESASkySSOSearchResult.ESASkySSOObjType;

public class SSOCrossMatchEvent extends GwtEvent<SSOCrossMatchEventHandler> {

    /** Event type. */
    public static Type<SSOCrossMatchEventHandler> TYPE = new Type<SSOCrossMatchEventHandler>();

    String ssoName;
    ESASkySSOObjType ssoType;

    /**
     * Constructor class.
     * @param inputCount Input integer
     * @param inputEntity Input Entity
     */
    public SSOCrossMatchEvent(String ssoName, ESASkySSOObjType ssoType) {
        super();
        this.ssoName = ssoName;
        this.ssoType = ssoType;

    }

    @Override
    public final Type<SSOCrossMatchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final SSOCrossMatchEventHandler handler) {
        handler.newSsoSelected(this);
    }

    public String getSsoName() {
        return ssoName;
    }

    public ESASkySSOObjType getSsoType() {
        return ssoType;
    }

}
