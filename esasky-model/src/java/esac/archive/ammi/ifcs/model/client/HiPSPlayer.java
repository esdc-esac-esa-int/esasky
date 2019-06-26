package esac.archive.ammi.ifcs.model.client;

import java.util.LinkedList;
import java.util.List;

public class HiPSPlayer {

    private List<HiPSPlayerEntry> hipsEntries = new LinkedList<HiPSPlayerEntry>();
    private int total;

    public List<HiPSPlayerEntry> getHipsEntries() {
        return hipsEntries;
    }

    public void setHipsEntries(List<HiPSPlayerEntry> hipsEntries) {
        this.hipsEntries = hipsEntries;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
