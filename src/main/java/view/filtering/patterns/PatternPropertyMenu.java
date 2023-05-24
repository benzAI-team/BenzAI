package view.filtering.patterns;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;

class PatternPropertyMenu {

    private CheckMenuItem itemUndisjunct;
    private CheckMenuItem itemDisjunct;
    private CheckMenuItem itemNNDisjunct;
    private CheckMenuItem disableItem;
    private TextField occurencesField;

     Menu build() {
        Menu patternMenu = new Menu("Patterns properties");
        Menu multipleMenu = buildMultipleMenu();
        buildDisableItem();
        buildOccurenceItem();
        patternMenu.getItems().addAll(multipleMenu, disableItem);//-occurenceItem
        return patternMenu;
    }

    private void buildOccurenceItem() {
        // CheckMenuItem occurencesItem = new CheckMenuItem();
        Label occurencesLabel = new Label("Pattern's occurences: ");
        occurencesField = new TextField();
        // HBox occurencesBox = new HBox(3.0);
        // occurencesBox.getChildren().addAll(occurencesLabel, occurencesField);
        // occurencesItem.setGraphic(occurencesBox);
    }

    private void buildDisableItem() {
        disableItem = new CheckMenuItem("Exclude pattern");
        disableItem.setOnAction(e -> {
             if (disableItem.isSelected())
                 unselectAllMenus(itemUndisjunct, itemDisjunct, itemNNDisjunct);
         });
    }

    private Menu buildMultipleMenu() {
        Menu multipleMenu = new Menu("Multiple patterns interaction");
        buildItemUndisjunct();
        buildItemDisjunct();
        buildItemNNDisjunct();
        itemNNDisjunct.setSelected(true);
        multipleMenu.getItems().addAll(itemUndisjunct, itemDisjunct, itemNNDisjunct);
        return multipleMenu;
    }

    private void buildItemUndisjunct() {
        itemUndisjunct = new CheckMenuItem("Undisjunct patterns");
        itemUndisjunct.setOnAction(e -> {
            itemUndisjunct.setSelected(true);
            if (itemUndisjunct.isSelected())
                unselectAllMenus(itemDisjunct, itemNNDisjunct, disableItem);
        });
    }

    private void buildItemDisjunct() {
        itemDisjunct = new CheckMenuItem("Disjunct patterns");
        itemDisjunct.setOnAction(e -> {
            itemDisjunct.setSelected(true);
            if (itemDisjunct.isSelected())
                unselectAllMenus(itemUndisjunct, itemNNDisjunct, disableItem);
        });
    }

    private void buildItemNNDisjunct() {
        itemNNDisjunct = new CheckMenuItem("Disjunct on neutral/negative hexagons");
        itemNNDisjunct.setOnAction(e -> {
            itemNNDisjunct.setSelected(true);
            if (itemNNDisjunct.isSelected())
                unselectAllMenus(itemUndisjunct, itemDisjunct, disableItem);
        });
    }

    private void unselectAllMenus(CheckMenuItem... items) {
        for (CheckMenuItem item : items) {
            item.setSelected(false);
        }
    }

    CheckMenuItem getItemUndisjunct() {
        return itemUndisjunct;
    }

    CheckMenuItem getItemDisjunct() {
        return itemDisjunct;
    }

    CheckMenuItem getItemNNDisjunct() {
        return itemNNDisjunct;
    }

    CheckMenuItem getDisableItem() {
        return disableItem;
    }

    TextField getOccurencesField() {
        return occurencesField;
    }
}