package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Parallel {
    public int numAS;
    public int nClusters;
    public int iterations;

    public List<AccSite> accSites;
    public List<Cluster> clusters;



    public int threads = Runtime.getRuntime().availableProcessors();
    public int chunk;

    public Parallel(List<AccSite> accSites, int numAS, int nClusters, int iterations) {
        this.accSites = accSites.subList(0,numAS);
        this.numAS = numAS;
        this.iterations = iterations;
        this.nClusters = nClusters;
        this.chunk = numAS / threads;

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
        while(i<iterations){
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
            cluster.members.clear();
        }
        //assign accSites to clusters
        //do it parallel
        ParallelWorker[] workers = new ParallelWorker[threads];
        for (int i = 0; i < threads; i++) {
            if (i==threads-1){
                workers[i] = new ParallelWorker(i*chunk, accSites.size(), clusters, accSites);
            }else{
                workers[i] = new ParallelWorker(i*chunk, i*chunk+chunk, clusters, accSites);
            }
            workers[i].start();
        }
        for (ParallelWorker worker: workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //move clusters
        for (Cluster cluster: clusters) {
            if (!cluster.members.isEmpty()){
                cluster.move();
            }

        }

    }






}
