package molecules.sort;

import molecules.Molecule;

public class NbHydrogensComparator implements MoleculeComparator{

	@Override
	public int compare(Molecule molecule1, Molecule molecule2) {
		if (molecule1.getNbHydrogens() < molecule2.getNbHydrogens())
			return -1;
		else if (molecule1.getNbHydrogens() == molecule2.getNbHydrogens())
			return 0;
		else
			return 1;
	}
}
