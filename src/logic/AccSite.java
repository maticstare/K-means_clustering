package logic;

import java.util.HashMap;

public class AccSite {
    public double la;
    public double lo;
    public Cluster cluster;

    public int id;
    public int clusterId;


    public AccSite(HashMap<String, String> siteData) {
        this.la = Double.parseDouble(siteData.get("la"));
        this.lo = Double.parseDouble(siteData.get("lo"));
    }

    public AccSite(int id, double la, double lo){
        this.id = id;
        this.la = la;
        this.lo = lo;
    }

    public AccSite(){}


}
