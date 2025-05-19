package benzenoid;

import java.util.HashMap;

public class DatabaseCheckManager {

    private final Benzenoid benzenoid;
    private boolean IRSpectraChecked;
    private HashMap<String,Boolean> imsMapChecked;
    private boolean NICSChecked;
    private boolean propertiesChecked;

    public DatabaseCheckManager(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        IRSpectraChecked = false;
        imsMapChecked = new HashMap<String, Boolean>();
        imsMapChecked.put("R",false);
        imsMapChecked.put("U",false);
        NICSChecked = false;
        propertiesChecked = false;
    }

    public boolean isIRSpectraChecked() {
        return IRSpectraChecked;
    }

    public  boolean isImsMapChecked(String mapType) {
        return imsMapChecked.get(mapType);
    }

    public boolean isNICSChecked() {
        return NICSChecked;
    }

    public boolean isPropertiesChecked() {
        return propertiesChecked;
    }

    public void checkIRSpectra() {
        IRSpectraChecked = true;
    }

    public void checkImsMap(String mapType) {
        imsMapChecked.put(mapType,true);
    }

    public void checkNICS() {
        NICSChecked = true;
    }

    public void checkProperties() {
        propertiesChecked = true;
    }
}
