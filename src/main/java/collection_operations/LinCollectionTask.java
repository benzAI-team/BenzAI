package collection_operations;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidPane;
import view.collections.BenzenoidCollectionsManagerPane;

import java.util.ArrayList;

public class LinCollectionTask extends CollectionTask {

	LinCollectionTask() {
		super("Resonance energy (Lin)");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		BenzenoidCollectionPane currentPane = getCollectionManagerPane().getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		setRunning(true);
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "RE Lin";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(getCollectionManagerPane(), getCollectionManagerPane().getBenzenoidSetPanes().size(),
				getCollectionManagerPane().getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		getCollectionManagerPane().getApplication().addTask("RE Lin");

		if (selectedBenzenoidPanes.size() == 0)
			getCollectionManagerPane().selectAll();

		setCalculateService(new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						ArrayList<BenzenoidPane> panes = new ArrayList<>();

						for (BenzenoidPane pane : selectedBenzenoidPanes)
							panes.add(pane);

						setIndex(0);
						int size = panes.size();

						System.out.println("Computing resonance energy of " + size + " benzenoids.");
						getCollectionManagerPane().log("RE Lin (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (isRunning()) {
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getAromaticity();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN);
								setIndex(getIndex() + 1);
								System.out.println(getIndex() + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (getIndex() == 1) {
											getCollectionManagerPane().log(getIndex() + " / " + size, false);
											setLineIndex(currentPane.getConsole().getNbLines() - 1);
										} else
											getCollectionManagerPane().changeLineConsole(getIndex() + " / " + size, getLineIndex());
									}
								});
							}
						}

						return null;
					}

				};
			}
		});

		getCalculateService().stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				BenzenoidCollectionsManagerPane managerPane = getCollectionManagerPane();
				switch (newValue) {
				case FAILED:
					Utils.alert("No selected benzenoid");
					setRunning(false);
					break;

					case CANCELLED:
					case SUCCEEDED:
						// Utils.alert("No selected benzenoid");
					benzenoidSetPane.refresh();
					managerPane.getTabPane().getSelectionModel().clearAndSelect(0);
					managerPane.addBenzenoidSetPane(benzenoidSetPane);
					managerPane.getTabPane().getSelectionModel().clearAndSelect(managerPane.getBenzenoidSetPanes().size() - 2);
					managerPane.getApplication().removeTask("RE Lin");
					setRunning(false);
					break;
				}
			}
		});

		getCalculateService().start();
	}

}
