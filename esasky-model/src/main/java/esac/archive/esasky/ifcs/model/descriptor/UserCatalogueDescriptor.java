package esac.archive.esasky.ifcs.model.descriptor;

/**
 * @author ESDC team Copyright (c) 2017 - European Space Agency
 */
public class UserCatalogueDescriptor extends CatalogDescriptor {

	private static int tabNumber = 0;
	
	@Override
	public String generateId() {
		 return getMission() + " UC " + generateNextTabCount();
	}
	
	@Override
	protected int generateNextTabCount(){
		tabNumber = tabNumber + 1;
		return tabNumber;
	}
}
