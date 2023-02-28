package esac.archive.esasky.ifcs.model.shared.contentdescriptors;

public interface IUType extends IContentDescriptor{
    String getValue();

    String getType();

    default String getTypeString() {
        return getType() + ":";

    }
    default boolean isType(String ucd) {
        return ucd.toLowerCase().startsWith(getType().toLowerCase());
    }

    default boolean matches(String str) {
        return isType(str) && str.replace(getTypeString(), "").equalsIgnoreCase(getValue());
    }

}
