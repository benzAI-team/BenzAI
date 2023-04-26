package collection_operations;

import java.util.ArrayList;
import java.util.Objects;

import javafx.scene.control.MenuItem;

public class CollectionOperationSet {
	static private final ArrayList<CollectionOperation> collectionSimpleOperationSet = new ArrayList<>();
	static private final ArrayList<CollectionOperation> collectionComputationSet = new ArrayList<>();
	static private final ArrayList<CollectionOperation> collectionIOSet = new ArrayList<>();

	static private final ArrayList<CollectionOperation> collectionOperationSet = new ArrayList<>();

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
		collectionIOSet.add(new CollectionExportGraph());
		collectionIOSet.add(new CollectionExportPNG());
		collectionIOSet.add(new CollectionExportCOM());
		collectionIOSet.add(new CollectionExportCML());
		collectionIOSet.add(new CollectionExport());
		collectionIOSet.add(new CollectionImport());

		collectionComputationSet.add(new LinResonanceEnergyTask());
		collectionComputationSet.add(new LinFanResonanceEnergyTask());
		collectionComputationSet.add(new ClarCoverTask());
		collectionComputationSet.add(new FixedBondClarCoverComputation());
		collectionComputationSet.add(new ForcedSingleClarCoverTask());
		collectionComputationSet.add(new ForcedSingleStatisticsTask());
		collectionComputationSet.add(new AllKekuleStructureComputation());
		collectionComputationSet.add(new RingBoundOrderTask());
		collectionComputationSet.add(new Ims2d1aComputation());
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
