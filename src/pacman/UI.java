package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;
import pacman.slots.Void;

import java.util.Observable;
import java.util.Observer;

public class UI extends Application implements Observer {
    static private GraphicsContext graphicsContext;
    static private Game game;
    static private int windowHeight = 600;
    static private int windowWidth = 600;

    public void update(Observable observable, Object arg) {
        System.out.println("Game matrix changed: " + arg);

        Slot[][] matrix = game.getMatrix();

        int slotWidth = windowWidth / game.getWidth();
        int slotHeight = windowHeight / game.getHeight();

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                Slot slot = matrix[x][y];
                if (!(slot instanceof Void)) {
                    if (slot instanceof Wall) {
                        graphicsContext.setFill(Color.BLUE);
                    } else if (slot instanceof Corridor) {
                        graphicsContext.setFill(Color.BLACK);
                    } else if (slot instanceof GhostDoor) {
                        graphicsContext.setFill(Color.PINK);
                    }
                    graphicsContext.fillRect(x * slotWidth, y * slotHeight, slotWidth, slotHeight);
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        primaryStage.setTitle("Pacman");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        game.load();
    }

    void bootstrap(Game g) {
        game = g;

        String[] args = new String[0];
        launch(args);
    }
}
