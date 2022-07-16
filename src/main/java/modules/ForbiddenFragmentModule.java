package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.fragments.Fragment;
import generator.fragments.FragmentOccurences;

public class ForbiddenFragmentModule extends Module{

	private int mode = 3;
	
	private Fragment fragment;
	
	private ArrayList<Fragment> symmetricFragments;
	
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	private BoolVar [] presences;
	
	private FragmentOccurences fragmentOccurences;
	
	public ForbiddenFragmentModule(GeneralModel generalModel, Fragment fragment) {
		super(generalModel);
		this.fragment = fragment;
	}

	@Override
	public void buildVariables() {
		
		computeFragmentOccurences();
		
		if (mode < 3){
			presences = new BoolVar[fragmentOccurences.getOccurences().size()];
			for (int i = 0 ; i < presences.length ; i++)
				presences[i] = generalModel.getProblem().boolVar("presence_" + i);
		}
		
		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		unknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < fragment.getNbNodes() ; i++) {
			
			int label = fragment.getLabel(i);
			
			if (label == 1)
				unknownHexagons.add(i);
			
			else if (label == 2)
				presentHexagons.add(i);
			
			else if (label == 3)
				absentHexagons.add(i);
		}
	}

	@Override
	public void postConstraints() {

		ArrayList<Integer []> occurences = fragmentOccurences.getOccurences();
		
		for (int i = 0 ; i < occurences.size() ; i++) {
			
			Integer [] occurence = occurences.get(i);
			
			ArrayList<Integer> present = new ArrayList<>();
			ArrayList<Integer> absent = new ArrayList<>();
			
			for (Integer hexagon : presentHexagons) {
				if (occurence[hexagon] != -1)
					present.add(occurence[hexagon]);
			}
			
			for (Integer hexagon : absentHexagons) {
				if (occurence[hexagon] != -1)
					absent.add(occurence[hexagon]);
			}
			
			//version contrainte
			
			if (mode == 1) {
				Constraint ifCstr = generalModel.getProblem().arithm(presences[i], "=", 1);
				
				Constraint [] andCstr = new Constraint[present.size() + absent.size()];
				
				int index = 0;
				
				for (Integer j : absent) {
					andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 0);
					index ++;
				}
				
				for (Integer j : present) {
					andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 1);
					index ++;
				}	
				
				Constraint thenCstr = generalModel.getProblem().and(andCstr);
				generalModel.getProblem().ifThen(ifCstr, thenCstr);
				generalModel.getProblem().ifOnlyIf(ifCstr, thenCstr);
				
				generalModel.getProblem().sum(presences, "=", 0).post();
			}
			else if (mode == 2) {
				Constraint [] andCstr = new Constraint[present.size() + absent.size()];
				
				int index = 0;
				
				for (Integer j : absent) {
					andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 0);
					index ++;
				}
				
				for (Integer j : present) {
					andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 1);
					index ++;
				}
				
				generalModel.getProblem().and(andCstr).reifyWith(presences[i]);
				
				generalModel.getProblem().sum(presences, "=", 0).post();
			}
			else {
		 		BoolVar [] varClause = new BoolVar[present.size() + absent.size()];
		 		IntIterableRangeSet[] valClause = new IntIterableRangeSet[present.size() + absent.size()];
		 		int index = 0;
		 		
				for (Integer j : absent) {
					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(1);
					index ++;
				}
				
				for (Integer j : present) {
					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(0);
					index ++;
				}
		 		
		 		generalModel.getProblem().getClauseConstraint().addClause(varClause,valClause);			
			}
		}
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		//generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getVG(), new FirstFail(generalModel.getProblem()), new IntDomainMin()), new IntStrategy(presences, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
		if (mode < 3)
			generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getChanneling(), new FirstFail(generalModel.getProblem()), new IntDomainMin()), new IntStrategy(presences, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
		else generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getChanneling(), new FirstFail(generalModel.getProblem()), new IntDomainMin()));
	}

	@Override
	public void changeGraphVertices() { }
	
	private void computeFragmentOccurences() {
		
		symmetricFragments = fragment.computeRotations();	
		fragmentOccurences = new FragmentOccurences();
		
		for (Fragment f : symmetricFragments)
			fragmentOccurences.addAll(generalModel.computeTranslations(f));
	}

}
