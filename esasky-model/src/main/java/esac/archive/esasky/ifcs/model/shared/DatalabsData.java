package esac.archive.esasky.ifcs.model.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import esac.archive.esasky.ifcs.model.coordinatesutils.SkyViewPosition;
import esac.archive.esasky.ifcs.model.descriptor.CommonTapDescriptor;

import java.util.ArrayList;

public class DatalabsData {

    public static class DataEntity {
        @JsonProperty("descriptor")
        private CommonTapDescriptor descriptor;

        @JsonProperty("query")
        private String query;

        @JsonProperty("sourceId")
        private String sourceId;

        @JsonProperty("authorId")
        private String authorId;

        @JsonProperty("skyViewPosition")
        private SkyViewPosition skyViewPosition;

        @JsonProperty("levelDescriptor")
        private String levelDescriptor;

        public DataEntity() {}

        public DataEntity(CommonTapDescriptor descriptor, String query, SkyViewPosition skyViewPosition, String sourceId, String authorId, String levelDescriptor) {
            this.descriptor = descriptor;
            this.query = query;
            this.sourceId = sourceId;
            this.authorId = authorId;
            this.skyViewPosition = skyViewPosition;
            this.levelDescriptor = levelDescriptor;
        }

        public CommonTapDescriptor getDescriptor() {
            return descriptor;
        }

        public String getQuery() {
            return query;
        }

        public String getSourceId() {
            return sourceId;
        }

        public String getAuthorId() {
            return authorId;
        }

        public String getLevelDescriptor() {
            return levelDescriptor;
        }

        public SkyViewPosition getSkyViewPosition() {
            return skyViewPosition;
        }
    }

    @JsonProperty
    ArrayList<DataEntity> entities;

    @JsonProperty("hipsList")
    private ArrayList<String> hipsList;

    public DatalabsData() {
        entities = new ArrayList<>();
        hipsList = new ArrayList<>();
    }

    public void addDataEntity(final DataEntity entity) {
        entities.add(entity);
    }

    public void addHips(String hipsName) {
        hipsList.add(hipsName);
    }

    public ArrayList<String> getHipsList() {
        return hipsList;
    }

    public ArrayList<DataEntity> getEntities() {
        return entities;
    }
}
