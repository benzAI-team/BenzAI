package classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import molecules.Molecule;
import utils.Couple;

public class CarbonHydrogenClassifier extends Classifier{

	private static final int MAX_NB_CARBONS = 1000;
	private static final int MAX_NB_HYDROGENS = 1000;
	
	public CarbonHydrogenClassifier(HashMap<String, MoleculeInformation> moleculesInformations) throws IOException {
		super(moleculesInformations);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PAHClass> classify() {
		

		ArrayList<String> [][] matrixClasses = new ArrayList[MAX_NB_CARBONS][MAX_NB_HYDROGENS];
		
		for (int i = 0 ; i < MAX_NB_CARBONS ; i++)
			for (int j = 0 ; j < MAX_NB_HYDROGENS ; j++)
				matrixClasses[i][j] = new ArrayList<String>();

		Iterator<Entry<String, MoleculeInformation>> it = moleculesInformations.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = it.next();
	        
	        MoleculeInformation moleculeInformation = (MoleculeInformation) pair.getValue();
	        
	        String moleculeName = moleculeInformation.getMoleculeName();
	        Molecule molecule = moleculeInformation.getMolecule();
	        
	        Couple<Integer, Integer> nbAtoms = countCarbonsAndHydrogens(molecule);
			matrixClasses[nbAtoms.getX()][nbAtoms.getY()].add(moleculeName);
	    }
		
		ArrayList<PAHClass> classes = new ArrayList<PAHClass>();
		
		for (int i = 0 ; i < MAX_NB_CARBONS ; i++) {
			for (int j = 0 ; j < MAX_NB_HYDROGENS ; j++) {
				
				if (matrixClasses[i][j].size() > 0) {
					
					PAHClass moleculeClass = new PAHClass(i + "_carbons_" + j + "_hydrogens", moleculesInformations);
					
					for (String moleculeName : matrixClasses[i][j])
						moleculeClass.addMolecule(moleculeName);

					classes.add(moleculeClass);
				}
			}
		}
		
		return classes;
	}
	
	private Couple<Integer, Integer> countCarbonsAndHydrogens(Molecule molecule) {
		
		return new Couple<Integer, Integer>(molecule.getNbNodes(), molecule.getNbHydrogens());
	}

}
