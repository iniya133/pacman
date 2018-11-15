package pacman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        int windowSize = 600;
        Group root = new Group();

        Game game = new Game();
        game.load();

        Canvas canvas = new Canvas(windowSize, windowSize);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        int matrixSize = game.getSize();

        int slotSize = windowSize / matrixSize;


        Slot[][] matrix = game.getMatrix();

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                Slot slot = matrix[x][y];
                System.out.println(slot);
                if (slot instanceof Wall) {
                    graphicsContext.setFill(Color.BLUE);
                    System.out.println("Blue");
                    graphicsContext.fillRect(x * slotSize, y * slotSize, slotSize, slotSize);
                } else if (slot instanceof Corridor) {
                    System.out.println("Black");
                    graphicsContext.setFill(Color.BLACK);
                    graphicsContext.fillRect(x * slotSize, y * slotSize, slotSize, slotSize);
                }
            }
        }

        root.getChildren().add(canvas);

        primaryStage.setTitle("Pacman");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
