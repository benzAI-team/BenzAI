package benzenoid.sort;

import benzenoid.Benzenoid;

public class ClarNumberComparator implements MoleculeComparator {
    @Override
    public int compare(Benzenoid molecule1, Benzenoid molecule2) {
        if (molecule1.getClarNumber() < molecule2.getClarNumber())
            return -1;
        else if (molecule1.getClarNumber() == molecule2.getClarNumber())
            return 0;
        else
            return 1;
    }
}
