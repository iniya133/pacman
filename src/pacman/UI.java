package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;

import pacman.entities.PacMan;
import pacman.entities.Ghost;
import pacman.entities.Pickable;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class UI extends Application implements Observer {
    static private GraphicsContext graphicsContext;
    static private Game game;
    static private int windowWidth = 900;
    static private int windowHeight = 900;
    static private Image pacmanImage;
    static private Image blueGhostImage;
    static private Image greenGhostImage;
    static private Image pickableImage;

    public void update(Observable observable, Object arg) {
        Slot[][] matrix = game.getMatrix();

        int slotSize = Math.max(windowWidth / game.getWidth(), windowHeight / game.getHeight());

        int paddingLeft = (windowWidth - slotSize * game.getWidth()) / 2;
        int paddingTop = (windowHeight - slotSize * game.getHeight()) / 2;

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                Slot slot = matrix[x][y];
                if (slot instanceof Wall) {
                    graphicsContext.setFill(Color.BLUE);
                } else if (slot instanceof Corridor) {
                    graphicsContext.setFill(Color.BLACK);
                } else if (slot instanceof GhostDoor) {
                    graphicsContext.setFill(Color.PINK);
                } else {
                    graphicsContext.setFill(Color.BLACK);
                }
                graphicsContext.fillRect(paddingLeft + x * slotSize, paddingTop + y * slotSize, slotSize, slotSize);
            }
        }

        ArrayList<Entity> entities = game.getEntities();

        for (Entity entity : entities) {
            if (entity instanceof PacMan) {
                graphicsContext.drawImage(pacmanImage, paddingLeft + entity.getPosition().x * slotSize, paddingTop + entity.getPosition().y * slotSize, slotSize, slotSize);
            } else if (entity instanceof Ghost) {
                graphicsContext.drawImage(greenGhostImage, paddingLeft + entity.getPosition().x * slotSize, paddingTop + entity.getPosition().y * slotSize, slotSize, slotSize);
            } else if (entity instanceof Pickable) {
                graphicsContext.drawImage(pickableImage, paddingLeft + entity.getPosition().x * slotSize, paddingTop + entity.getPosition().y * slotSize, slotSize, slotSize);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        primaryStage.setTitle("PacMan");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();

        pacmanImage = new Image("file:assets/pacman.png");
        pickableImage = new Image("file:assets/pickable.png");
        blueGhostImage = new Image("file:assets/blue-ghost.png");
        greenGhostImage = new Image("file:assets/green-ghost.png");

        game.load();
    }

    void bootstrap(Game g) {
        game = g;

        String[] args = new String[0];
        launch(args);
    }
}
