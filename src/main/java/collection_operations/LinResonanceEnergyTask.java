package collection_operations;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import molecules.Benzenoid;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class LinResonanceEnergyTask extends CollectionTask {

	LinResonanceEnergyTask() {
		super("Resonance energy (Lin)");
	}

	@Override
	public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
		BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		setOperationIsRunning(true);
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();
		if (selectedBenzenoidPanes.size() == 0)
			collectionManagerPane.selectAll();

		String name = "RE Lin";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
				collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));
		collectionManagerPane.getApplication().addTask("RE Lin");
		setCalculateService(new Service<>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<>() {

					@Override
					protected Void call() {

						ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

						setIndex(0);
						int size = panes.size();

						System.out.println("Computing resonance energy of " + size + " benzenoids.");
						collectionManagerPane.log("RE Lin (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (operationIsRunning()) {
								Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getAromaticity();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN);
								setIndex(getIndex() + 1);
								System.out.println(getIndex() + " / " + size);

								Platform.runLater(() -> {
									if (getIndex() == 1) {
										collectionManagerPane.log(getIndex() + " / " + size, false);
										setLineIndex(currentPane.getConsole().getNbLines() - 1);
									} else
										collectionManagerPane.changeLineConsole(getIndex() + " / " + size, getLineIndex());
								});
							}
						}

						return null;
					}

				};
			}
		});

		getCalculateService().stateProperty().addListener((observable, oldValue, newValue) -> {
			switch (newValue) {
			case FAILED:
				Utils.alert("No selected benzenoid");
				setOperationIsRunning(false);
				break;
				case CANCELLED:
				case SUCCEEDED:
					// Utils.alert("No selected benzenoid");
				addNewSetPane(benzenoidSetPane, collectionManagerPane);
				collectionManagerPane.getApplication().removeTask("RE Lin");
				setOperationIsRunning(false);
				break;
				//default:
					//throw new IllegalStateException("Unexpected value: " + newValue);
			}
		});

		getCalculateService().start();
	}

}
