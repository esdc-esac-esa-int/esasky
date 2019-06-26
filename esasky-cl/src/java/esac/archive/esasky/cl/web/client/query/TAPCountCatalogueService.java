package esac.archive.esasky.cl.web.client.query;

import esac.archive.absi.modules.cl.aladinlite.widget.client.AladinLiteWidget;
import esac.archive.ammi.ifcs.model.descriptor.CatalogDescriptor;

public class TAPCountCatalogueService extends AbstractCountService<CatalogDescriptor> {

    private static TAPCountCatalogueService instance = null;

    private TAPCountCatalogueService() {
    }

    public static TAPCountCatalogueService getInstance() {
        if (instance == null) {
            instance = new TAPCountCatalogueService();
        }
        return instance;
    }

    /**
     * getCount4CatalogueURL().
     * @param descriptor Input CatalogDescriptor.
     * @param aladinLite Input instance to AladinLiteWidget.
     * @return String
     */
    @Override
    public String getCount(AladinLiteWidget aladinLite, CatalogDescriptor descriptor) {
        return super.getDynamicCountQuery(aladinLite, descriptor.getTapTable());
    }

}
