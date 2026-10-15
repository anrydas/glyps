package das.tools.gui.fontawesome;

import org.controlsfx.glyphfont.FontAwesome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlyphCodeTest {

    @Test
    void charCodeIsUnicodeCodePointNotOrdinal() {
        assertEquals(0xF05A, Gui.getCharCode(FontAwesome.Glyph.INFO_CIRCLE));
        assertEquals(0xF042, Gui.getCharCode(FontAwesome.Glyph.ADJUST));
    }

    @Test
    void codesAreFormattedAsHex() {
        String text = String.format(GuiInterface.GLYPH_BUTTON_TOOLTIP_MESSAGE, "INFO_CIRCLE",
                Gui.getCharCode(FontAwesome.Glyph.INFO_CIRCLE), Gui.getCharCode(FontAwesome.Glyph.INFO_CIRCLE));
        assertEquals("INFO_CIRCLE: [61530] [F05Ah]\nRight click to copy glyph's name", text);
    }
}
