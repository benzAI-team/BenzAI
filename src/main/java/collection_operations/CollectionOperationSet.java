package collection_operations;

import java.util.ArrayList;
import java.util.Objects;

import javafx.scene.control.MenuItem;

public enum CollectionOperationSet {
    ;
  private static final ArrayList<CollectionOperation> collectionSimpleOperationSet = new ArrayList<>();
	private static final ArrayList<CollectionOperation> collectionComputationSet = new ArrayList<>();
	private static final ArrayList<CollectionOperation> collectionIOSet = new ArrayList<>();

	private static final ArrayList<CollectionOperation> collectionOperationSet = new ArrayList<>();

	static {
		collectionSimpleOperationSet.add(new CollectionRename());
		collectionSimpleOperationSet.add(new CollectionCopy());
		collectionSimpleOperationSet.add(new CollectionPaste());
		collectionSimpleOperationSet.add(new CollectionDelete());
		collectionSimpleOperationSet.add(new CollectionSelectAll());
		collectionSimpleOperationSet.add(new CollectionUnselectAll());
		collectionSimpleOperationSet.add(new CollectionDraw());
		collectionSimpleOperationSet.add(new CheckDatabaseTask());

		collectionIOSet.add(new CollectionExportProperties());
		collectionIOSet.add(new CollectionExportGraph(false));
		collectionIOSet.add(new CollectionExportDot(false));
		collectionIOSet.add(new CollectionExportPNG(false));
		collectionIOSet.add(new CollectionExportCOM(false));
		collectionIOSet.add(new CollectionExportCML(false));
		collectionIOSet.add(new CollectionExportGraph(true));
		collectionIOSet.add(new CollectionExportDot(true));
		collectionIOSet.add(new CollectionExportPNG(true));
		collectionIOSet.add(new CollectionExportCOM(true));
		collectionIOSet.add(new CollectionExportCML(true));
		collectionIOSet.add(new CollectionImport());

		collectionComputationSet.add(new LinResonanceEnergyTask());
		collectionComputationSet.add(new LinFanResonanceEnergyTask());
		collectionComputationSet.add(new NicsTask());
		collectionComputationSet.add(new ClarCoverTask());
		collectionComputationSet.add(new FixedBondClarCoverComputation());
		collectionComputationSet.add(new ForcedSingleClarCoverTask());
		collectionComputationSet.add(new ForcedSingleStatisticsTask());
		collectionComputationSet.add(new AllKekuleStructureComputation());
		collectionComputationSet.add(new RingBoundOrderTask());
		collectionComputationSet.add(new Ims2d1aComputation("R"));
		collectionComputationSet.add(new Ims2d1aComputation("U"));
		collectionComputationSet.add(new Ims2d1aComputation("R&U"));
		collectionComputationSet.add(new IRSpectraTask());
		collectionComputationSet.add(new RadicalarStatisticsTask());
		collectionComputationSet.add(new IrregularityComputation());

		collectionOperationSet.addAll(collectionSimpleOperationSet);
		collectionOperationSet.addAll(collectionComputationSet);
		collectionOperationSet.addAll(collectionIOSet);
	}

	public static MenuItem getMenuItemByName(String name){
		for(CollectionOperation operation : collectionOperationSet)
			if(Objects.equals(name, operation.getMenuItem().getText()))
				return operation.getMenuItem();
		return null;
	}
	/**
	 * getters
	 */
	public static ArrayList<CollectionOperation> getCollectionSimpleOperationSet() {
		return collectionSimpleOperationSet;
	}

	public static ArrayList<CollectionOperation> getCollectionComputationSet() {
		return collectionComputationSet;
	}

	public static ArrayList<CollectionOperation> getCollectionIOSet() {
		return collectionIOSet;
	}

	public static ArrayList<CollectionOperation> getCollectionOperationSet() {
		return collectionOperationSet;
	}
}
