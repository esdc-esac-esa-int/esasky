package esac.archive.ammi.ifcs.model.descriptor;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class SpectraDescriptor extends CommonObservationDescriptor {

	@Override
	public String generateId() {
		 return getMission() + " Spectra " + generateNextTabCount();
	}
}
