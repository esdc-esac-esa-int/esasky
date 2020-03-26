package esac.archive.esasky.ifcs.model.descriptor;

import java.util.LinkedList;
import java.util.List;

public class SSODescriptor extends CommonObservationDescriptor {

	private List<MetadataDescriptor> ssoXMatchMetadata = new LinkedList<MetadataDescriptor>();
//	private String tapObservationId;
//	private String ssoCardReductionTapTable;
//    private String ssoXMatchTapTable;
	
	@Override
	public final void setMetadata(final List<MetadataDescriptor> inputMetadata) {
		metadata = inputMetadata;
		metadata.addAll(ssoXMatchMetadata);
	}
//	@Override
//	public final List<MetadataDescriptor> getMetadata() {
//		LinkedList<MetadataDescriptor> combinationOfObservationAndSsoMetadata = new LinkedList<MetadataDescriptor>(metadata);
//		combinationOfObservationAndSsoMetadata.addAll(ssoXMatchMetadata);
//		return combinationOfObservationAndSsoMetadata;
//	}
	
//	public final List<MetadataDescriptor> getObservationMetadata() {
//		return metadata;
//	}
	
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
    
//    public String getUniqueIdentifierField(){
//    	return tapObservationId;
//    }
//    
//    public void setUniqueIdentifierField(String field){
//    	tapObservationId = field;
//    }
//	
//    public final String getTapObservationId() {
//        return tapObservationId;
//    }
//
//    public final void setTapObservationId(final String inputTapObservationId) {
//        this.tapObservationId = inputTapObservationId;
//    }
//    
//    public String getSsoCardReductionTapTable() {
//        return ssoCardReductionTapTable;
//    }
//
//    public void setSsoCardReductionTapTable(String ssoCardReductionTapTable) {
//        this.ssoCardReductionTapTable = ssoCardReductionTapTable;
//    }
//
//    public String getSsoXMatchTapTable() {
//        return ssoXMatchTapTable;
//    }
//
//    public void setSsoXMatchTapTable(String ssoXMatchTapTable) {
//        this.ssoXMatchTapTable = ssoXMatchTapTable;
//    }
}
