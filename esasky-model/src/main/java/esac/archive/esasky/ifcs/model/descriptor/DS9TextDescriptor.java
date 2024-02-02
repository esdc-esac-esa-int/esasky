package esac.archive.esasky.ifcs.model.descriptor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DS9TextDescriptor {
    @JsonProperty("text")
    private String text;

    @JsonProperty("angle")
    private float angle;

    @JsonProperty("ra")
    private double ra;

    @JsonProperty("dec")
    private double dec;

    public DS9TextDescriptor() {}
    public DS9TextDescriptor(String text, float angle, double ra, double dec) {
        this.text = text;
        this.angle = angle;
        this.ra = ra;
        this.dec = dec;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public double getRa() {
        return ra;
    }

    public void setRa(double ra) {
        this.ra = ra;
    }

    public double getDec() {
        return dec;
    }

    public void setDec(double dec) {
        this.dec = dec;
    }
}
