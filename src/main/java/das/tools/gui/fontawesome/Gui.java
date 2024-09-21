package das.tools.gui.fontawesome;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gui implements GuiInterface {
    private static Gui instance;
    private VBox glyphsContainer;
    private HBox topBox;
    private Slider slider;
    private final ColorPicker cpFontColor;
    private final ColorPicker cpBackColor;
    private Color fontColor = Color.BLACK;
    private Color backColor = BUTTON_DEFAULT_COLOR;
    private Button btDemo;
    private final CheckBox chbShowDialog;
    private final CheckBox chbConstGlyphsAmount;
    private final GlyphFont glyphFont;
    private List<Labeled[]> controlsLines;
    private TextField edSearchGliph;

    private Gui() {
        glyphFont = GlyphFontRegistry.font("FontAwesome");
        chbShowDialog = new CheckBox(SHOW_DIALOG_LABEL_TEXT);
        chbShowDialog.setTooltip(new Tooltip(SHOW_DIALOG_TOOLTIP));
        chbConstGlyphsAmount = new CheckBox(CONST_GLYPH_LABEL_TEXT);
        chbConstGlyphsAmount.setSelected(true);
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
        box.setPadding(TOP_BOX_PADDING);
        box.setAlignment(Pos.TOP_LEFT);
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
        controlsLines = new ArrayList<>();
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
                controlsLines.add(lineControls);
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
        ctrl.setUserData(glyph.name());
        ctrl.setPrefWidth(controlWidth);
        ctrl.setPadding(GLYPH_CONTROL_PADDING);
        setControlBackColor(ctrl);
        ctrl.setTooltip(new Tooltip(
                String.format(GLYPH_BUTTON_TOOLTIP_MESSAGE, glyph.name(), glyph.ordinal(), getHexValue(glyph))));
        ctrl.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                updateDemoButton(glyph);
                String text = edSearchGliph.getText();
                if (!"".equals(text) && !text.equals(glyph.name())) edSearchGliph.setText("");
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
        dlg.initOwner(slider.getScene().getWindow());
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
        if (!backColor.equals(BUTTON_DEFAULT_COLOR)) {
            ctrl.setStyle(String.format(BACKGROUND_COLOR_STYLE, backColor.toString().substring(2)));
        } else {
            ctrl.setStyle("");
        }
    }

    private VBox getSizeBox() {
        VBox sizeBox = new VBox(
                new HBox(5, getGlyphNamesField(), getSlider()));
        sizeBox.setAlignment(Pos.CENTER);
        return sizeBox;
    }

    private Node getSlider() {
        slider = new Slider(GLYPH_SIZE_MIN_VALUE, GLYPH_SIZE_MAX_VALUE, GLYPH_SIZE_INITIAL_VALUE);
        slider.setMinWidth(150);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateItems();
        });
        return new VBox(5, new Label(BUTTON_SIZE_LABEL_TEXT), slider);
    }

    private Node getGlyphNamesField() {
        edSearchGliph = new TextField();
        edSearchGliph.setPrefWidth(GLYPH_NAMES_FIELD_WIDTH);
        edSearchGliph.setPromptText(GLYPH_NAMES_PROMPT_TEXT);
        edSearchGliph.setTooltip(new Tooltip(GLYPH_NAMES_TOOLTIP_TEXT));

        VBox vBox = new VBox(5, edSearchGliph);
        vBox.setAlignment(Pos.BOTTOM_LEFT);

        Map<String, FontAwesome.Glyph> glyphMap = new HashMap<>();
        edSearchGliph.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER: {
                    String text = edSearchGliph.getText();
                    if (glyphMap.containsKey(text)) {
                        highlightControl(glyphMap.get(text));
                        updateDemoButton(glyphMap.get(text));
                    }
                    break;
                }
                case ESCAPE: {
                    edSearchGliph.setText("");
                    topBox.getChildren().remove(btDemo);
                    break;
                }
            }

        });
        edSearchGliph.textProperty().addListener(e -> {
            String text = edSearchGliph.getText();
            if (glyphMap.containsKey(text)) {
                highlightControl(glyphMap.get(text));
                updateDemoButton(glyphMap.get(text));
            }
        });
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable task = new ApplicationThread(() -> {
            for (FontAwesome.Glyph v : FontAwesome.Glyph.values()) {
                glyphMap.put(v.name(), v);
            }
            Set<String> keySet = glyphMap.keySet();
            String[] strings = keySet.toArray(new String[0]);
            TextFields.bindAutoCompletion(edSearchGliph, strings);
        });
        executor.execute(task);
        executor.shutdown();
        return vBox;
    }

    private void highlightControl(FontAwesome.Glyph glyph) {
        for (Labeled[] line : controlsLines) {
            for (Labeled control : line) {
                if (control.getUserData().equals(glyph.name())) {
                    ScrollPane pane = (ScrollPane) topBox.getScene().lookup(SCROLL_PANE_ID);
                    control.setStyle(control.getStyle() + HIGHLIGHT_BUTTON_STYLE);
                    scrollToSelected(pane, control);
                } else {
                    setControlBackColor(control);
                }
            }
        }
    }

    private void scrollToSelected(ScrollPane scrollPane, Node node) {
        Bounds viewport = scrollPane.getViewportBounds();
        // Y value
        double contentHeight = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal()).getHeight();
        double nodeMinY = node.localToScene(node.getBoundsInLocal()).getMinY();
        double nodeMaxY = node.localToScene(node.getBoundsInLocal()).getMaxY();
        double vValueDelta = 0;
        double vValueCurrent = scrollPane.getVvalue();
        if (nodeMaxY < 0) {
            vValueDelta = (nodeMinY - viewport.getHeight()) / (contentHeight + topBox.getHeight());
        } else if (nodeMinY > viewport.getHeight()) {
            vValueDelta = (nodeMinY + viewport.getHeight() - (nodeMaxY-nodeMinY)*12) / (contentHeight - topBox.getHeight());
        }
        // X value
        double contentWidth = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal()).getWidth();
        double nodeMinX = node.localToScene(node.getBoundsInLocal()).getMinX();
        double nodeMaxX = node.localToScene(node.getBoundsInLocal()).getMaxX();
        double hValueDelta = 0;
        double hValueCurrent = scrollPane.getHvalue();
        if (nodeMaxX < 0) {
            hValueDelta = (nodeMinX - viewport.getWidth()) / (contentWidth);
        } else if (nodeMinX > viewport.getWidth()) {
            hValueDelta = (nodeMinX + viewport.getWidth() - (nodeMaxX-nodeMinX)*5) / (contentWidth);
        }
        scrollPane.setVvalue(vValueCurrent + vValueDelta);
        scrollPane.setHvalue(hValueCurrent + hValueDelta);
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

        VBox buttonsPane = new VBox(5,
                getButtonsPane(
                        btUaFlagColors, btOunFlagColors,
                        btApplyColor, btResetColor)
        );
        buttonsPane.setAlignment(Pos.BOTTOM_CENTER);
        HBox colorBox = new HBox(5,
                getColorPickerBox(FONT_COLOR_LABEL_TEXT, cpFontColor, fontColor, e -> fontColor = cpFontColor.getValue()),
                btSwap,
                getColorPickerBox(FOREGROUND_COLOR_LABEL_TEXT, cpBackColor, backColor,  e -> backColor = cpBackColor.getValue()),
                buttonsPane
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
        vBox.setAlignment(Pos.BOTTOM_CENTER);
        return vBox;
    }
}
