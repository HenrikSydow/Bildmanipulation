import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ContentPane extends JPanel implements MouseMotionListener {

    private ImageRenderer imageRenderer;
    private PixelDataPanel pixelDataPanel;

    public ContentPane(ImageRenderer imageRenderer, PixelDataPanel pixelDataPanel) {
        this.imageRenderer = imageRenderer;
        this.pixelDataPanel = pixelDataPanel;
        this.setLayout(new BorderLayout());
        this.addMouseMotionListener(this);

        JScrollPane scrollPane = new JScrollPane(imageRenderer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(pixelDataPanel, BorderLayout.SOUTH);
    }

    public ImageRenderer getImageRenderer() {
        return imageRenderer;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        pixelDataPanel.updateCoordinates(imageRenderer.getMouseX(), imageRenderer.getMouseY());
        try {
            pixelDataPanel.updateColorChannels(
                    new Color(imageRenderer.getImage().getRGB(imageRenderer.getMouseX(), imageRenderer.getMouseY()))
            );
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
            // either no image or pixel out of bounds --> do nothing
        }
    }
}
