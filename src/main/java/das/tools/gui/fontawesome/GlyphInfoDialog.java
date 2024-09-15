package das.tools.gui.fontawesome;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;

public class GlyphInfoDialog extends Dialog<ButtonType> {
    private GlyphFont glyphFont;
    private FontAwesome.Glyph glyph;
    public GlyphInfoDialog(GlyphFont glyphFont, FontAwesome.Glyph glyph) {
        this.glyphFont = glyphFont;
        this.glyph = glyph;
        DialogPane root = this.getDialogPane();
        root.getButtonTypes().addAll(ButtonType.OK);
        root.setHeader(getHeaderPane());
        root.setContentText("Placeholder content");
    }

    private AnchorPane getHeaderPane() {
        HBox textBox = getTextBox();
        HBox imageBox = getImageBox(glyphFont);
        AnchorPane headerPane = new AnchorPane(textBox, imageBox);
        headerPane.getStyleClass().addAll("header-panel");
        return headerPane;
    }

    private HBox getTextBox() {
        Label headerGlyph = new Label("", glyphFont.create(glyph).sizeFactor(2));
        Label headerText = new Label(glyph.name());
        HBox box = new HBox(10, headerGlyph, headerText);
        box.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setTopAnchor(box, 5.0);
        AnchorPane.setLeftAnchor(box, 5.0);
        return box;
    }

    private HBox getImageBox(GlyphFont glyphFont) {
        Label headerImage = new Label("", glyphFont.create(FontAwesome.Glyph.INFO_CIRCLE).color(Color.BLUE).sizeFactor(3));
        HBox box = new HBox(10, headerImage);
        box.setAlignment(Pos.CENTER_RIGHT);
        AnchorPane.setTopAnchor(box, 5.0);
        AnchorPane.setRightAnchor(box, 5.0);
        return box;
    }
}
