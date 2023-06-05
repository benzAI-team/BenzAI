package benzenoid.sort;

import benzenoid.Benzenoid;

public class NbKekuleStructuresComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {
		if (molecule1.getNbKekuleStructures() < molecule2.getNbKekuleStructures())
			return -1;
		else if (molecule1.getNbKekuleStructures() == molecule2.getNbKekuleStructures())
			return 0;
		else
			return 1;
	}
}
