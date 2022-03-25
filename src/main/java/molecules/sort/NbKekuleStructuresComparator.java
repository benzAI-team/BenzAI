package molecules.sort;

import molecules.Molecule;

public class NbKekuleStructuresComparator implements MoleculeComparator{

	@Override
	public int compare(Molecule molecule1, Molecule molecule2) {
		if (molecule1.getNbKekuleStructures() < molecule2.getNbKekuleStructures())
			return -1;
		else if (molecule1.getNbKekuleStructures() == molecule2.getNbKekuleStructures())
			return 0;
		else
			return 1;
	}
}
