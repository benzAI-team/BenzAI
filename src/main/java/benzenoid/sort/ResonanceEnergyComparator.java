package benzenoid.sort;

import benzenoid.Benzenoid;
import solveur.Aromaticity;

public class ResonanceEnergyComparator implements MoleculeComparator {

	@Override
	public int compare(Benzenoid molecule1, Benzenoid molecule2) {
		
		Aromaticity aromaticity1 = molecule1.getAromaticity().get();
		Aromaticity aromaticity2 = molecule2.getAromaticity().get();
		
		double globalAromaticity1 = aromaticity1.getGlobalAromaticity();
		double globalAromaticity2 = aromaticity2.getGlobalAromaticity();
		
		if (globalAromaticity1 < globalAromaticity2)
			return -1;
		else if (globalAromaticity1 == globalAromaticity2)
			return 0;
		else
			return 1;
	}

}
