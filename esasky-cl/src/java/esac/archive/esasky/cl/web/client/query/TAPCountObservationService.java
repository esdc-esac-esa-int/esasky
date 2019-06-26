package esac.archive.esasky.cl.web.client.query;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.ammi.ifcs.model.descriptor.IDescriptor;

public class TAPCountObservationService extends AbstractCountService<IDescriptor> {

    private static TAPCountObservationService instance = null;

    private TAPCountObservationService() {
    }

    public static TAPCountObservationService getInstance() {
        if (instance == null) {
            instance = new TAPCountObservationService();
        }
        return instance;
    }

    /**
     * getCount4ObservationURL().
     * @param obsDescriptor Input ObservationDescriptor.
     * @param aladinLite Input AladinLiteWidget
     * @return String
     */
    @Override
    public String getCount(AladinLiteWidget aladinLite, IDescriptor descriptor) {
        return super.getDynamicCountQuery(aladinLite, descriptor.getTapTable());
    }
}
