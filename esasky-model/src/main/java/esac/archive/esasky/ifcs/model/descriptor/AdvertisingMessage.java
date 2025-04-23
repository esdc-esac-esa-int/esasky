package esac.archive.esasky.ifcs.model.descriptor;

public class AdvertisingMessage {
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AdvertisingMessage bannerMessage = (AdvertisingMessage) obj;
        return message.equals(bannerMessage.getMessage());
    }
}
