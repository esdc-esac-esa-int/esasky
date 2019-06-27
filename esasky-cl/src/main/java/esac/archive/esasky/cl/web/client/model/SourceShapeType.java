package esac.archive.esasky.cl.web.client.model;


public enum SourceShapeType {
    
    PLUS(0, "plus"), CROSS(1, "cross"), RHOMB(2, "rhomb"), 
    TRIANGLE(3, "triangle"), CIRCLE(4, "circle"), SQUARE(5, "square");

    String name;
    int index;

    SourceShapeType(int index, String name) {
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
