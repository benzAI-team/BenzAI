package generator.properties.model.checkers;

import java.util.ArrayList;

import generator.properties.model.ModelProperty;
import generator.properties.model.SymmetryHandler;
import generator.properties.model.expression.ParameterizedExpression;
import generator.properties.model.expression.PropertyExpression;
import molecules.Molecule;

public class SymmetryChecker extends Checker {

	@Override
	public boolean checks(Molecule molecule, ModelProperty property) {
		ArrayList<Integer> flatBooleanGrid = molecule.getVerticesSolutions();
		ArrayList<Integer> moleculeIndices = new ArrayList<Integer>();
		int couronnes = molecule.getNbCrowns();
		for(int index = 0; index < flatBooleanGrid.size(); index++)
			if(flatBooleanGrid.get(index) == 1)
				moleculeIndices.add(index);
		PropertyExpression expression = property.getExpressions().get(0);
		//System.out.println(((ParameterizedExpression)expression).getOperator());
		switch(((ParameterizedExpression)expression).getOperator()) {
		case "D_6h \"(face)-60-rotation+(edge)-mirror\"" : 
		case "D_3h(ii) \"vertex-120-rotation+(edge)-mirror\"" :
		case "D_2h(ii) \"edge-180-rotation+edge-mirror\"" :
			return true;
		case "C_6h \"(face)-60-rotation\"" : 
			return  ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes);
		case "D_3h(ia) \"face-120-rotation+face-mirror\"" :
		case "D_3h(ib) \"face-120-rotation+edge-mirror\"" :
		case "D_2h(i) \"face-180-rotation+edge-mirror\"" :
			return  ! SymmetryHandler.hasRot60Symmetry(moleculeIndices, couronnes); 
		case "C_3h(i) \"face-120-rotation\"":
			return  ! SymmetryHandler.hasRot60Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasHexagonAxisSymmetry(moleculeIndices, couronnes); 
		case "C_3h(ii) \"vertex-120-rotation\"" : 
			return  ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes);
		case "C_2v(a) \"face-mirror\"" :
			return  ! SymmetryHandler.hasRot180Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot180EdgeSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot120Symmetry(moleculeIndices, couronnes);
		case "C_2v(b) \"edge-mirror\"" :
			return ! SymmetryHandler.hasRot120Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot180Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot180EdgeSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot120VertexSymmetry(moleculeIndices, couronnes) ;
		case "C_2h(i) \"face-180-rotation\"" :
			return ! SymmetryHandler.hasRot60Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes);
		case "C_2h(ii) \"edge-180-rotation\"" :
			return ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes);
		case "C_s \"no-symmetry\"" :
			return ! SymmetryHandler.hasRot120Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot180Symmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasHexagonAxisSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasEdgeAxisSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot180EdgeSymmetry(moleculeIndices, couronnes) && ! SymmetryHandler.hasRot120VertexSymmetry(moleculeIndices, couronnes);
		default : return true;
		}
	}
}
