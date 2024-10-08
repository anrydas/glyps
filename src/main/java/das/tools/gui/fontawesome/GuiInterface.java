package das.tools.gui.fontawesome;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public interface GuiInterface {
    int GLYPHS_PER_LINE = 20;
    int GLYPH_SIZE_MIN_VALUE = 1;
    int GLYPH_SIZE_MAX_VALUE = 5;
    int GLYPH_SIZE_INITIAL_VALUE = 2;
    int DEMO_BUTTON_GLYPH_SIZE = 3;
    int EXECUTOR_THREADS_AMOUNT = 7;
    Color BUTTON_DEFAULT_COLOR = Color.rgb(0xE6, 0xE6, 0xE6);
    Color COLOR_UA_BLUE = Color.rgb(0x00, 0x57, 0xB7);
    Color COLOR_UA_YELLOW = Color.rgb(0xFF, 0xDD, 0x00);
    Color COLOR_UA_RED = Color.rgb(0xAD, 0x15, 0x0F);
    Color COLOR_UA_BLACK = Color.rgb(0x33, 0x33, 0x33);
    String HIGHLIGHT_BUTTON_STYLE = "-fx-effect: dropshadow(three-pass-box, deepskyblue, 10, 0, 0, 0);";

    Insets LINE_BOX_PADDING = new Insets(5, 5, 5, 5);
    Insets GLYPH_CONTROL_PADDING = new Insets(0, 5, 0, 5);
    Insets TOP_BOX_PADDING = new Insets(5, 5, 5, 5);
    int[] MIN_WIDTH_MULTIPLIERS = {28, 26, 24, 22, 20};
    int[] GLYPHS_IN_LINE_COUNTS = {30, 23, 19, 16, 14};
    int HEX_MASK = 0xFFFF;
    int GLYPH_NAMES_FIELD_WIDTH = 130;
    String SCROLL_PANE_ID = "ScrollPane";
    String FONT_COLOR_LABEL_TEXT = "Font color:";
    String BACKGROUND_COLOR_LABEL_TEXT = "Background:";
    String RESET_COLOR_BUTTON_TEXT = "Reset color to default";
    String APPLY_COLORS_BUTTON_TEXT = "Apply colors";
    String SWAP_COLORS_BUTTON_TEXT = "Swap / Reverse colors";
    String BUTTON_SIZE_LABEL_TEXT = "Buttons Sizes: ";
    String GLYPH_NAMES_PROMPT_TEXT = "Search glyph by name";
    String GLYPH_NAMES_TOOLTIP_TEXT = GLYPH_NAMES_PROMPT_TEXT + "\nHit Enter to activate founded Glyph\nEsc - to clear";
    String DEMO_BUTTON_TEXT = "Demo button\n%s: [%03d] [%04Xh]\nClick to copy glyph's name to clipboard\nCtrl + Click to remove this button";
    String DIALOG_TITLE = "Glyph Info";
    String DIALOG_CONTENT_MESSAGE = "Code (DEC / HEX): %03d / %04Xh";
    String BACKGROUND_COLOR_STYLE = "-fx-background-color: #%s;";
    String GLYPH_BUTTON_TOOLTIP_MESSAGE = "%s: [%03d] [%04Xh]\nRight click to copy glyph's name";
    String SHOW_DIALOG_LABEL_TEXT = "Show dialog";
    String SHOW_DIALOG_TOOLTIP = "Show information dialog on glyph clicked";
    String CONST_GLYPH_LABEL_TEXT = "Constant Glyphs";
    String CONST_GLYPH_TOOLTIP = "If checked the constant glyphs amount will be shown in the row when glyph's size have changed.\nOtherwise Glyph's amount will be changed";
    String UA_FLAG_BUTTON_TEXT = "Apply UA Flag colors";
    String OUN_FLAG_BUTTON_TEXT = "Apply OUN UPA Flag colors";

    AnchorPane getGui();
}
