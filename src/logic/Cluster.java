package logic;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    public int id;
    public double la;
    public double lo;
    public List<AccSite> members;

    public Cluster(int id, double la, double lo) {
        this.id = id;
        this.la = la;
        this.lo = lo;
        this.members = new ArrayList<>();
    }


    public void move(){
        if (this.members.isEmpty()){
            //empty cluster
            return;
        }
        double la = 0;
        double lo = 0;

        for (AccSite accSite: members) {
            if (accSite != null){
                la += accSite.la;
                lo += accSite.lo;
            }

        }
        this.la = la / members.size();
        this.lo = lo / members.size();

    }
}
