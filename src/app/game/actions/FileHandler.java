package app.game.actions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class FileHandler {

    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = FileHandler.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            return null;
        } else {
            return new File(resource.getFile());
        }
    }
	
	public static void writeFile(File file, Collection<String> text) {
		if(file == null || !file.exists()) 
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

	public static ArrayList<String> readFile(File file){
		if(file == null || !file.exists()) return null;
		
		ArrayList<String> content = new ArrayList<String>();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) { 
				content.add(line);
				line = reader.readLine();
			}

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
