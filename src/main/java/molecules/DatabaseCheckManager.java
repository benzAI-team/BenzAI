package molecules;

public class DatabaseCheckManager {

    private Benzenoid benzenoid;

    private boolean IRSpectraChecked;

    private boolean imsMapChecked;

    private boolean NICSChecked;

    public DatabaseCheckManager(Benzenoid benzenoid) {
        this.benzenoid = benzenoid;
        IRSpectraChecked = false;
        imsMapChecked = false;
        NICSChecked = false;
    }

    public boolean isIRSpectraChecked() {
        return IRSpectraChecked;
    }

    public  boolean isImsMapChecked() {
        return imsMapChecked;
    }

    public boolean isNICSChecked() {
        return NICSChecked;
    }

    public void checkIRSpectra() {
        IRSpectraChecked = true;
    }

    public void checkImsMap() {
        imsMapChecked = true;
    }

    public void checkNICS() {
        NICSChecked = true;
    }
}
