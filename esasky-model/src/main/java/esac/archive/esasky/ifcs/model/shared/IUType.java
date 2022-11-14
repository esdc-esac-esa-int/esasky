package esac.archive.esasky.ifcs.model.shared;

public interface IUType extends IContentDescriptor{
    String getValue();

    String getType();

    default boolean isType(String ucd) {
        return ucd.toLowerCase().startsWith(getType().toLowerCase());
    }

    default boolean matches(String str) {
        return isType(str) && str.toLowerCase().contains(getValue().toLowerCase());
    }

}
