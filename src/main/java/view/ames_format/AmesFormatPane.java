package view.ames_format;

import application.BenzenoidApplication;
import javafx.scene.layout.ColumnConstraints;
import benzenoid.Geometry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Triplet;
import utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AmesFormatPane extends GridPane {

    private static final int AREAS_HEIGHT = 1000;

    private final BenzenoidApplication application;
    private ListView<LogFileBox> listView;
    private List<LogFileBox> logFileBoxes;
    private Button addButton;
    private Button saveButton;
    private TextArea textArea;
    private int index;

    public AmesFormatPane(BenzenoidApplication application) throws IOException {
        super();
        this.application = application;
        index = 0;
        initialize();
    }

    private void initialize() throws IOException {
        this.setPrefWidth(1400);
        this.setPadding(new Insets(50));
        this.setHgap(5);
        this.setVgap(5);

        listView = new ListView<>();
        logFileBoxes = new ArrayList<>();
        textArea = new TextArea();

        refreshListView();

        addButton = new Button("Add");
        saveButton = new Button("Save as");

        VBox vBoxLeft = new VBox(5.0);
        vBoxLeft.getChildren().addAll(listView, addButton);

        addButton.setPrefWidth(listView.getPrefWidth());

        this.add(vBoxLeft, 0, 0);

        VBox vBoxRight = new VBox(5.0);
        textArea.setPrefHeight(AREAS_HEIGHT);
        vBoxRight.getChildren().addAll(textArea, saveButton);
        saveButton.setPrefWidth(textArea.getPrefWidth());
        listView.setPrefHeight(AREAS_HEIGHT);
        textArea.setEditable(false);

        this.add(vBoxRight, 1, 0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        this.getColumnConstraints().addAll(col1, col2);

        setActions();
    }

    private void setActions() {
        addButton.setOnAction(e -> {
            try {
                addFile();
            }
            catch(IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void refreshListView() throws IOException{
        ObservableList<LogFileBox> items = FXCollections.observableArrayList(logFileBoxes);
        listView.setItems(items);

        String amesFormat = buildAmesFormat();
        textArea.setText(amesFormat);
    }

    private void addFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(application.getStage());

        if (file != null && file.getName().endsWith(".log")) {
            LogFileBox logFileBox = new LogFileBox(this, file, index);
            logFileBoxes.add(logFileBox);
            refreshListView();
            index ++;
        }

        else
            Utils.alert("Please assign a valid file (Gaussian .log)");
    }

    private String buildAmesFormat() throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("<pahdatabase database=\"theoretical\" version=\"3.00\" date=\"2017-08-18\" full=\"false\">\n");

        builder.append("<comment>This file was generated with BenzAI software.</comment>\n");

        builder.append("<species>\n");

        int uid = 0;

        for (LogFileBox logFileBox : logFileBoxes) {

            builder.append("\t<specie uid=\"" + uid + "\">\n");
            uid ++;

            File file = logFileBox.getFile();
            Geometry geometry = retrieveGeometry(file);
            ResultLogFile resultLogFile = SpectrumsComputer.parseLogFile(file.getAbsolutePath());

            builder.append("\t\t<comments>\n");
            builder.append("\t\t\t<comment># b3lyp/6-31g opt freq</comment>\n");
            builder.append("\t\t</comments>\n");

            builder.append("\t\t<formula>C" + geometry.getNbCarbons() + "H" + geometry.getNbHydrogens() + "</formula>\n");
            builder.append("\t\t<charge>0</charge>\n");
            builder.append("\t\t<method>B3LYP</method>\n");

            //Unable to retrieve groups data from a simple .log file
            builder.append("\t\t<n_solo>" + 0 + "</n_solo>\n");
            builder.append("\t\t<n_duo>" + 0 + "</n_duo>\n");
            builder.append("\t\t<n_trio>" + 0 + "</n_trio>\n");
            builder.append("\t\t<n_quartet>" + 0 + "</n_quartet>\n");
            builder.append("\t\t<n_quintet>" + 0 + "</n_quintet>\n");

            builder.append("\t\t<geometry>\n");

            int position = 1;

            for(Triplet<Double, Double, Double> carbon : geometry.getCarbons()) {
                builder.append("\t\t\t<atom>\n");
                builder.append("\t\t\t\t<position>" + position + "</position>\n");
                builder.append("\t\t\t\t<x>" + carbon.getX() + "</x>\n");
                builder.append("\t\t\t\t<y>" + carbon.getY() + "</y>\n");
                builder.append("\t\t\t\t<z>" + carbon.getZ() + "</z>\n");
                builder.append("\t\t\t\t<type>" + 6 + "</type>\n");
                builder.append("\t\t\t</atom>\n");
                position ++;
            }

            for(Triplet<Double, Double, Double> hydrogen : geometry.getHydrogens()) {
                builder.append("\t\t\t<atom>\n");
                builder.append("\t\t\t\t<position>" + position + "</position>\n");
                builder.append("\t\t\t\t<x>" + hydrogen.getX() + "</x>\n");
                builder.append("\t\t\t\t<y>" + hydrogen.getY() + "</y>\n");
                builder.append("\t\t\t\t<z>" + hydrogen.getZ() + "</z>\n");
                builder.append("\t\t\t\t<type>" + 1 + "</type>\n");
                builder.append("\t\t\t</atom>\n");
                position ++;
            }

            builder.append("\t\t</geometry>\n");

            builder.append("\t\t<transitions>\n");

            for (int i = 0 ; i < resultLogFile.getFrequencies().size() ; i++) {
                Double frequency = resultLogFile.getFrequency(i);
                Double intensity = resultLogFile.getIntensity(i);

                builder.append("\t\t\t<mode>\n");
                builder.append("\t\t\t\t<frequency>" + frequency + "</frequency>\n");
                builder.append("\t\t\t\t<intensity>" + intensity + "</intensity>\n");
                builder.append("\t\t\t\t<symmetry>unknown</symmetry>\n");
                builder.append("\t\t\t</mode>\n");
            }

            builder.append("\t\t</transitions>\n");


            builder.append("\t</specie>\n");

        }
        builder.append("</species>\n");
        builder.append("</pahdatabase>\n");

        return builder.toString();
    }

    private Geometry retrieveGeometry(File file) throws IOException {

        List<String> lines = Utils.getLinesFromFile(file);
        Geometry geometry = new Geometry();

        int position = 0;

        for (int i = 0 ; i < lines.size() ; i++) {
            String line = lines.get(i);
            if (line.contains("Standard orientation:"))
                position = i;
        }

        position += 5;

        String line = lines.get(position);
        while(!line.contains("----------------")) {
            String [] split = Utils.splitBySeparators(line);

            int type = Integer.parseInt(split[2]);
            double x = Double.parseDouble(split[4]);
            double y = Double.parseDouble(split[5]);
            double z = Double.parseDouble(split[6]);

            Triplet<Double, Double, Double> atom = new Triplet<>(x, y, z);

            if (type == 1)
                geometry.addHydrogen(atom);
            else if (type == 6)
                geometry.addCarbon(atom);

            position ++;
            line = lines.get(position);
        }

        return geometry;
    }

    public void deleteFile(int index) throws IOException {
        logFileBoxes.remove(index);
        refreshListView();
    }
}
