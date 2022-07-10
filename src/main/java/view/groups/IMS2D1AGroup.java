package view.groups;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import database.PictureConverter;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IMS2D1AGroup extends Group {

	private String pictureData;
	
	public IMS2D1AGroup(String pictureData) {
		this.pictureData = pictureData;
		try {
			buildImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void buildImage() throws IOException {
		
		if (pictureData != null) {
			
			File file = writeFile();		
			InputStream stream = new FileInputStream("map.png");
	    	Image image = new Image(stream);
	    	ImageView imageView = new ImageView();
	    	imageView.setImage(image);
			super.getChildren().add(imageView);
			file.delete();
		}
		
		else {
			Image image = new Image("/resources/graphics/unknown.png");
			ImageView imageView = new ImageView();
	    	imageView.setImage(image);
			super.getChildren().add(imageView);
		}
	}
	
	private File writeFile() throws IOException {
		
		PictureConverter.stringToPng(pictureData, "map.png");
		return new File("map.png");
	}
	
	public static void main(String [] args) {
		try {
			System.out.println(PictureConverter.pngToString("/home/adrien/Téléchargements/unknown.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
