package modules;

import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.BoundsBuilder;
import generator.GeneralModel;

public class CoronoidModule extends Module{
	
	private UndirectedGraphVar holes;
	private UndirectedGraphVar coronoid;

	private BoolVar [] coronoidChanneling;
	private BoolVar [] holesChanneling;
	
	private BoolVar[] coronoidVertices;
	private BoolVar[] holesVertices;
	
	private IntVar [] benzenoidDegrees;
	private IntVar [] holesDegrees;
	
	public CoronoidModule(GeneralModel generalModel) {
		super(generalModel);
	}

	@Override
	public void buildVariables() {
		
		holes = generalModel.getProblem().graphVar("holes", BoundsBuilder.buildGLB2(generalModel), BoundsBuilder.buildGUB2(generalModel));
		
		UndirectedGraph GLBCoronoid = BoundsBuilder.buildGLB2(generalModel);
		UndirectedGraph GUBCoronoid = BoundsBuilder.buildGUB2(generalModel);
		
		coronoid = generalModel.getProblem().graphVar("coronoid", GLBCoronoid, GUBCoronoid);
			
		generalModel.setWatchedGUB(GUBCoronoid);
		
		benzenoidDegrees = new IntVar[generalModel.getNbHexagonsCoronenoid()];
		for (int i = 0 ; i < benzenoidDegrees.length ; i++)
			benzenoidDegrees[i] = generalModel.getProblem().intVar("benzenoid_degrees[" + i + "]", 0, generalModel.getNbHexagonsCoronenoid() + 1);
		
		generalModel.getProblem().degrees(generalModel.getXG(), benzenoidDegrees).post();
		
		buildCoronoidVertices();
		buildHolesVertices();
	
		holesDegrees = new IntVar[generalModel.getNbHexagonsCoronenoid()];
		for (int i = 0 ; i < holesDegrees.length ; i++)
			holesDegrees[i] = generalModel.getProblem().intVar("holes_degree[" + i + "]", 0, generalModel.getNbHexagonsCoronenoid() + 1);
		
		generalModel.getProblem().degrees(holes, holesDegrees).post();
	
	}
	
	
	
	private void buildCoronoidVertices() {
		
		int diameter = generalModel.getDiameter();
		int [][] coordsMatrix = generalModel.getCoordsMatrix();
		int [] correspondancesHexagons = generalModel.getCorrespondancesHexagons();
		
		coronoidChanneling = new BoolVar[generalModel.getNbHexagonsCoronenoid()];
		for (int i = 0 ; i < coronoidChanneling.length ; i++)
			coronoidChanneling[i] = generalModel.getProblem().boolVar("coronoid[" + i + "]");
		
		generalModel.getProblem().nodesChanneling(coronoid, coronoidChanneling).post();
		
		coronoidVertices = new BoolVar[diameter * diameter];
		
		int index = 0;
		
		for (int i = 0 ; i < diameter ; i++) {
			for (int j = 0 ; j < diameter ; j++) {
				if (coordsMatrix[i][j] != -1) {
					
					BoolVar x = coronoidChanneling[correspondancesHexagons[coordsMatrix[i][j]]];
					coronoidVertices[index] = x;
				}
				
				index ++;
			}
		}
	}
	
	private void buildHolesVertices() {
		
		int diameter = generalModel.getDiameter();
		int [][] coordsMatrix = generalModel.getCoordsMatrix();
		int [] correspondancesHexagons = generalModel.getCorrespondancesHexagons();
		
		holesChanneling = new BoolVar[generalModel.getNbHexagonsCoronenoid()];
		for (int i = 0 ; i < holesChanneling.length ; i++)
			holesChanneling[i] = generalModel.getProblem().boolVar("holes[" + i + "]");
		
		generalModel.getProblem().nodesChanneling(holes, holesChanneling).post();
		
		holesVertices = new BoolVar[diameter * diameter];
		
		int index = 0;
		
		for (int i = 0 ; i < diameter ; i++) {
			for (int j = 0 ; j < diameter ; j++) {
				if (coordsMatrix[i][j] != -1) {
					
					BoolVar x = holesChanneling[correspondancesHexagons[coordsMatrix[i][j]]];
					holesVertices[index] = x;
				}
				
				index ++;
			}
		}
	}
	
	@Override
	public void postConstraints() {
		
		//Remplace la contrainte subGraph
		for (int i = 0 ; i < holesChanneling.length ; i++) {
			generalModel.getProblem().ifThen(generalModel.getProblem().arithm(holesChanneling[i], "=", 1), 
					                         generalModel.getProblem().arithm(generalModel.getChanneling()[i], "=" , 1));
		}
		
	 	postDiggableHexagonSurrounded();
	 	generalModel.getProblem().minDegree(holes, 1).post();
	 	generalModel.getProblem().sum(holesDegrees, ">", 0).post();
	 	postBenzenoidIsCoronoidXORHoles();
	 	generalModel.getProblem().connected(coronoid).post();
	}

	@Override
	public void addWatchedVariables() {
	
	}
	
	@Override
	public void changeSolvingStrategy() {
		generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getChanneling(), new FirstFail(generalModel.getProblem()), new IntDomainMax()),
				                                        new IntStrategy(holesChanneling, new FirstFail(generalModel.getProblem()), new IntDomainMax()));
	}
	
	/***
	 * pose la contrainte qu'un hexagone ne peut être dans un trou que s'il est entouré de 6 hexagones dans le graphe
	 */
	private void postDiggableHexagonSurrounded() {
	 	for(int i = 0; i < benzenoidDegrees.length ; i++) {
	 		
	 		BoolVar x = generalModel.getProblem().arithm(holesDegrees[i], ">", 0).reify();
	 		BoolVar y = generalModel.getProblem().arithm(benzenoidDegrees[i], "=", 6).reify();
	 		
	 		BoolVar [] clauseVariables = new BoolVar [] {x, y};
	 		IntIterableRangeSet [] clauseValues = new IntIterableRangeSet[] {
	 			new IntIterableRangeSet(0),
	 			new IntIterableRangeSet(1)
	 		};
	 		
	 		generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
	 		
	 		/*
	 		generalModel.getProblem().addClauses(LogOp.implies(generalModel.getProblem().arithm(holesDegrees[i], ">", 0).reify(),
	 				                                           generalModel.getProblem().arithm(benzenoidDegrees[i], "=", 6).reify()));		
	 		*/
	 	}
	}
	
	/***
	 * pose la contrainte que les sommets du benzenoides sont soit dans le coronoide soit dans les trous
	 */
	private void postBenzenoidIsCoronoidXORHoles() {	
		for(int i = 0; i < generalModel.getChanneling().length ; i++) {
	 			/*
				generalModel.getProblem().addClauses(LogOp.implies(coronoidChanneling[i], generalModel.getChanneling()[i]));
	 			generalModel.getProblem().addClauses(LogOp.nand(coronoidChanneling[i], holesChanneling[i]));
	 			generalModel.getProblem().addClauses(LogOp.implies(generalModel.getChanneling()[i], LogOp.or(holesChanneling[i], coronoidChanneling[i])));
	 			*/
	 			
	 			// Clause 1
	 			BoolVar[] clauseVariables = new BoolVar[] {coronoidChanneling[i], generalModel.getChanneling()[i]};
	 			IntIterableRangeSet [] clauseValues = new IntIterableRangeSet [] {
	 					new IntIterableRangeSet(0), new IntIterableRangeSet(1)
	 			};
	 			generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
	 			
	 			// Clause 2
	 			clauseVariables = new BoolVar[] {coronoidChanneling[i], holesChanneling[i]};
	 			clauseValues = new IntIterableRangeSet [] {
	 					new IntIterableRangeSet(0), new IntIterableRangeSet(0)
	 			};
	 			generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
	 			
	 			// Clause 3
	 			clauseVariables = new BoolVar[] {generalModel.getChanneling()[i], holesChanneling[i], coronoidChanneling[i]};
	 			clauseValues = new IntIterableRangeSet [] {
	 					new IntIterableRangeSet(0), new IntIterableRangeSet(1), new IntIterableRangeSet(1)
	 			};
	 			generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
		}	
	}

	@Override 
	public void changeWatchedGraphVertices() {
		generalModel.setWatchedGraphVertices(coronoidVertices);
		generalModel.setWatchedChanneling(coronoidChanneling);
		generalModel.setWatchedGraphVar(coronoid);
	}

	@Override
	public void setPriority() {
		priority = 3;
	}
	
	@Override
	public String toString() {
		return "CoronoidModule";
	}
}
