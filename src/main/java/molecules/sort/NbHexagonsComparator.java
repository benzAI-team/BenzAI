package molecules.sort;

import molecules.Benzenoid;

public class NbHexagonsComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {
		if (molecule1.getNbHexagons() < molecule2.getNbHexagons())
			return -1;
		else if (molecule1.getNbHexagons() == molecule2.getNbHexagons())
			return 0;
		else
			return 1;
	}

}
