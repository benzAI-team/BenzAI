package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;

public class CatacondensedModule2 extends Module {

	public CatacondensedModule2(GeneralModel generalModel) {
		super(generalModel);
	}


	@Override
	public void buildVariables() {
		//DO_NOTHING
	}

	@Override
	public void postConstraints() {
		
		ArrayList<BoolVar []> triangles = computeTriangles();
		
		for (BoolVar [] triangle : triangles) {
		
			IntIterableRangeSet[] valClause = new IntIterableRangeSet[] {
					new IntIterableRangeSet(0),
					new IntIterableRangeSet(0),
					new IntIterableRangeSet(0)
			};

			generalModel.getProblem().getClauseConstraint().addClause(triangle, valClause);
		}
		
		System.out.println("");
	}

	@Override
	public void addVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub
		
	}
	
	private int getMatrix(int [][] matrix, int i, int j) {
		
		if (i >= matrix.length || j >= matrix.length)
			return -1;
		
		return matrix[i][j];
	}
	
	private ArrayList<BoolVar []> computeTriangles() {
		
		int diameter = generalModel.getDiameter();
		int [][] coordsMatrix = generalModel.getCoordsMatrix();
		
		ArrayList<BoolVar []> triangles = new ArrayList<>();
		
		for (int i = 0 ; i < diameter ; i++) {
			for (int j = 0 ; j < diameter ; j++) {
				if (coordsMatrix[i][j] != -1) {
					
					int u = getMatrix(coordsMatrix, i, j);
					int v1 = getMatrix(coordsMatrix, i+1, j);
					int v2 = getMatrix(coordsMatrix, i, j+1);
					int w = getMatrix(coordsMatrix, i+1, j+1);
					
					if (v1 != -1 && w != -1)
						triangles.add(new BoolVar[] {generalModel.getGraphVertices()[u], 
													generalModel.getGraphVertices()[v1], 
													generalModel.getGraphVertices()[w]});
					
					
						
					if (v2 != -1 && w != -1)
						triangles.add(new BoolVar[] {generalModel.getGraphVertices()[u], 
								generalModel.getGraphVertices()[v2], 
								generalModel.getGraphVertices()[w]});
					
				}
			}
		}
		
		return triangles;
	}

}
