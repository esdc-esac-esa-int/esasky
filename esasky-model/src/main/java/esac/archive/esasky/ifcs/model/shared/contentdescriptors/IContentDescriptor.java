package esac.archive.esasky.ifcs.model.shared.contentdescriptors;

public interface IContentDescriptor {
    boolean matches(String str);
    String getValue();
}
