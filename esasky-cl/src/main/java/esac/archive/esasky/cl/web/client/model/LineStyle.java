package esac.archive.esasky.cl.web.client.model;


public enum LineStyle {
    
    SOLID(0, "solid"), DASHED(1, "dashed"), DOT(1, "dot");

    String name;
    int index;

    LineStyle(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }
}
