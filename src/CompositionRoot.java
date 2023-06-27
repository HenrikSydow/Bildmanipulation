
public class CompositionRoot {

    public static ContentPane createContentPane() {
        return new ContentPane(
                new ImageRenderer(),
                new PixelDataPanel()
        );
    }

    public static void main(String[] args) {
        new MainFrame(createContentPane());
    }
}
