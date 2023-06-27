import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame implements ActionListener {

    public static final String
            OPEN_BUTTON_CLICKED = "MainFrame_openButtonClicked",
            SAVE_BUTTON_CLICKED = "MainFrame_saveButtonClicked",
            GRAY_SCALE_BUTTON_CLICKED = "MainFrame_grayScaleButtonClicked",
            BINARY_TRESHOLD_CLICKED = "MainFrame_binaryThresholdClicked",
            BINARY_CLICKED = "MainFrame_binaryClicked",
            BINARY_JOHNSON_METHOD = "MainFrame_binaryJohnsonMethod";

    public MainFrame(BufferedImage image) {
        ContentPane newContentPane = CompositionRoot.createContentPane();
        JFrame newFrame = new MainFrame(newContentPane);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newContentPane.getImageRenderer().showImage(image);
        JMenu fileMenu = newFrame.getJMenuBar().getMenu(0);
        fileMenu.remove(0);
        newFrame.getJMenuBar().remove(1);
    }

    public MainFrame(ContentPane contentPane) {
        super("Henriks Bildbearbeitung");
        this.setContentPane(contentPane);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Datei");
        JMenu imageMenu = new JMenu("Bild");
        JMenuItem openMenuItem = new JMenuItem("Öffnen");
        openMenuItem.setActionCommand(MainFrame.OPEN_BUTTON_CLICKED);
        openMenuItem.addActionListener(this);
        JMenuItem saveMenuItem = new JMenuItem("Speichern");
        saveMenuItem.setActionCommand(MainFrame.SAVE_BUTTON_CLICKED);
        saveMenuItem.addActionListener(this);
        JMenuItem grayScaleItem = new JMenuItem("Graustufen");
        grayScaleItem.setActionCommand(MainFrame.GRAY_SCALE_BUTTON_CLICKED);
        grayScaleItem.addActionListener(this);
        JMenuItem binaryTresholdItem = new JMenuItem("Binär - Manueller Schwellwert");
        binaryTresholdItem.setActionCommand(MainFrame.BINARY_TRESHOLD_CLICKED);
        binaryTresholdItem.addActionListener(this);
        JMenuItem binaryItem = new JMenuItem("Binär - Schwellwert");
        binaryItem.setActionCommand(MainFrame.BINARY_CLICKED);
        binaryItem.addActionListener(this);
        JMenuItem binaryJohnsonItem = new JMenuItem("Binär - Johnson Methode");
        binaryJohnsonItem.setActionCommand(MainFrame.BINARY_JOHNSON_METHOD);
        binaryJohnsonItem.addActionListener(this);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        imageMenu.add(grayScaleItem);
        imageMenu.add(binaryItem);
        imageMenu.add(binaryTresholdItem);
        imageMenu.add(binaryJohnsonItem);
        menuBar.add(fileMenu);
        menuBar.add(imageMenu);
        this.setJMenuBar(menuBar);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // this.setResizable(false);
        this.setSize(new Dimension(500, 500));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void doOpen() {
        LookAndFeel previousLookAndFeel = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) { }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedImage = fileChooser.getSelectedFile();
            ((ContentPane) this.getContentPane()).getImageRenderer().showImageFrom(selectedImage);
        }
        try {
            UIManager.setLookAndFeel(previousLookAndFeel);
        } catch (UnsupportedLookAndFeelException ignored) { }
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void doSave() {
        LookAndFeel previousLookAndFeel = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) { }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            BufferedImage currentImage = ((ContentPane) this.getContentPane()).getImageRenderer().getImage();
            try {
                ImageIO.write(currentImage, "PNG", fileToSave);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            UIManager.setLookAndFeel(previousLookAndFeel);
        } catch (UnsupportedLookAndFeelException ignored) { }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ContentPane contentPane = (ContentPane) this.getContentPane();
        BufferedImage img = contentPane.getImageRenderer().getImage();
        ImageRenderer imgRenderer = contentPane.getImageRenderer();

        switch (e.getActionCommand()) {
            case MainFrame.OPEN_BUTTON_CLICKED -> doOpen();
            case MainFrame.SAVE_BUTTON_CLICKED -> doSave();
            case MainFrame.GRAY_SCALE_BUTTON_CLICKED -> new MainFrame(ImageManipulator.GRAYSCALE(img));
            case MainFrame.BINARY_TRESHOLD_CLICKED ->  new MainFrame(
                    ImageManipulator.BINARY(
                            img,
                            Integer.parseInt(JOptionPane.showInputDialog("Schwellwert für Binär-Bild angeben:"))
                    )
            );
            case MainFrame.BINARY_CLICKED -> new MainFrame(ImageManipulator.BINARY(img));
            case MainFrame.BINARY_JOHNSON_METHOD -> {
                imgRenderer.setMode(ImageRenderer.JOHNSON_REGION);
                imgRenderer.setJohnsonRegionRadius(
                        Integer.parseInt(
                                JOptionPane.showInputDialog("Radius der Johnson Region:")
                        )
                );
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while ( !imgRenderer.isJohnsonTriggered() ) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        new MainFrame(
                                ImageManipulator.BINARY_JOHNSON(
                                        img,
                                        imgRenderer.getJohnsonRegionPoints(),
                                        imgRenderer.getJohnsonRegionRadius()
                                )
                        );
                        imgRenderer.resetJohnsonTrigger();
                        imgRenderer.setMode(ImageRenderer.DEFAULT);
                    }
                }).start();
            }
        }
    }
}
