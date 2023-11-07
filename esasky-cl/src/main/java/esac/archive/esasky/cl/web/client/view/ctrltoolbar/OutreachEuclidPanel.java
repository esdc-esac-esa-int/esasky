package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class OutreachEuclidPanel extends OutreachImagePanel {
    public OutreachEuclidPanel() {
        super(EsaSkyConstants.EUCLID_MISSION, GoogleAnalytics.CAT_EUCLIDOUTREACHIMAGES);
    }

    @Override
    protected CommonTapDescriptor getOutreachImageDescriptor() {
        return DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_IMAGES, EsaSkyConstants.EUCLID_MISSION);
    }
}
