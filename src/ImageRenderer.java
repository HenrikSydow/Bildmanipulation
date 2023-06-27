import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageRenderer extends JPanel implements MouseMotionListener, MouseInputListener {

    public static final int
            DEFAULT = 0,
            JOHNSON_REGION = 1;

    private BufferedImage image;
    private int mouseX, mouseY;
    private int mode;
    private int johnsonRegionRadius;
    private Point[] johnsonRegionPoints = new Point[] {null, null};

    public ImageRenderer() {
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setMode(ImageRenderer.DEFAULT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        } catch (NullPointerException e) {
            // No image, nothing to do.
        }

        switch (mode) {
            case ImageRenderer.JOHNSON_REGION -> {
                g.setColor(Color.YELLOW);
                if (johnsonRegionPoints[0] != null) {
                    g.drawRect(
                            johnsonRegionPoints[0].x - johnsonRegionRadius / 2,
                            johnsonRegionPoints[0].y - johnsonRegionRadius / 2,
                            johnsonRegionRadius,
                            johnsonRegionRadius
                    );
                }
                if (johnsonRegionPoints[1] != null) {
                    g.drawRect(
                            johnsonRegionPoints[1].x - johnsonRegionRadius / 2,
                            johnsonRegionPoints[1].y - johnsonRegionRadius / 2,
                            johnsonRegionRadius,
                            johnsonRegionRadius
                    );
                }
            }
        }
    }

    public void showImageFrom(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
            showImage(image);
        } catch(IOException e) {
            // Nothing to do.
        }
    }

    public void showImage(BufferedImage image) {
        this.image = image;
        this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        this.repaint();
        this.revalidate();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isJohnsonTriggered() {
        return (johnsonRegionPoints[0] != null && johnsonRegionPoints[1] != null);
    }

    public void resetJohnsonTrigger() {
        johnsonRegionPoints[0] = null;
        johnsonRegionPoints[1] = null;
    }

    public Point[] getJohnsonRegionPoints() {
        return johnsonRegionPoints;
    }

    public int getJohnsonRegionRadius() {
        return this.johnsonRegionRadius;
    }

    public void setJohnsonRegionRadius(int radius) {
        this.johnsonRegionRadius = radius;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        // viewport -> jscrollpanel -> contentpane
        getParent().getParent().getParent().dispatchEvent(e);
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (mode) {
            case ImageRenderer.JOHNSON_REGION -> {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    johnsonRegionPoints[0] = e.getPoint();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    johnsonRegionPoints[1] = e.getPoint();
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /*
    private BufferedImage createScaledImage() {
        double widthRatio = (double) this.getWidth() / this.image.getWidth();
        double heightRatio = (double) this.getHeight() / this.image.getHeight();
        double smallestRatio = Math.min(widthRatio, heightRatio);
        int newWidth = (int) (this.image.getWidth() * smallestRatio);
        int newHeight = (int) (this.image.getHeight() * smallestRatio);
        return ImageManipulator.RESIZE_IMAGE(this.image, newWidth, newHeight);
    }
    */
}
