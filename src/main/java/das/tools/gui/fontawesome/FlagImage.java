package das.tools.gui.fontawesome;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FlagImage {
    static final int SIZE = 14;

    public static ImageView getFlagImage(Color color1, Color color2) {
        WritableImage image = new WritableImage(SIZE, SIZE);
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                writer.setColor(x, y, getPixelColor(x, y, SIZE, color1, color2));
            }
        }
        return new ImageView(image);
    }

    /**
     * Square icon split by the anti-diagonal: {@code color1} in the upper-left triangle,
     * {@code color2} in the lower-right one, with a white top-left and gray bottom-right border.
     */
    static Color getPixelColor(int x, int y, int size, Color color1, Color color2) {
        boolean border = x == 0 || y == 0 || x == size - 1 || y == size - 1;
        if (border) {
            boolean whiteEdge = (x == 0 || y == 0) && x < size - 1 && y < size - 1;
            return whiteEdge ? Color.WHITE : Color.GRAY;
        }
        return x + y + 1 >= size ? color2 : color1;
    }
}
