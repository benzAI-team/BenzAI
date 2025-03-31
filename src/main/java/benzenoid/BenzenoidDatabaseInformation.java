package benzenoid;

import database.models.IRSpectraEntry;
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
    private Optional<String> NICS;
    private Optional<String> graphFile;

    public BenzenoidDatabaseInformation(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        databaseCheckManager = new DatabaseCheckManager(benzenoid);
        imsMap = new HashMap<String,Optional<String>>();
    }

    public DatabaseCheckManager getDatabaseCheckManager() {
        return databaseCheckManager;
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
    
    
    public Optional<String> findNICS() {

        databaseCheckManager.checkNICS();

        if (NICS == null) {
            String label = benzenoid.getNames().get(0);
            String service = "find_nics/";
            String json = "{\"label\": \"= " + label + "\"}";

            try {
                List<Map> results = Post.post(service, json);

                if (!results.isEmpty()) {
                    Map map = results.get(0);
                    String stringData = (String) map.get("nics");
                    if (stringData.length() > 0) {
                      NICS = Optional.of(stringData);
                      graphFile = Optional.of((String) map.get("graphFile"));
                    }
                    return NICS;
                }
            } catch (Exception e) {
                System.out.println("Connection to database failed");
            }

            NICS = Optional.empty();
            return NICS;
        }

        return NICS;
    }

    public Optional<String> findGraphFile() {
      return graphFile;
    }
}
