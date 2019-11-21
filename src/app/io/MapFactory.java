package app.io;

import java.awt.Color;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import app.events.E_Outbreak;
import app.game.City;
import app.game.Game;

public class MapFactory {
	public static void createMap(Game game) {
		try {
			// Dimensions
			int width = 3600, height = 1800;
 
			// Create image
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  
			// Get canvas
			Graphics2D g2d = bi.createGraphics();
 
			// Fill background
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, width, height);
  
			// Prettier lines
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
 
			// Font for city names
			Font font = new Font("TimesRoman", Font.BOLD, 20);
			g2d.setFont(font);
  
			for(City c : game.getCities().values()) {
				// Draw city circle
				// Coordinates
				int prev = 0;
				for(E_Outbreak e: game.getOutbreakEvents()) {
					if(e.getCity() == c) prev = (int) (255 * e.getPrevalence());
				}
				Color red = new Color(prev, 0, 0, 100);
				g2d.setColor(red);
				int x = (int) c.getX() + 180, y = (int) - c.getY() + 90;
				int diameter = c.getPopulation() / 100;
				x *= 10;
				y *= 10;
				// Inner
				Ellipse2D.Double circle = new Ellipse2D.Double(x - diameter / 2, y - diameter / 2, diameter, diameter);
				g2d.fill(circle);
				//Outer
				g2d.setColor(Color.BLACK);
				g2d.drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter);
				
				//Draw name
				FontMetrics fontMetrics = g2d.getFontMetrics();
		    	int stringWidth = fontMetrics.stringWidth(c.getName());
		    	int stringHeight = fontMetrics.getAscent();
				g2d.drawString(c.getName(), x - stringWidth / 2, y + stringHeight / 4);
			}

			ImageIO.write(bi, "PNG", new File("Map" + game.getRound() +".PNG"));
	      
		} catch (IOException ie) {
	    	ie.printStackTrace();
	    }
	}
}
