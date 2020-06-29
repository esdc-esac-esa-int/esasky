package esac.archive.esasky.ifcs.model.shared;

public enum ColumnType {
    DATALINK("datalink"), LINK("link"), LINKLIST("linklist"), STRING("string"), CHAR("char"), VARCHAR("char"),  LINK2ARCHIVE("link2archive"), DOWNLOAD("download"), RA(
            "ra"), DEC("dec"), DOUBLE("double"), FLOAT("double"), INTEGER("integer"), LONG("long"), INT("int"), DATETIME("datetime"), TIMESTAMP("timestamp");

    private String type;

    private ColumnType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
