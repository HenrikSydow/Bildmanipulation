import jdk.jshell.spi.ExecutionControl;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ImageManipulator {

    public static BufferedImage RESIZE_BOUNDS(BufferedImage img, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, img.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return newImage;
    }

    public static BufferedImage FILL_MAGENTA(BufferedImage img) {
        BufferedImage newImage = RESIZE_BOUNDS(img, img.getWidth(), img.getHeight() * 2);
        Graphics2D g2d = newImage.createGraphics();
        g2d.setColor(Color.MAGENTA);
        g2d.fillRect(0, img.getHeight(), img.getWidth(), img.getHeight());
        g2d.dispose();
        return newImage;
    }

    public static BufferedImage GRAYSCALE(BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return newImage;
    }

    public static int RGB_TO_GRAYSCALE(int rgbValue) {
        Color color = new Color(rgbValue);
        return (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
    }

    public static int GRAYSCALE_TO_RGB(int grayscale) {
        return (grayscale << 16) | (grayscale << 8) | grayscale | (255 << 24);
    }

    public static BufferedImage BINARY(BufferedImage img) {
        int lowestPx = RGB_TO_GRAYSCALE(img.getRGB(0, 0));
        int highestPx = RGB_TO_GRAYSCALE(img.getRGB(0, 0));
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int thisPx = RGB_TO_GRAYSCALE(img.getRGB(x, y));
                if (thisPx < lowestPx)  lowestPx = thisPx;
                if (thisPx > highestPx) highestPx = thisPx;
            }
        }
        int threshold = (highestPx + lowestPx) / 2;
        return BINARY(img, threshold);
    }

    public static BufferedImage BINARY(BufferedImage img, int threshold) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixelGrayscaleValue = RGB_TO_GRAYSCALE(img.getRGB(x, y));
                if (pixelGrayscaleValue <= threshold) {
                    newImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    newImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return newImage;
    }

    private static int GET_GRAYSCALE_MIDDLE(BufferedImage img) {
        int[] grayscaleCount = new int[256];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                grayscaleCount[RGB_TO_GRAYSCALE(img.getRGB(x, y))]++;
            }
        }
        int maxValue = 0;
        for (int i = 0; i < grayscaleCount.length; i++)
            maxValue = (grayscaleCount[i] > grayscaleCount[maxValue]) ? i : maxValue;
        return maxValue;
    }

    public static BufferedImage BINARY_JOHNSON(BufferedImage img, Point[] johnsonRegionPoints, int radius) {
        BufferedImage firstSubImage = img.getSubimage(
                johnsonRegionPoints[0].x - radius / 2,
                johnsonRegionPoints[0].y - radius / 2,
                radius,
                radius
        );
        BufferedImage secondSubImage = img.getSubimage(
                johnsonRegionPoints[1].x - radius / 2,
                johnsonRegionPoints[1].y - radius / 2,
                radius,
                radius
        );
        int threshold = (GET_GRAYSCALE_MIDDLE(firstSubImage) + GET_GRAYSCALE_MIDDLE(secondSubImage)) / 2;
        return BINARY(img, threshold);
    }

    public static BufferedImage RESIZE_IMAGE(BufferedImage img, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, img.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return newImage;
    }

}
