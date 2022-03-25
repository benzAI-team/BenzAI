package molecules.sort;

import molecules.Molecule;

public class NbCarbonsComparator implements MoleculeComparator{

	@Override
	public int compare(Molecule molecule1, Molecule molecule2) {
		if (molecule1.getNbNodes() < molecule2.getNbNodes())
			return -1;
		else if (molecule1.getNbNodes() == molecule2.getNbNodes())
			return 0;
		else
			return 1;
	}
}
