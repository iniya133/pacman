package pacman;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
import java.io.IOException;
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
    static private AudioInputStream beginningSound;
    static private Stage stage;
    static private Scene gameOverScene;
    final private int textSlotX = 1;
    final private int textSlotY = 11;
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
        if (game.hasLost()) {
            gameOverText.setFill(Color.RED);
            gameOverText.setText("You lost ! \n Score " + game.getScore() + "\n Press space to restart");
            stage.setScene(gameOverScene);
        }

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

        if (pacMan != null && pacMan.getSuperPacman()) {
            ghostImage = frightenedGhostImage;
        } else {
            ghostImage = blueGhostImage;
        }

        scoreText.setText("Score : " + game.getScore() + "\n Level : " + game.getLevel());
        scoreText.setX(slotSize * textSlotX + paddingTop);
        scoreText.setY(slotSize * textSlotY + paddingTop);

        if (pacMan != null) {
            for (int i = 0; i < pacMan.getLifes(); i++) {
                drawImage(pacmanImage, slotSize, paddingLeft, paddingTop, lifeSlotX + i, lifeSlotY, true);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        stage = primaryStage;
        primaryStage.setTitle("PacMan");
        primaryStage.setResizable(false);

        // Game scene
        Group gameRoot = new Group();
        TextFlow textFlow = new TextFlow();
        scoreText = new Text();
        scoreText.setFont(Font.font("Helvetica", 20));
        scoreText.setFill(Color.WHITE);

        textFlow.getChildren().add(scoreText);

        Canvas canvas = new Canvas(windowWidth, windowHeight);
        graphicsContext = canvas.getGraphicsContext2D();

        gameRoot.getChildren().addAll(canvas, textFlow, scoreText);

        Scene gameScene = new Scene(gameRoot, windowWidth, windowHeight, Color.BLACK);

        gameScene.setOnKeyPressed(e -> {
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

        // Menu scene
        StackPane menuRoot = new StackPane();
        Text menuText = new Text();
        menuText.setFill(Color.YELLOW);
        menuText.setFont(Font.font("Helvetica", 40));
        menuText.setText("PACMAN\n\nPress space to start...");
        menuRoot.getChildren().add(menuText);
        menuText.setTextAlignment(TextAlignment.CENTER);
        StackPane.setAlignment(menuText, Pos.CENTER);
        Scene menuScene = new Scene(menuRoot, windowWidth, windowHeight, Color.BLACK);

        // GameOver scene
        StackPane gameOverRoot = new StackPane();
        gameOverText = new Text();
        gameOverText.setFill(Color.GREEN);
        gameOverText.setFont(Font.font("Helvetica", 40));
        gameOverText.setText("You won");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        gameOverRoot.getChildren().add(gameOverText);
        StackPane.setAlignment(gameOverText, Pos.CENTER);
        gameOverScene = new Scene(gameOverRoot, windowWidth, windowHeight, Color.BLACK);

        gameOverScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                game.restart();
                primaryStage.setScene(gameScene);
            }
        });

        menuScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                game.start();
                primaryStage.setScene(gameScene);
            }
        });

        primaryStage.setScene(menuScene);
        primaryStage.show();

        pacmanImage = new Image("file:assets/sprites/pacman.png");
        pacmanLeftImage = new Image("file:assets/sprites/pacman-left.png");
        pickableImage = new Image("file:assets/sprites/pickable.png");
        bonusPickableImage = new Image("file:assets/sprites/mega-pickable.png");
        blueGhostImage = new Image("file:assets/sprites/blue-ghost.png");
        frightenedGhostImage = new Image("file:assets/sprites/frightened-ghost.png");

        Clip clip = AudioSystem.getClip();
        beginningSound = AudioSystem.getAudioInputStream(new File("./assets/sounds/pacman_beginning.wav"));
        clip.open(beginningSound);
        clip.start();

        game.load();
    }

    void bootstrap(Game g) {
        game = g;

        String[] args = new String[0];
        launch(args);
    }
}
