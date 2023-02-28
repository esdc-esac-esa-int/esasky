package esac.archive.esasky.ifcs.model.descriptor;

import java.util.LinkedList;
import java.util.List;

public class SSODescriptor extends CommonObservationDescriptor {

	private List<MetadataDescriptor> ssoXMatchMetadata = new LinkedList<>();
    private String ssoCardReductionTapTable;

    private String ssoXMatchTapTable;
	
	@Override
	public final void setMetadata(final List<MetadataDescriptor> inputMetadata) {
		metadata = inputMetadata;
		metadata.addAll(ssoXMatchMetadata);
	}
	
    public final List<MetadataDescriptor> getSsoXMatchMetadata() {
        return ssoXMatchMetadata;
    }

    public final void setSsoXMatchMetadata(final List<MetadataDescriptor> inputMetadata) {
        this.ssoXMatchMetadata = inputMetadata;
    }

    @Override
    public String getIcon() {
        return "sso";
    }
    
    @Override
    public String getDescriptorId() {
        if(descriptorId == null || descriptorId.isEmpty()) {
            return "SSO_" + getMission();
        }
        return descriptorId;
    }
    
    public String getSsoCardReductionTapTable() {
        return ssoCardReductionTapTable;
    }

    public void setSsoCardReductionTapTable(String ssoCardReductionTapTable) {
        this.ssoCardReductionTapTable = ssoCardReductionTapTable;
    }

    public String getSsoXMatchTapTable() {
        return ssoXMatchTapTable;
    }

    public void setSsoXMatchTapTable(String ssoXMatchTapTable) {
        this.ssoXMatchTapTable = ssoXMatchTapTable;
    }
}
