package esac.archive.esasky.cl.web.client.utility.exceptions;

public class SaveStateException extends Exception {
	private static final long serialVersionUID = 790880198748214996L;

	public SaveStateException() {
		super();
	}

	public SaveStateException(String errorMsg) {
		super(errorMsg);
	}
	
	public SaveStateException(String errorMsg, Exception e) {
		super(errorMsg, e);
	}
	
	
}
