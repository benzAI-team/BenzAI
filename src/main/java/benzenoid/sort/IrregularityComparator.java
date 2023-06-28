package benzenoid.sort;

import benzenoid.Benzenoid;
import classifier.Irregularity;

import java.util.Optional;

public class IrregularityComparator implements MoleculeComparator{

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {

		Optional<Irregularity> irregularity1 = molecule1.getIrregularity();
		Optional<Irregularity> irregularity2 = molecule2.getIrregularity();

		if (irregularity1.isEmpty() && irregularity2.isEmpty())
			return 0;

		else if (irregularity1.isEmpty() && irregularity2.isPresent())
			return -1;

		else if (irregularity1.isPresent() && irregularity2.isEmpty())
			return 1;
		
		double XI1 = irregularity1.get().getXI();
		double XI2 = irregularity2.get().getXI();
		
		if (XI1 < XI2) 
			return -1;
		else if (XI1 == XI2)
			return 0;
		else
			return 1;
	}

}
