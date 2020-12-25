package engine.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class spritesheetMaker {

	public static void main(String[] args) {
		int anzahl = Integer.parseInt(JOptionPane.showInputDialog("wie viele bilder gibt es"));
		String absoluterPfad = JOptionPane.showInputDialog("absoluter pfad zu bildern");
		List<BufferedImage> images = new ArrayList<>();
		for (int i = 0; i <= anzahl; i++) {
			try {
				String test = absoluterPfad + convertI(i) + ".png";
				images.add(ImageIO.read(new File(test)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = anzahl; i >= 0; i--) {
			try {
				String test = absoluterPfad + convertI(i) + ".png";
				images.add(ImageIO.read(new File(test)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int rows = getRows(images.size());
		int width = images.get(0).getWidth();
		BufferedImage spriteSheet = new BufferedImage(width * rows, width * rows, images.get(0).getType());
		Graphics g = spriteSheet.getGraphics();

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < rows; x++) {
				if (x + y * rows < images.size()) {
					g.drawImage(images.get(x + y * rows), x * width, y * width, null);
				}
			}
		}
		g.dispose();
		File outputfile = new File(JOptionPane.showInputDialog("Name des Spritesheets") + ".png");
		try {
			ImageIO.write(spriteSheet, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getRows(int size) {
		return (int) Math.sqrt(size) + 1;
	}

	private static String convertI(int i) {
		if (i < 10) {
			return "0" + i;
		} else {
			return "" + i;
		}
	}

	public static boolean isPointValue(double d) {
		Double tD = new Double(d);
		String str = tD.toString();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') {
				int ix = Integer.valueOf(str.substring(i + 1, str.length()));
				if (ix > 0) {
					return true;
				}
			}
		}
		return false;
	}

}
