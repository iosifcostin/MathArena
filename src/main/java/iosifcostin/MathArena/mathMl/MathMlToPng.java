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

    public String convertMathMl(String mathml) {


//        String folderFonts = "http://localhost:8080/mathml/fonts/stix/descriptor.properties";
        ApplicationConfiguration.setFolderUrlForFonts(folderFonts);
//        String folderGlyphs = "http://localhost:8080/mathml/glyphs";
        ApplicationConfiguration.setFolderUrlForGlyphs(folderGlyphs);
        ApplicationConfiguration.setWebApp(false);

        MathMLFormula formula = new MathMLFormula();
        BufferedImage img = formula.drawImage(mathml);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(os.toByteArray());

    }


}
