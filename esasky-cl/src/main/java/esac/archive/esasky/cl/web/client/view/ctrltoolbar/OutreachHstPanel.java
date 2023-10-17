package esac.archive.esasky.cl.web.client.view.ctrltoolbar;

import esac.archive.esasky.cl.web.client.repository.DescriptorRepository;
import esac.archive.esasky.cl.web.client.utility.EsaSkyWebConstants;
import esac.archive.esasky.cl.web.client.utility.GoogleAnalytics;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;
import esac.archive.esasky.ifcs.model.shared.EsaSkyConstants;

public class OutreachHstPanel extends OutreachImagePanel {

    public OutreachHstPanel() {
        super(EsaSkyConstants.HST_MISSION, GoogleAnalytics.CAT_OUTREACHIMAGES);
    }

    @Override
    protected CommonTapDescriptor getOutreachImageDescriptor() {
        return DescriptorRepository.getInstance().getFirstDescriptor(EsaSkyWebConstants.CATEGORY_IMAGES, EsaSkyConstants.HST_MISSION);
    }
}
