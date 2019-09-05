package iosifcostin.MathArena.mathMl;


import fmath.ApplicationConfiguration;
import fmath.components.MathMLFormula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class GenerateImage {

	private static File fileMathML = new File("mathml/test");
	private static String folderGeneratedImage = "images";

	private static String folderFonts = "fonts";
	private static String folderGlyps = "glyphs";

	public static void main(String[] args) throws IOException  {
		
		ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
		ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyps);
		ApplicationConfiguration.setWebApp(false);

		String mathml = getFileAsString(fileMathML);
		MathMLFormula formula = new MathMLFormula();
		BufferedImage img = formula.drawImage(mathml);

		File file = new File(folderGeneratedImage + "/img"+System.currentTimeMillis()+".png");
		ImageIO.write(img, "png", file);
		System.out.println("--> Image generated in folder:" + folderGeneratedImage);

	}

	
	private static String getFileAsString(File fileSource) throws FileNotFoundException {
		Scanner scanner = null;
		StringBuffer sb = new StringBuffer();
		try {
			scanner = new Scanner(new FileInputStream(fileSource), "UTF-8");
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine() + "\n");
			}
		} finally {
			if (scanner != null)
				scanner.close();
		}

		return sb.toString();
	}

}
