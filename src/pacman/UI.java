package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import pacman.entities.BonusPickable;
import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;

import pacman.entities.PacMan;
import pacman.entities.Ghost;
import pacman.entities.Pickable;

import javafx.scene.text.Text;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import java.lang.Object;

public class UI extends Application implements Observer {
    static private GraphicsContext graphicsContext;
    static private Game game;
    static private int windowWidth = 900;
    static private int windowHeight = 900;
    static private Image pacmanImage;
    static private Image pacmanLeftImage;
    static private Image blueGhostImage;
    static private Image pickableImage;
    static private Image frightenedGhostImage;
    static private Image ghostImage;
    static private Image bonusPickableImage;
    static private Text scoreText;
    static private Text gameOverText;
    static private AudioInputStream beginingSound;
    final private int textSlotX = 2;
    final private int textSlotY = 12;
    final private int lifeSlotX = 23;
    final private int lifeSlotY = 12;

//    static Media

    private void drawImage(Image image, int slotSize, int paddingLeft, int paddingTop, int x, int y, boolean enableRatio) {
        double imageRatio = 0.7;
        double topLeftPadding = enableRatio ? (slotSize * (1 - imageRatio) / 2) : 0;
        double slotSizeScaled = enableRatio ? slotSize * imageRatio : slotSize;

        graphicsContext.drawImage(
                image,
                topLeftPadding + paddingLeft + x * slotSize,
                topLeftPadding + paddingTop + y * slotSize,
                slotSizeScaled,
                slotSizeScaled
        );
    }

    public void update(Observable observable, Object arg) {
        Slot[][] matrix = game.getMatrix();

        PacMan pacMan = null;

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
                pacMan = (PacMan) entity;
                drawImage(pacMan.getDirection() == Direction.LEFT ? pacmanLeftImage : pacmanImage, slotSize, paddingLeft, paddingTop, pacMan.getPosition().x, pacMan.getPosition().y, true);
            } else if (entity instanceof Ghost) {
                drawImage(ghostImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, true);
            } else if (entity instanceof Pickable) {
                drawImage(pickableImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, false);
            } else if (entity instanceof BonusPickable) {
                drawImage(bonusPickableImage, slotSize, paddingLeft, paddingTop, entity.getPosition().x, entity.getPosition().y, false);
            }
        }

        if (game.hasLost()) {
            gameOverText.setFill(Color.RED);
            gameOverText.setText("YOU LOSE");
        } else if (game.hasWon()) {
            gameOverText.setFill(Color.GREEN);
            gameOverText.setText("YOU WIN");
        }

        if (pacMan != null && pacMan.getSuperPacman()) {
            ghostImage = frightenedGhostImage;
        } else {
            ghostImage = blueGhostImage;
        }

        gameOverText.setX(slotSize * (game.getWidth() / 2.0 - 5) + paddingLeft);
        gameOverText.setY(slotSize * game.getWidth() / 2.0 + paddingTop);

        scoreText.setText(String.valueOf(game.getScore()));
        scoreText.setX(slotSize * textSlotX + paddingTop);
        scoreText.setY(slotSize * textSlotY + paddingTop);

        if (pacMan != null) {
            for (int i = 0; i < pacMan.getLifes(); i++) {
                drawImage(pacmanImage, slotSize, paddingLeft, paddingTop, lifeSlotX + i, lifeSlotY, true);
            }
        }

    }

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        TextFlow textFlow = new TextFlow();
        scoreText = new Text();
        gameOverText = new Text();
        scoreText.setFont(Font.font("Helvetica", 40));
        gameOverText.setFont(Font.font("Helvetica", 60));
        scoreText.setFill(Color.WHITE);
        gameOverText.setFill(Color.RED);

        textFlow.getChildren().add(scoreText);
        textFlow.getChildren().add(gameOverText);

        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        root.getChildren().add(textFlow);

        primaryStage.setTitle("PacMan");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, Color.BLACK);


        root.getChildren().add(scoreText);
        root.getChildren().add(gameOverText);


        scene.setOnKeyPressed(e -> {
            PacMan pacMan = game.getPacman();
            if (pacMan != null) {
                switch (e.getCode()) {
                    case UP:
                    case Z:
                        pacMan.setNextDirection(Direction.UP);
                        break;
                    case DOWN:
                    case S:
                        pacMan.setNextDirection(Direction.DOWN);
                        break;
                    case LEFT:
                    case Q:
                        pacMan.setNextDirection(Direction.LEFT);
                        break;
                    case RIGHT:
                    case D:
                        pacMan.setNextDirection(Direction.RIGHT);
                        break;
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        pacmanImage = new Image("file:assets/sprites/pacman.png");
        pacmanLeftImage = new Image("file:assets/sprites/pacman-left.png");
        pickableImage = new Image("file:assets/sprites/pickable.png");
        bonusPickableImage = new Image("file:assets/sprites/mega-pickable.png");
        blueGhostImage = new Image("file:assets/sprites/blue-ghost.png");
        frightenedGhostImage = new Image("file:assets/sprites/frightened-ghost.png");
        try {
            Clip clip = AudioSystem.getClip();
            beginingSound = AudioSystem.getAudioInputStream(new File("./assets/sounds/pacman_beginning.wav"));
            clip.open(beginingSound);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        game.load();

    }

    void bootstrap(Game g) {
        game = g;

        String[] args = new String[0];
        launch(args);
    }
}
