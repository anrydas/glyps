package das.tools.gui.fontawesome;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gui implements GuiInterface {
    private static Gui instance;
    private VBox glyphsContainer;
    private HBox topBox;
    private Slider slider;
    private Spinner<Integer> spinner;
    private final ColorPicker cpFontColor;
    private final ColorPicker cpBackColor;
    private Color fontColor = Color.BLACK;
    private Color backColor = BUTTON_DEFAULT_COLOR;
    private Button btDemo;
    private final CheckBox chbShowDialog;
    private final CheckBox chbConstGlyphsAmount;
    private final GlyphFont glyphFont;

    private Gui() {
        glyphFont = GlyphFontRegistry.font("FontAwesome");
        chbShowDialog = new CheckBox(SHOW_DIALOG_LABEL_TEXT);
        chbShowDialog.setTooltip(new Tooltip(SHOW_DIALOG_TOOLTIP));
        chbConstGlyphsAmount = new CheckBox(CONST_GLYPH_LABEL_TEXT);
        chbConstGlyphsAmount.setTooltip(new Tooltip(CONST_GLYPH_TOOLTIP));
        chbConstGlyphsAmount.setOnAction(e -> updateItems());
        cpFontColor = new ColorPicker();
        cpBackColor = new ColorPicker();
    }

    public static synchronized GuiInterface getInstance() {
        if (instance == null) {
            instance = new Gui();
            synchronized (GuiInterface.class) {
                if (instance == null) {
                    instance = new Gui();
                }
            }
        }
        return instance;
    }

    @Override
    public AnchorPane getGui() {
        topBox = getTopBox();
        VBox box = new VBox(5, topBox, getGlyphsContainer());
        AnchorPane.setTopAnchor(box, 5.0);
        AnchorPane.setBottomAnchor(box, 5.0);
        AnchorPane.setLeftAnchor(box, 5.0);
        AnchorPane.setRightAnchor(box, 5.0);
        return new AnchorPane(box);
    }

    private HBox getTopBox() {
        HBox box = new HBox(
                10, getSizeBox(), getColorsBox(), getOptionsBox()
        );
        box.setMinHeight(TOP_BOX_MIN_HEIGHT);
        box.setPadding(SLIDER_BOX_PADDING);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox getOptionsBox() {
        VBox box = new VBox(5, chbShowDialog, chbConstGlyphsAmount);
        box.setAlignment(Pos.BOTTOM_LEFT);
        return box;
    }

    private Node getGlyphsContainer() {
        glyphsContainer = new VBox(5);
        AnchorPane.setTopAnchor(glyphsContainer, 5.0);
        AnchorPane.setBottomAnchor(glyphsContainer, 5.0);
        AnchorPane.setLeftAnchor(glyphsContainer, 5.0);
        AnchorPane.setRightAnchor(glyphsContainer, 5.0);
        glyphsContainer.getChildren().add(topBox);
        ScrollPane sp = new ScrollPane(glyphsContainer);
        sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        updateItems();
        return sp;
    }

    private void updateItems() {
        double controlWidth = getControlWidth();
        int glyphsPerLine = chbConstGlyphsAmount.isSelected() ? GLYPHS_PER_LINE : GLYPHS_IN_LINE_COUNTS[(int) slider.getValue() - 1];
        glyphsContainer.getChildren().clear();
        FontAwesome.Glyph[] glyphs = FontAwesome.Glyph.values();
        ExecutorService executor = Executors.newFixedThreadPool(EXECUTOR_THREADS_AMOUNT);
        for (int i = 0; i < glyphs.length; i = i + glyphsPerLine) {
            Labeled[] lineControls = new Labeled[Math.min(glyphs.length - i, glyphsPerLine)];
            HBox lineBox = new HBox(5);
            lineBox.setPadding(LINE_BOX_PADDING);
            glyphsContainer.getChildren().add(lineBox);
            int n = i;
            Runnable task = new ApplicationThread(() -> {
                for (int j = n; j < n + glyphsPerLine && j < glyphs.length; j++) {
                    FontAwesome.Glyph glyph = glyphs[j];
                    lineControls[j < glyphsPerLine ? j : j - n] = getGlyphControl(glyph, controlWidth);
                }
                lineBox.getChildren().addAll(lineControls);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            executor.execute(task);
        }
        executor.shutdown();
    }

    /**
     * Purpose: on Windows system there are lags when showed about 600 buttons.
     * So the Labels will be shown on Windows OS.
     */
    private Labeled getGlyphControl(FontAwesome.Glyph glyph, double controlWidth) {
        Labeled ctrl;
        Glyph controlGlyph = glyphFont.create(glyph).sizeFactor((int) slider.getValue()).color(fontColor);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            ctrl = new Label("", controlGlyph);
        } else {
            ctrl = new Button("", controlGlyph);
        }
        ctrl.setPrefWidth(controlWidth);
        ctrl.setPadding(GLYPH_CONTROL_PADDING);
        if (!backColor.equals(BUTTON_DEFAULT_COLOR)) {
            setControlBackColor(ctrl);
        }
        ctrl.setTooltip(new Tooltip(
                String.format(GLYPH_BUTTON_TOOLTIP_MESSAGE, glyph.name(), glyph.ordinal(), getHexValue(glyph))));
        ctrl.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                updateDemoButton(glyph);
                if (chbShowDialog.isSelected()) showInfoDialog(glyph);
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                copyGlyphNameToClipboard(glyph);
            }
        });
        return ctrl;
    }

    private double getControlWidth() {
        int multiplier = MIN_WIDTH_MULTIPLIERS[(int) slider.getValue() - 1];
        return multiplier * slider.getValue();
    }

    private void updateDemoButton(FontAwesome.Glyph glyph) {
        if (btDemo != null) topBox.getChildren().remove(btDemo);
        if (glyph != null) {
            btDemo = new Button("", glyphFont.create(glyph).sizeFactor(DEMO_BUTTON_GLYPH_SIZE).color(fontColor));
            btDemo.setTooltip(new Tooltip(String.format(DEMO_BUTTON_TEXT, glyph.name(), glyph.ordinal(), getHexValue(glyph))));
        } else {
            btDemo = new Button("?");
        }
        if (!backColor.equals(BUTTON_DEFAULT_COLOR)) setControlBackColor(btDemo);
        btDemo.setUserData(glyph);
        btDemo.setOnMouseClicked(e -> {
            if (btDemo != null) {
                if (e.getButton().equals(MouseButton.SECONDARY) && glyph != null) {
                    copyGlyphNameToClipboard(glyph);
                }
                if (e.getButton().equals(MouseButton.PRIMARY) && e.isControlDown()) {
                    topBox.getChildren().remove(btDemo);
                    btDemo = null;
                }
            }
        });
        topBox.getChildren().add(0, btDemo);
    }

    private void showInfoDialog(FontAwesome.Glyph glyph) {
        GlyphInfoDialog dlg = new GlyphInfoDialog(glyphFont, glyph);
        dlg.initOwner(spinner.getScene().getWindow());
        dlg.initModality(Modality.NONE);
        dlg.setTitle(DIALOG_TITLE);
        dlg.setContentText(String.format(DIALOG_CONTENT_MESSAGE, glyph.ordinal(), getHexValue(glyph)));
        dlg.show();
    }

    private int getHexValue(FontAwesome.Glyph glyph) {
        return glyph.ordinal() & HEX_MASK;
    }

    private void copyGlyphNameToClipboard(FontAwesome.Glyph glyph) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(glyph.name()), null);
    }

    private void setControlBackColor(Labeled ctrl) {
        ctrl.setStyle(String.format(BACKGROUND_COLOR_STYLE, backColor.toString().substring(2)));
    }

    private VBox getSizeBox() {
        VBox sizeBox = new VBox(
                new Label(BUTTON_SIZE_LABEL_TEXT),
                new HBox(5, getSlider(), getSpinner()));
        sizeBox.setAlignment(Pos.CENTER);
        return sizeBox;
    }

    private Slider getSlider() {
        slider = new Slider(GLYPH_SIZE_MIN_VALUE, GLYPH_SIZE_MAX_VALUE, GLYPH_SIZE_INITIAL_VALUE);
        slider.setMinWidth(150);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner.getValueFactory().setValue(newValue.intValue());
            updateItems();
        });
        return slider;
    }

    private Spinner<Integer> getSpinner() {
        spinner = new Spinner<>(GLYPH_SIZE_MIN_VALUE, GLYPH_SIZE_MAX_VALUE, GLYPH_SIZE_INITIAL_VALUE, INCREMENT_STEP);
        spinner.setMaxWidth(70);
        spinner.valueProperty().addListener(v -> {
            slider.setValue(spinner.getValue());
            updateItems();
        });
        return spinner;
    }

    private HBox getColorsBox() {
        Button btSwap = getButton(
                glyphFont.create(FontAwesome.Glyph.EXCHANGE), SWAP_COLORS_BUTTON_TEXT,
                e ->  updateControlsEvent(backColor, fontColor));
        Button btApplyColor = getButton(
                glyphFont.create(FontAwesome.Glyph.CHECK),
                APPLY_COLORS_BUTTON_TEXT,
                e -> updateControlsEvent(cpFontColor.getValue(), cpBackColor.getValue()));
        Button btResetColor = getButton(glyphFont.create(FontAwesome.Glyph.ROTATE_LEFT),
                RESET_COLOR_BUTTON_TEXT,
                e -> updateControlsEvent(Color.BLACK, BUTTON_DEFAULT_COLOR));
        Button btUaFlagColors = getButton(
                FlagImage.getFlagImage(COLOR_UA_BLUE, COLOR_UA_YELLOW),
                UA_FLAG_BUTTON_TEXT,
                e -> updateControlsEvent(COLOR_UA_YELLOW, COLOR_UA_BLUE));
        Button btOunFlagColors = getButton(FlagImage.getFlagImage(COLOR_UA_RED, COLOR_UA_BLACK),
                OUN_FLAG_BUTTON_TEXT,
                e -> updateControlsEvent(COLOR_UA_RED, COLOR_UA_BLACK));

        HBox colorBox = new HBox(5,
                getColorPickerBox(FONT_COLOR_LABEL_TEXT, cpFontColor, fontColor, e -> fontColor = cpFontColor.getValue()),
                btSwap,
                getColorPickerBox(FOREGROUND_COLOR_LABEL_TEXT, cpBackColor, backColor,  e -> backColor = cpBackColor.getValue()),
                getButtonsPane(
                        btUaFlagColors, btOunFlagColors,
                        btApplyColor, btResetColor
                )
        );
        colorBox.setAlignment(Pos.BOTTOM_CENTER);
        return colorBox;
    }

    private void updateControlsEvent(Color fColor, Color bColor) {
        fontColor = fColor;
        backColor = bColor;
        cpFontColor.setValue(fontColor);
        cpBackColor.setValue(backColor);
        if (btDemo != null) updateDemoButton((FontAwesome.Glyph) btDemo.getUserData());
        updateItems();
    }

    private Button getButton(Node glyph, String tooltipText, EventHandler<ActionEvent> handler) {
        Button bt = new Button("", glyph);
        bt.setPrefWidth(32);
        bt.setTooltip(new Tooltip(tooltipText));
        bt.setOnAction(handler);
        return bt;
    }

    private GridPane getButtonsPane(Button... buttons) {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(buttons[0], 0, 0);
        grid.add(buttons[1], 1, 0);
        grid.add(buttons[2], 0, 1);
        grid.add(buttons[3], 1, 1);
        return grid;
    }

    private VBox getColorPickerBox(String text, ColorPicker cp, Color color, EventHandler<ActionEvent> actionEventEventHandler) {
        cp.setValue(color);
        cp.setOnAction(actionEventEventHandler);
        VBox vBox = new VBox(2, new Label(text), cp);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }
}
