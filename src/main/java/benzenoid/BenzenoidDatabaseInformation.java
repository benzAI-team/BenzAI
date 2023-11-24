package benzenoid;

import database.models.IRSpectraEntry;
import http.Post;
import spectrums.ResultLogFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BenzenoidDatabaseInformation {

    private final Benzenoid benzenoid;

    private final DatabaseCheckManager databaseCheckManager;

    private Optional<String> imsMap;
    private Optional<ResultLogFile> IRSpectra;
    private Optional<String> NICS;

    public BenzenoidDatabaseInformation(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        databaseCheckManager = new DatabaseCheckManager(benzenoid);
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

    public Optional<String> findimsMap() {

        databaseCheckManager.checkImsMap();

        if (imsMap == null) {
            String name = benzenoid.getNames().get(0);
            String service = "find_ims2d1a/";
            String json = "{\"label\": \"= " + name + "\"}";

            try {
                List<Map> results = Post.post(service, json);

                if (!results.isEmpty()) {
                    Map map = results.get(0);
                    String stringData = (String) map.get("picture");

                    imsMap = Optional.of(stringData);
                    return imsMap;
                }

            } catch (Exception e) {
                System.out.println("Connection to database failed");
            }

            imsMap = Optional.empty();
            return imsMap;
        }

        return imsMap;
    }

}
