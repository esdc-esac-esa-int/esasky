package esac.archive.ammi.ifcs.model.shared;

import java.util.LinkedList;
import java.util.List;

public class MultiTargetEntityList {

    List<MultiTargetEntity> data = new LinkedList<MultiTargetEntity>();
    int total;

    public List<MultiTargetEntity> getData() {
        return data;
    }

    public void setData(List<MultiTargetEntity> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
