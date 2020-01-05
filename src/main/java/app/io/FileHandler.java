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

/**
 * A handler that can write or read files.
 */
public class FileHandler {

	/**
	 * Returns a File from the resource folder.
	 * 
	 * @param path The path of the file relative to the resource folder.
	 * @return A file object if a file with the name path exists in the resource
	 *         folder, otherwise null is returned.
	 */
	public static File getFileFromResources(String path) {

		ClassLoader classLoader = FileHandler.class.getClassLoader();
		URL resource = classLoader.getResource(path);

		if (resource == null) {
			return null;

		} else {
			return new File(resource.getFile());
		}
	}

	/**
	 * Writes text into a file.
	 * 
	 * @param file The file to write the text into. If this file does not exist,
	 *             nothing happens.
	 * @param text The text to be written into the file as a Collection. Each entry
	 *             corresponds to one line in the file. The order of the lines is
	 *             determined by the iterator of the collection.
	 */
	public static void writeFile(File file, Collection<String> text) {

		// Check if file exists
		if (file == null || !file.exists()) {
			return;
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

			// Write text line by line into file
			for (String s : text) {
				writer.write(s);
				writer.newLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String path, BufferedImage image) {
		FileHandler.writeFile(new File(path), image);
	}

	public static void writeFile(File file, BufferedImage image) {

		if (file == null) {
			return;
		}

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			ImageIO.write(image, "png", file);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static ArrayList<String> readFile(String path) {
		return FileHandler.readFile(FileHandler.getFileFromResources(path));
	}

	/**
	 * Returns the contents of a file as a List of Strings.
	 * 
	 * @param file The file to read the content from. If file is null or does not
	 *             exist, null is also returned
	 * @return The contents of the file as a list of Strings. Each line corresponds
	 *         to one entry in the resulting List. The first line is at position 0
	 *         in the list, etc.
	 */
	public static ArrayList<String> readFile(File file) {

		// Check if file exists
		if (file == null || !file.exists()) {
			return null;
		}

		ArrayList<String> content = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			// Read lines and store them into content
			content = reader.lines().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}

}
