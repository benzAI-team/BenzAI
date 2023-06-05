package benzenoid.sort;

import benzenoid.Benzenoid;

public class NbCarbonsComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {
		if (molecule1.getNbNodes() < molecule2.getNbNodes())
			return -1;
		else if (molecule1.getNbNodes() == molecule2.getNbNodes())
			return 0;
		else
			return 1;
	}
}
