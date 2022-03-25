package view.collections;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

public class Console extends TextArea {

	public Console() {
		super();
		this.setEditable(false);

		clear();

		Console console = this;

		this.textProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				console.setScrollTop(Double.MAX_VALUE);
			}
		});

	}

//	public void clearConsole() {
//		this.setText("");
//	}

	public void append(String content, boolean displayDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());

		String timestamp = "[" + formatter.format(date) + "]";

		if (displayDate)
			this.appendText(timestamp + " " + content + "\n");
		else
			this.appendText(content + "\n");

		System.out.print("");
	}

	public void changeLine(String line, int lineIndex) {

		this.setScrollTop(Double.MAX_VALUE);
		String[] split = getText().split("\n");
		// split[lineIndex] = line;
		split[split.length - 1] = line;

		clear();

		StringBuilder builder = new StringBuilder();

		for (String str : split)
			builder.append(str + "\n");

		this.appendText(builder.toString() + "\n");
		this.setScrollTop(Double.MAX_VALUE);
	}

	public int getNbLines() {
		return getText().split("\n").length;
	}
}
