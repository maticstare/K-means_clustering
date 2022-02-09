package waypoint;
import logic.AccSite;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.util.List;

public class Waypoint extends DefaultWaypoint {
    private final JButton waypointButton;
    private int pripada;
    private List<AccSite> members;

    private final int id;

    //accSite
    public Waypoint(int pripada, GeoPosition coord) {
        super(coord);
        this.waypointButton = new WaypointButton();
        this.pripada = pripada;
        this.id = Integer.MIN_VALUE;
    }

    //cluster
    public Waypoint(int id, GeoPosition coord, List<AccSite> members) {
        super(coord);
        this.waypointButton = new WaypointButton();
        this.members = members;
        this.id = id;
    }

    public JButton getWaypointButton() {
        return waypointButton;
    }

    public int getPripada() {
        return pripada;
    }

    public int getId() {
        return id;
    }

    public List<AccSite> getMembers() {
        return members;
    }
}
