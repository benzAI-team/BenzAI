package molecules.sort;

import molecules.Molecule;

public class IrregularityComparator implements MoleculeComparator{

	@Override
	public int compare(Molecule molecule1, Molecule molecule2) {
		
		if (molecule1.getIrregularity() == null && molecule2.getIrregularity() == null)
			return 0;
		
		else if (molecule1.getIrregularity() == null && molecule2.getIrregularity() != null)
			return -1;
		
		else if (molecule1.getIrregularity() != null && molecule2.getIrregularity() == null)
			return 1;
		
		double XI1 = molecule1.getIrregularity().getXI();
		double XI2 = molecule2.getIrregularity().getXI();
		
		
		
		if (XI1 < XI2) 
			return -1;
		else if (XI1 == XI2)
			return 0;
		else
			return 1;
	}

}
