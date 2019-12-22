package app.io;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class FileHandler {

	public static File getFileFromResources(String path) {
		ClassLoader classLoader = FileHandler.class.getClassLoader();
		URL resource = classLoader.getResource(path);
		if (resource == null) {
			return null;
		} else {
			return new File(resource.getFile());
		}
	}

	public static void writeFile(File file, Collection<String> text) {
		if (file == null || !file.exists())
			return;

		FileWriter writeFile = null;
		BufferedWriter writer = null;
		try {
			writeFile = new FileWriter(file);
			writer = new BufferedWriter(writeFile);

			for (String s : text) {
				writer.write(s);
				writer.newLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	public static void writeFile(String path, WritableImage image) {
		FileHandler.writeFile(new File(path), image);
	}
	
	public static void writeFile(File file, WritableImage image) {
		if (file == null)
			return;
		
		try {
			if (!file.exists())
				file.createNewFile();
			System.out.println("Jep");
	        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
	        ImageIO.write(bufferedImage, "png", file);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	public static ArrayList<String> readFile(String path) {
		return FileHandler.readFile(FileHandler.getFileFromResources(path));
	}

	public static ArrayList<String> readFile(File file) {
		if (file == null || !file.exists())
			return null;

		ArrayList<String> content = new ArrayList<>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			content = reader.lines().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}

		return content;
	}

}
