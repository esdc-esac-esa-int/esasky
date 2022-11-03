//package esac.archive.esasky.ifcs.model.descriptor;
//
//import com.github.nmorel.gwtjackson.client.ObjectMapper;
//import com.google.gwt.core.client.GWT;
//
//import java.util.List;
//
//public class WavelengthRangeDescriptorList extends TapDescriptorList<WavelengthRangeDescriptor>{
//
//    public interface Mapper extends ObjectMapper<WavelengthRangeDescriptor> { }
//
//    @Override
//    public List<WavelengthRangeDescriptor> getDescriptors() {
//        if (descriptors == null) {
//            Mapper mapper = GWT.create(Mapper.class);
//            return createDescriptors(mapper);
//        } else {
//            return this.descriptors;
//        }
//    }
//}
