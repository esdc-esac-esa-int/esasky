package esac.archive.ammi.ifcs.model.shared;

import java.util.LinkedList;
import java.util.List;

// TODO this class must be moved into ammi-ifcs-model
public class ESASkySSOSearchResult {

    ESASkySSOObjType ssoObjType;
    String name;
    List<String> aliases = new LinkedList<String>();

    public enum ESASkySSOObjType {
        ASTEROID("Asteroid"), COMET("Comet"), SATELLITE("Satellite"), SPACECRAFT("Spacecraft"), PLANET(
                "Planet"), DWARF_PLANET("Dwarf Planet"), SSO("Sso"), SPACEJUNK("Spacejunk"), EXOPLANET(
                        "Exoplanet"), STAR("Star");

        String type;

        ESASkySSOObjType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static ESASkySSOObjType fromValue(String value) {
            for (ESASkySSOObjType currType : ESASkySSOObjType.values()) {

                if (currType.getType().equals(value)) {
                    return currType;
                }
            }
            return null;
        }

    }

    public ESASkySSOSearchResult() {
        super();
    }

    public ESASkySSOObjType getSsoObjType() {
        return ssoObjType;
    }

    public void setSsoObjType(ESASkySSOObjType ssoObjType) {
        this.ssoObjType = ssoObjType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

}
