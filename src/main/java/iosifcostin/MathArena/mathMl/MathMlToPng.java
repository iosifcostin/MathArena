package iosifcostin.MathArena.mathMl;

import fmath.ApplicationConfiguration;
import fmath.components.MathMLFormula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;


public class MathMlToPng {

    private static String folderFonts = "fonts";
    private static String folderGlyphs = "glyphs";

    public void convertMathMl(String descriptionFileName, String resultFileName, String matmlDescription, String matmlResult) {

        String folderGeneratedImage = "src/main/resources/static/problemsImages/";

//        String folderFonts = "http://localhost:8080/mathml/fonts/stix/descriptor.properties";
        ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
//        String folderGlyphs = "http://localhost:8080/mathml/glyphs";
        ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyphs);
        ApplicationConfiguration.setWebApp(false);

        try {
            MathMLFormula formula = new MathMLFormula();
            BufferedImage imgDescription = formula.drawImage(matmlDescription);
            BufferedImage imgResult = formula.drawImage(matmlResult);
            File description = new File(folderGeneratedImage + descriptionFileName);
            ImageIO.write(imgDescription, "png", description);
            File result = new File(folderGeneratedImage + resultFileName);
            ImageIO.write(imgResult, "png", result);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
