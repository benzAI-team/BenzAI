package benzenoid;

import database.models.IRSpectraEntry;
import database.models.PropertiesEntry;
import http.Post;
import spectrums.ResultLogFile;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BenzenoidDatabaseInformation {

    private final Benzenoid benzenoid;

    private final DatabaseCheckManager databaseCheckManager;

    private HashMap<String,Optional<String>> imsMap;
    private Optional<ResultLogFile> IRSpectra;
    private HashMap<String,Optional<String>>  NICS;
    private Optional<String> graphFile;

    public BenzenoidDatabaseInformation(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        databaseCheckManager = new DatabaseCheckManager(benzenoid);
        imsMap = new HashMap<String,Optional<String>>();
        NICS = new HashMap<String,Optional<String>>();
    }

    public DatabaseCheckManager getDatabaseCheckManager() {
        return databaseCheckManager;
    }

    public Boolean findProperties() {

        databaseCheckManager.checkProperties();

        String label = benzenoid.getNames().get(0);
        String service = "find_properties/";
        String json = "{\"label\": \"= " + label + "\"}";

        try {
            List<Map> results = Post.post(service, json);

            if (!results.isEmpty()) {
                PropertiesEntry content = PropertiesEntry.buildQueryContent(results.get(0));
                benzenoid.setInchi(content.getInchi());
                benzenoid.setSmiles(content.getSmiles());
                benzenoid.setSelfies(content.getSelfies());
                benzenoid.setBenzdbId(content.getIdMolecule());
                benzenoid.setClarNumber(content.getClarNumber());
                benzenoid.setHomo(content.getHomo());
                benzenoid.setLumo(content.getLumo());
                benzenoid.setMoment(content.getMoment());
                return true;
            }
            else {
                return false;
            }

        } catch (Exception e) {
            System.out.println("Connection to database failed");
        }

        return false;
    }

    public Optional<ResultLogFile> findIRSpectra() {

        databaseCheckManager.checkIRSpectra();

        if (IRSpectra == null) {

            String label = benzenoid.getNames().get(0);
            String service = "find_ir/";
            String json = "{\"label\": \"= " + label + "\"}";

            try {
                List<Map> results = Post.post(service, json);
                
                if (!results.isEmpty()) {
                    findProperties();

                    IRSpectraEntry content = IRSpectraEntry.buildQueryContent(results.get(0));

                    String amesFormat = content.getAmesFormat();

                    ResultLogFile IRSpectraData = content.buildResultLogFile();
                    IRSpectraData.setAmesFormat(amesFormat);

                    IRSpectra = Optional.of(IRSpectraData);

                    System.out.println(IRSpectraData);

                    benzenoid.setInchi(content.getInchi());
                    benzenoid.setBenzdbId(content.getIdMolecule());
                    
                    return IRSpectra;
                }

            } catch (Exception e) {
                System.out.println("Connection to database failed");
            }

            IRSpectra = Optional.empty();
            return IRSpectra;
        }
        return IRSpectra;
    }

    public Optional<String> findimsMap(String mapType) {

        databaseCheckManager.checkImsMap(mapType);
        
        if (imsMap.get(mapType) == null) {
            String name = benzenoid.getNames().get(0);
            String service = "find_ims2d1a/";            
            String json = "{\"label\": \"= " + name + "\"";
            
            if (mapType == "R"){
              json += ", \"type\": \"= R\"";
            }
            else if (mapType == "U")
            {
              json += ", \"type\": \"= U\"";
            }
            
            json +="}";
            
            Optional<String> op = Optional.empty();
            try {
                List<Map> results = Post.post(service, json);

                if (!results.isEmpty()) {
                    Map map = results.get(0);
                    String stringData = (String) map.get("picture");
                    op = Optional.of(stringData);
                }

            } catch (Exception e) {
                System.out.println("Connection to database failed");
            }
            imsMap.put(mapType, op);
        }

        return imsMap.get(mapType);
    }
    
    
    public Optional<String> findNICS(String nicsType) {

        databaseCheckManager.checkNICS(nicsType);

        if (NICS.get(nicsType) == null) {
            String label = benzenoid.getNames().get(0);
            String service = "find_nics/";
            String json = "{\"label\": \"= " + label + "\"}";

            Optional<String> op = Optional.empty();
            try {
                List<Map> results = Post.post(service, json);

                if (!results.isEmpty()) {
                    Map map = results.get(0);
                    String stringData = (String) map.get("nics"+nicsType);
                    if (stringData.length() > 0) {
                        System.out.println(stringData);
                      op = Optional.of(stringData);
                      graphFile = Optional.of((String) map.get("graphFile"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection to database failed");
            }

            NICS.put(nicsType,op);;
        }

        return NICS.get(nicsType);
    }

    public Optional<String> findGraphFile() {
      return graphFile;
    }
}
