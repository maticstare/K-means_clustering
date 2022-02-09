import UI.MapWindow;
import dataPrep.DataPrep;
import logic.AccSite;
import logic.Cluster;
import mpi.MPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainDistributed {

    public static final boolean showMap = true;
    public static final int nOfAccSites = 11093;
    public static final int nOfClusters = 50;
    public static final int nOfIterations = 40;


    static List<AccSite> accSites;
    static List<Cluster> clusters = new ArrayList<>();

    public static void main(String[] args) {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int chunk = ((int) Math.ceil((double) nOfAccSites / (double) size));
        long initTime = System.currentTimeMillis();

        //acc site
        double[] accSitesLa = new double[nOfAccSites];
        double[] accSitesLo = new double[nOfAccSites];
        int[] accSitesClusterId = new int[chunk * size];

        //cluster
        double[] clustersLa = new double[nOfClusters];
        double[] clustersLo = new double[nOfClusters];
        double[] clustersMembersNLaLo = new double[nOfClusters * 3 * size];

        //prepare initial data
        if (me==0) {
            accSites = new DataPrep(nOfAccSites).accSites.subList(0, nOfAccSites);
            for (int i = 0; i < nOfClusters; i++) {
                clusters.add(new Cluster(i, 47 + new Random().nextDouble() * (55 - 47), 5 + new Random().nextDouble() * (15 - 5)));
            }

            for (int i = 0; i < nOfAccSites; i++) {
                accSitesLa[i] = accSites.get(i).la;
                accSitesLo[i] = accSites.get(i).lo;
            }

            for (int i = 0; i < chunk*size; i++) {
                accSitesClusterId[i] = -1;
            }

            for (int i = 0; i < nOfClusters; i++) {
                clustersLa[i] = clusters.get(i).la;
                clustersLo[i] = clusters.get(i).lo;
            }
        }

        //broadcast acc sites
        MPI.COMM_WORLD.Bcast(accSitesLa, 0, accSitesLa.length, MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Bcast(accSitesLo, 0, accSitesLo.length, MPI.DOUBLE, 0);




        for (int z = 0; z < nOfIterations; z++) {
            //broadcast new clusters locations
            MPI.COMM_WORLD.Bcast(clustersLa, 0, clustersLa.length, MPI.DOUBLE, 0);
            MPI.COMM_WORLD.Bcast(clustersLo, 0, clustersLo.length, MPI.DOUBLE, 0);

            //assign accSites to clusters
            clusters.clear();
            for (int i = 0; i < nOfClusters; i++) {
                clusters.add(i, new Cluster(i, clustersLa[i], clustersLo[i]));
            }


            int[] myAccSitesClusterId = new int[chunk];
            double[] myClustersMembersNLaLo = new double[nOfClusters * 3];


            for (int i = 0; i < chunk; i++) {
                int index = me*chunk+i;
                try {
                    AccSite currAs = new AccSite(index, accSitesLa[index], accSitesLo[index]);
                    Cluster closestCluster = findClosestCluster(currAs, clusters);
                    myAccSitesClusterId[i] = closestCluster.id;

                    myClustersMembersNLaLo[closestCluster.id * 3] += 1;
                    myClustersMembersNLaLo[closestCluster.id * 3 + 1] += currAs.la;
                    myClustersMembersNLaLo[closestCluster.id * 3 + 2] += currAs.lo;
                }catch (Exception ignored){}

            }

            MPI.COMM_WORLD.Gather(
                    myClustersMembersNLaLo,
                    0,
                    myClustersMembersNLaLo.length,
                    MPI.DOUBLE,
                    clustersMembersNLaLo,
                    0,
                    myClustersMembersNLaLo.length,
                    MPI.DOUBLE,
                    0
            );

            MPI.COMM_WORLD.Gather(
                    myAccSitesClusterId,
                    0,
                    myAccSitesClusterId.length,
                    MPI.INT,
                    accSitesClusterId,
                    0,
                    myAccSitesClusterId.length,
                    MPI.INT,
                    0
            );


            //move clusters
            if (me==0){
                for (int i = 0; i < myClustersMembersNLaLo.length; i+=3) {
                    double nOfCl = 0;
                    double clusterLa = 0;
                    double clusterLo = 0;
                    for (int j = 0; j < size; j++) {
                        nOfCl += clustersMembersNLaLo[j * nOfClusters * 3 + i];
                        clusterLa += clustersMembersNLaLo[j * nOfClusters * 3 + i + 1];
                        clusterLo += clustersMembersNLaLo[j * nOfClusters * 3 + i + 2];
                    }
                    if (nOfCl != 0) {
                        clustersLa[i / 3] = clusterLa / nOfCl;
                        clustersLo[i / 3] = clusterLo / nOfCl;
                    }
                }
            }
        }

        long computationTime = System.currentTimeMillis()-initTime;
        if (me==0){
            System.out.println("Time to complete computation: " + computationTime + " ms");
        }


        //draw
        if (me == 0 && showMap){
            clusters = new ArrayList<>();
            for (int i = 0; i < nOfClusters; i++) {
                if (!Double.isNaN(clustersLa[i]) || !Double.isNaN(clustersLo[i])){
                    Cluster cluster = new Cluster(i, clustersLa[i], clustersLo[i]);
                    clusters.add(cluster);
                }
            }
            for (int i = 0; i < nOfAccSites; i++) {
                accSites.get(i).clusterId = accSitesClusterId[i];
                if (accSitesClusterId[i] != -1){
                    clusters.get(accSitesClusterId[i]).members.add(new AccSite());
                }
            }
            new MapWindow(accSites, clusters);
            System.out.println("Time to draw graphics: " + (System.currentTimeMillis() - initTime - computationTime) + " ms");
        }
        MPI.Finalize();
    }




    private static Cluster findClosestCluster(AccSite accSite, List<Cluster> clusters){
        Cluster closestCluster = clusters.get(0);
        for (Cluster cluster: clusters) {
            if (haversineRazdalja(accSite.la, accSite.lo, cluster.la, cluster.lo) < (haversineRazdalja(accSite.la, accSite.lo, closestCluster.la, closestCluster.lo))){
                closestCluster = cluster;
            }
        }
        return closestCluster;
    }

    private static double haversineRazdalja(double la1, double lo1, double la2, double lo2){
        double radius = 6371.0;
        double lat = Math.toRadians(la2-la1);
        double lon = Math.toRadians(lo2-lo1);
        double a = Math.pow(Math.sin(lat / 2), 2) + Math.pow(Math.sin(lon / 2), 2) * Math.cos(Math.toRadians(la1)) * Math.cos(Math.toRadians(la2));
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

}
