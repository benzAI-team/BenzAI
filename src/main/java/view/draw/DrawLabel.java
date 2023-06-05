package view.draw;

import java.awt.*;

public enum DrawLabel {
    SELECTED, NOT_SELECTED;

    public static Color getColor(DrawLabel drawLabel) {
        if (drawLabel == DrawLabel.SELECTED)
            return Color.DARK_GRAY;
        return Color.WHITE;
    }

    public static DrawLabel next(DrawLabel drawLabel) {
        if (drawLabel == DrawLabel.SELECTED)
            return DrawLabel.NOT_SELECTED;
        return DrawLabel.SELECTED;
    }


}
