package logic;

import java.util.List;

public class ParallelWorker extends Thread {
    int start;
    int end;
    List<Cluster> clusters;
    List<AccSite> accSites;

    public ParallelWorker(int start, int end, List<Cluster> clusters, List<AccSite> accSites){
        this.start = start;
        this.end = end;
        this.clusters = clusters;
        this.accSites = accSites;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            Cluster closestCluster = findClosestCluster(accSites.get(i));
            accSites.get(i).cluster = closestCluster;
            try {
                closestCluster.members.add(accSites.get(i));
            }catch (ArrayIndexOutOfBoundsException ignored){}


        }
    }

    private Cluster findClosestCluster(AccSite accSite){
        Cluster closestCluster = clusters.get(0);
        for (Cluster cluster: clusters) {
            if (haversineRazdalja(accSite.la, accSite.lo, cluster.la, cluster.lo) < (haversineRazdalja(accSite.la, accSite.lo, closestCluster.la, closestCluster.lo))){
                closestCluster = cluster;
            }
        }
        return closestCluster;
    }

    private double haversineRazdalja(double la1, double lo1, double la2, double lo2){
        double radius = 6371.0;
        double lat = Math.toRadians(la2-la1);
        double lon = Math.toRadians(lo2-lo1);
        double a = Math.pow(Math.sin(lat / 2), 2) + Math.pow(Math.sin(lon / 2), 2) * Math.cos(Math.toRadians(la1)) * Math.cos(Math.toRadians(la2));
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }
}
