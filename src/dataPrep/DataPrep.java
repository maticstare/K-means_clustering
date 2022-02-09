package dataPrep;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import logic.AccSite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DataPrep {
    public List<AccSite> accSites;

    public DataPrep(int numAS){
        accSites = ReadFromFile(numAS);
    }

    public List<AccSite> ReadFromFile(int numAS){
        String str = "";
        JsonArray parsed = new JsonArray();
        try{
            str = new String(Files.readAllBytes(Paths.get("projectData\\germany.json")));
            parsed = (JsonArray) new JsonParser().parse(str);
        }catch (IOException ignored){}

        List<AccSite> accSites = new ArrayList<>();
        parsed.forEach(line ->
            accSites.add(new AccSite(new Gson().fromJson(line.toString(), HashMap.class)))
        );

        if (accSites.size()<numAS){
            for (int i = accSites.size(); i <= numAS; i++) {
                HashMap<String,String> siteData = new HashMap<>();
                siteData.put("name", "");
                double capacity = new Random().nextDouble() * 100;
                siteData.put("capacity", String.valueOf(capacity));
                //Europe: la = 36 to 72
                //        lo = -9 to 65
                double la = 36 + new Random().nextDouble() * (72-36);
                siteData.put("la", String.valueOf(la));
                double lo = -9 + new Random().nextDouble() * (65+9);
                siteData.put("lo", String.valueOf(lo));





                accSites.add(new AccSite(siteData));
            }
        }


        return accSites;
    }
}
