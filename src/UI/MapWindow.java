package UI;


import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

import logic.AccSite;
import logic.Cluster;
import logic.Parallel;
import logic.Sequential;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;
import waypoint.Waypoint;

public class MapWindow extends JFrame {
    private JXMapViewer jXMapViewer;


    public MapWindow(Sequential sequential) {
        initComponents();
        drawWaypoints(sequential.accSites, sequential.clusters);
    }

    public MapWindow(Parallel parallel) {
        initComponents();
        drawWaypoints(parallel.accSites, parallel.clusters);
    }

    public MapWindow(List<AccSite> accSites, List<Cluster> clusters){
        initComponents();
        drawWaypoints(accSites, clusters);
    }

    private void initComponents() {
        jXMapViewer = new JXMapViewer();
        jXMapViewer.setLayout(new GroupLayout(jXMapViewer));
        getContentPane().add(jXMapViewer);
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        jXMapViewer.setTileFactory(new DefaultTileFactory(new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP)));
        jXMapViewer.setAddressLocation(new GeoPosition(51.4, 10));
        jXMapViewer.setZoom(16);

        MouseInputListener mm = new PanMouseInputListener(jXMapViewer);
        jXMapViewer.addMouseListener(mm);
        jXMapViewer.addMouseMotionListener(mm);
        jXMapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(jXMapViewer));
    }


    private void drawWaypoints(List<AccSite> accSites, List<Cluster> clusters) {
        Set<Waypoint> allWaypoints = new HashSet<>();
        for (AccSite accSite: accSites) {
            allWaypoints.add(new Waypoint(accSite.clusterId, new GeoPosition(accSite.la, accSite.lo)));
        }
        for (Cluster cluster: clusters) {
            allWaypoints.add(new Waypoint(cluster.id, new GeoPosition(cluster.la, cluster.lo), cluster.members));
        }

        WaypointPainter<Waypoint> wp = new WaypointPainter<>(){

            protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
                for (Waypoint wp : allWaypoints) {
                    Point2D p = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                    Rectangle rec = map.getViewportBounds();
                    int x = (int) (p.getX() - rec.getX());
                    int y = (int) (p.getY() - rec.getY());
                    JButton cmd = wp.getWaypointButton();
                    cmd.setLocation(x - cmd.getWidth() / 2, y - cmd.getHeight());
                    if (wp.getId() == Integer.MIN_VALUE){
                        wp.getWaypointButton().setSize(new Dimension(8, 8));
                        wp.getWaypointButton().setBackground( new Color(wp.getPripada() * 1001683  % 1000000));
                    }else{
                        wp.getWaypointButton().setSize(new Dimension(20, 20));
                        wp.getWaypointButton().setBorder(new LineBorder(Color.black));
                        wp.getWaypointButton().setVisible(true);
                        if(wp.getId() != Integer.MIN_VALUE && !wp.getMembers().isEmpty()){
                            wp.getWaypointButton().setBackground( new Color(wp.getId() * 1001683  % 1000000));
                        }
                    }
                }

            }
        };
        wp.setWaypoints(allWaypoints);
        jXMapViewer.setOverlayPainter(wp);
        for (Waypoint d : allWaypoints) {
            jXMapViewer.add(d.getWaypointButton());
        }
    }
}
