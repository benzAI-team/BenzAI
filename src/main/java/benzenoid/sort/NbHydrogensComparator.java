package benzenoid.sort;

import benzenoid.Benzenoid;

public class NbHydrogensComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {
		if (molecule1.getNbHydrogens() < molecule2.getNbHydrogens())
			return -1;
		else if (molecule1.getNbHydrogens() == molecule2.getNbHydrogens())
			return 0;
		else
			return 1;
	}
}
