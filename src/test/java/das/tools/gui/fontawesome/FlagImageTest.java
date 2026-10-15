package das.tools.gui.fontawesome;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlagImageTest {
    private static final int SIZE = FlagImage.SIZE;

    private static Color pixel(int x, int y) {
        return FlagImage.getPixelColor(x, y, SIZE, Color.BLUE, Color.YELLOW);
    }

    @Test
    void upperLeftTriangleUsesFirstColor() {
        assertEquals(Color.BLUE, pixel(1, 1));
        assertEquals(Color.BLUE, pixel(SIZE - 3, 1));
    }

    @Test
    void lowerRightTriangleUsesSecondColor() {
        assertEquals(Color.YELLOW, pixel(SIZE - 2, SIZE - 2));
        assertEquals(Color.YELLOW, pixel(SIZE - 2, 1));
    }

    @Test
    void borderIsWhiteTopLeftAndGrayBottomRight() {
        assertEquals(Color.WHITE, pixel(0, 5));
        assertEquals(Color.WHITE, pixel(5, 0));
        assertEquals(Color.GRAY, pixel(SIZE - 1, 5));
        assertEquals(Color.GRAY, pixel(5, SIZE - 1));
    }
}
