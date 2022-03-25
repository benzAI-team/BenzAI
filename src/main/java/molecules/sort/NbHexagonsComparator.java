package molecules.sort;

import molecules.Molecule;

public class NbHexagonsComparator implements MoleculeComparator{

	@Override
	public int compare(Molecule molecule1, Molecule molecule2) {
		if (molecule1.getNbHexagons() < molecule2.getNbHexagons())
			return -1;
		else if (molecule1.getNbHexagons() == molecule2.getNbHexagons())
			return 0;
		else
			return 1;
	}

}
