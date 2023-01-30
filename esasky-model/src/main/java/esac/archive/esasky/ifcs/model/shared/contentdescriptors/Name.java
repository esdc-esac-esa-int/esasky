package esac.archive.esasky.ifcs.model.shared.contentdescriptors;

public enum Name implements IContentDescriptor {

    RA("ra"),
    S_RA("s_ra"),
    DEC("dec"),
    S_DEC("s_dec"),
    REGION("region"),
    S_REGION("s_region");

    private final String value;

    Name(String value) {
        this.value = value;
    }

    @Override
    public boolean matches(String str) {
        return str.equalsIgnoreCase(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
