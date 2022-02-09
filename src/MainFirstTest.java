import dataPrep.DataPrep;
import logic.Parallel;
import logic.Sequential;

import java.util.Arrays;

public class MainFirstTest {
    public static void main(String[] args) {
        //50 clusters, 40 algorithm iterations, starting 5000 accumulation sites and +5000 each iteration

        int nOfClusters = 50;
        int iterations = 40;
        int nOfAccSites = 5000;

        int nOfTests = 5;


        //sequential
        int[] stOutputSeq = new int[24];
        for (int i = nOfAccSites; i <= 120000; i+=5000) {
            int avg = 0;
            for (int j = 0; j < nOfTests; j++) {
                long initTime = System.currentTimeMillis();
                new Sequential(new DataPrep(i).accSites, i, nOfClusters, iterations);
                long computationTime = System.currentTimeMillis()-initTime;
                avg+=computationTime;
            }
            stOutputSeq[i/5000-1] = avg/nOfTests;
        }
        System.out.println("First test, sequential output: " + Arrays.toString(stOutputSeq));


        //parallel
        int[] stOutputPar = new int[24];
        for (int i = nOfAccSites; i <= 120000; i+=5000) {
            int avg = 0;
            for (int j = 0; j < nOfTests; j++) {
                long initTime = System.currentTimeMillis();
                new Parallel(new DataPrep(i).accSites, i, nOfClusters, iterations);
                long computationTime = System.currentTimeMillis()-initTime;
                avg+=computationTime;
            }
            stOutputPar[i/5000-1] = avg/nOfTests;
        }
        System.out.println("First test, parallel output:   " + Arrays.toString(stOutputPar));
    }
}
