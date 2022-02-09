package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sequential {
    public int numAS;
    public int nClusters;
    public int iterations;

    public List<AccSite> accSites;
    public List<Cluster> clusters;

    //public boolean notDone = true;

    public Sequential(List<AccSite> accSites, int numAS, int nClusters, int iterations) {
        this.accSites = accSites.subList(0,numAS);
        this.numAS = numAS;
        this.iterations = iterations;
        this.nClusters = nClusters;
        //Europe: la = 36 to 72
        //        lo = -9 to 65
        //Germany: la = 47 to 55
        //         lo = 5 to 15
        //set n of clusters randomly
        this.clusters = new ArrayList<>();
        for (int i = 0; i < nClusters; i++) {
            clusters.add(new Cluster(i, 47 + new Random().nextDouble() * (55-47), 5 + new Random().nextDouble() * (15-5)));
        }

        int i = 0;
        while(i<iterations) {
            // && notDone
            //notDone = false;
            iteration();
            i++;
        }

        for (AccSite as: accSites) {
            try {
                as.clusterId = as.cluster.id;
            }catch (Exception ignored){}

        }

        //System.out.println("Iterations: " + i);
    }



    public void iteration(){
        //reset cluster members
        for (Cluster cluster: clusters) {
            cluster.members = new ArrayList<>();
        }
        //assign accSites to clusters
        for (AccSite accSite: accSites) {
            Cluster closestCluster = findClosestCluster(accSite);
            /*
             if (accSite.cluster != closestCluster){
                 notDone = true;
             }
            */
            accSite.cluster = closestCluster;
            closestCluster.members.add(accSite);
        }
        //move clusters
        for (Cluster cluster: clusters) {
            if (!cluster.members.isEmpty()){
                cluster.move();
            }

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
