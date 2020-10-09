package esac.archive.esasky.cl.web.client.model;


public enum LineStyle {
    
    SOLID(0, "solid","--"), DASHED(1, "dashed", "- -"), DOT(1, "dot", ". .");

    String name;
    int index;
    String view;

    LineStyle(int index, String name, String view) {
        this.index = index;
        this.name = name;
        this.view = view;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }
    public String getView() {
    	return this.view;
    }
}
