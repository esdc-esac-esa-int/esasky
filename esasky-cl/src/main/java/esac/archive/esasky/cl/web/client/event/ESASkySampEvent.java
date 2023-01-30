package esac.archive.esasky.cl.web.client.event;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;

import esac.archive.esasky.cl.web.client.utility.SampConstants.SampAction;

/**
 * Event involved in the handler to distribute files via SAMP (copied from Gaia).
 *
 * @author María H Sarmiento - Copyright (c) 2015 - ESA/ESAC.
 */
public class ESASkySampEvent extends GwtEvent<ESASkySampEventHandler> {

    /** Defining event type. */
    public static final Type<ESASkySampEventHandler> TYPE = new Type<>();

    /** Samp action: register, load.votable... */
    private SampAction action;

    /**
     * /** List of Urls to be sent to SAMP.
     */
    private Map<String, String> sampUrlsPerMissionMap;

    /**
     * Default constructor.
     * @param inputAction Input SampAction
     * @param inputSampUrlsPerMissionMap Input HashMap<String, String>
     */
    public ESASkySampEvent(final SampAction inputAction, final Map<String, String> inputSampUrlsPerMissionMap) {
        super();
        setAction(inputAction);
        setSampUrlsPerMissionMap(inputSampUrlsPerMissionMap);
    }

    /**
     * Default constructor.
     * @param inputAction Input SampAction
     * @param inputResourceUrl Input String
     * @param inputTableName Input mission name
     */
    public ESASkySampEvent(final SampAction inputAction, final String inputResourceUrl,
            final String inputTableName) {
        super();
        setAction(inputAction);
        this.sampUrlsPerMissionMap = new HashMap<>();
        this.sampUrlsPerMissionMap.put(inputTableName, inputResourceUrl);

    }

    @Override
    public final Type<ESASkySampEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected final void dispatch(final ESASkySampEventHandler handler) {
        Log.debug("[ESASkySampEvent] Into dispatch sampEvent... ");
        handler.onEvent(this);
    }

    /**
     * getAction().
     * @return the action
     */
    public final SampAction getAction() {
        return action;
    }

    /**
     * setAction().
     * @param inputAction the action to set.
     */
    public final void setAction(final SampAction inputAction) {
        this.action = inputAction;
    }

    /**
     * getSampUrlsPerMissionMap().
     * @return the sampUrlsPerMissionMap
     */
    public final Map<String, String> getSampUrlsPerMissionMap() {
        return sampUrlsPerMissionMap;
    }

    /**
     * setSampUrlsPerMissionMap().
     * @param inputSampUrlsPerMissionMap the sampUrlsPerMissionMap to set
     */
    public final void setSampUrlsPerMissionMap(final Map<String, String> inputSampUrlsPerMissionMap) {
        this.sampUrlsPerMissionMap = inputSampUrlsPerMissionMap;
    }

}
