import javax.swing.*;
import java.awt.*;

public class PixelDataPanel extends JPanel {

    private JLabel
            corLabel = new JLabel("[x: ,y: ]"),
            rgbLabel = new JLabel("[r,g,b]");

    public PixelDataPanel() {
        this.setLayout(new FlowLayout());
        this.add(corLabel);
        this.add(rgbLabel);
    }

    public void updateCoordinates(int x, int y) {
        corLabel.setText(String.format("[x: %s, y: %s]", x, y));
    }

    public void updateColorChannels(Color color) {
        rgbLabel.setText(String.format("[r%s,g%s,b%s]", color.getRed(), color.getGreen(), color.getBlue()));
    }

}
