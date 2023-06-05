package molecules;

import database.models.IRSpectraEntry;
import http.Post;
import org.chocosolver.solver.constraints.nary.nvalue.amnv.differences.D;
import spectrums.ResultLogFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BenzenoidDatabaseInformations {

    private Benzenoid benzenoid;

    private DatabaseCheckManager databaseCheckManager;

    private Optional<String> imsMap;
    private Optional<ResultLogFile> IRSpectra;
    private Optional<String> NICS;

    public BenzenoidDatabaseInformations(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        databaseCheckManager = new DatabaseCheckManager(benzenoid);
    }

    public DatabaseCheckManager getDatabaseCheckManager() {
        return databaseCheckManager;
    }

    public Optional<ResultLogFile> findIRSpectra() {

        databaseCheckManager.checkIRSpectra();

        if (IRSpectra == null) {

            String name = benzenoid.getNames().get(0);
            String url = "https://benzenoids.lis-lab.fr/find_by_name/";
            String json = "{\"name\": \"" + name + "\"}";

            try {
                List<Map> results = Post.post(url, json);

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
            String url = "https://benzenoids.lis-lab.fr/find_ims2d_1a_by_name/";
            String json = "{\"name\": \"" + name + "\"}";

            try {
                List<Map> results = Post.post(url, json);

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
