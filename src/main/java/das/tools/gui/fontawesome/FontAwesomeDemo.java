package das.tools.gui.fontawesome;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class FontAwesomeDemo extends Application {
    protected static final int MIN_HEIGHT = 610;
    protected static final int MIN_WIDTH = 900;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        AnchorPane root = Gui.getInstance().getGui();
        Scene scene = new Scene(root , MIN_WIDTH, MIN_HEIGHT);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(FontAwesomeDemo.class.getResourceAsStream("/images/app.png"))));
        stage.setTitle("FontAwesome by ControlsFX Demo Glyphs application");
        stage.show();
    }

}