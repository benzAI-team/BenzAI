package benzenoid.sort;

import benzenoid.Benzenoid;
import classifier.Irregularity;

import java.util.Optional;

public class IrregularityComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {

		Irregularity irregularity1 = molecule1.getIrregularity();
		Irregularity irregularity2 = molecule2.getIrregularity();
		
		double XI1 = irregularity1.getXI();
		double XI2 = irregularity2.getXI();
		
		if (XI1 < XI2) 
			return -1;
		else if (XI1 == XI2)
			return 0;
		else
			return 1;
	}

}
