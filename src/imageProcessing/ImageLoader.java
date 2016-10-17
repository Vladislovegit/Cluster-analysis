package imageProcessing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static BufferedImage load(String path) {
        try {
            return ImageIO.read(new File("images/src/" + path + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Integer[][] res, String fileName, BufferedImage image) {
        save(res, res, res, fileName, image);
    }

    public static void saveClusters(Integer[][] res, String filename, BufferedImage image, Integer zonesAmount) {
        Integer[][] reds = new Integer[image.getWidth()][image.getHeight()];
        Integer[][] greens = new Integer[image.getWidth()][image.getHeight()];
        Integer[][] blues = new Integer[image.getWidth()][image.getHeight()];
        for (int i = 0; i < zonesAmount; i++) {
            Integer red = (int) (Math.random() * 256);
            Integer green = 255 - red;
            Integer blue = (int) (Math.random() * 256);
            for (int j = 0; j < res.length; j++) {
                for (int k = 0; k < res[0].length; k++) {
                    if (!res[j][k].equals(0)) {
                        if (res[j][k].equals(i + 1)) {
                            reds[j][k] = red;
                            greens[j][k] = green;
                            blues[j][k] = blue;
                        }
                    } else {
                        reds[j][k] = 0;
                        greens[j][k] = 0;
                        blues[j][k] = 0;
                    }
                }
            }
        }
        save(reds, greens, blues, filename, image);
    }

    public static void save(Integer[][] red, Integer[][] green, Integer[][] blue, String fileName, BufferedImage image) {
        BufferedImage resImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                resImage.setRGB(i, j, new Color(red[i][j], green[i][j], blue[i][j]).getRGB());
            }
        }

        File outputFile = new File(fileName + ".jpg");
        try {
            ImageIO.write(resImage, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
