package properties.database;

import properties.*;
import view.database.ChoiceBoxDatabaseCriterion;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;
import java.util.Objects;

public class DatabasePropertySet extends PropertySet{
    public DatabasePropertySet(){
        setPropertyList(new ArrayList<>());
        add(new HexagonNumberProperty());
        add(new CarbonNumberProperty());
        add(new HydrogenNumberProperty());
        add(new IrregularityProperty());
    }

    public HBoxCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion, String name) {
        Property property = getByName(name);
        return Objects.requireNonNull(property).makeHBoxCriterion(parent, choiceBoxCriterion);
    }

}
