import dataPrep.DataPrep;
import logic.Parallel;
import logic.Sequential;

import java.util.Arrays;

public class MainSecondTest {
    public static void main(String[] args) {
        //30000 accumulation sites, 40 algorithm iterations, starting 10 clusters and +10 each iteration

        int nOfAccSites = 30000;
        int iterations = 40;
        int nOfClusters = 10;

        int nOfTest = 1;


        //sequential
        int[] ndOutputSeq = new int[24];
        for (int i = nOfClusters; i <= 240; i+=10) {
            int avg = 0;
            for (int j = 0; j < nOfTest; j++) {
                long initTime = System.currentTimeMillis();
                new Sequential(new DataPrep(nOfAccSites).accSites, nOfAccSites, i, iterations);
                long computationTime = System.currentTimeMillis()-initTime;
                avg+=computationTime;
            }
            ndOutputSeq[i/10-1] = avg/nOfTest;
        }
        System.out.println("Second test, sequential output: " + Arrays.toString(ndOutputSeq));


        //parallel
        int[] ndOutputPar = new int[24];

        for (int i = nOfClusters; i <= 240; i+=10) {
            int avg = 0;
            for (int j = 0; j < nOfTest; j++) {
                long initTime = System.currentTimeMillis();
                new Parallel(new DataPrep(nOfAccSites).accSites, nOfAccSites, i, iterations);
                long computationTime = System.currentTimeMillis()-initTime;
                avg+=computationTime;
            }
            ndOutputPar[i/10-1] = avg/nOfTest;
        }
        System.out.println("Second test, parallel output:   " + Arrays.toString(ndOutputPar));







    }
}
