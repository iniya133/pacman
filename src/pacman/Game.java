package pacman;

import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;
import pacman.slots.Void;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Observable;

public class Game extends Observable {
    private Slot[][] matrix;
    private Entity[] entities;
    private int eateanPickables = 0;
    private int pickables;
    private int width;
    private int height;

    private Slot characterToSlot(char character) throws Exception {
        Slot slot = null;

        switch (character) {
            case '0':
                slot = new Corridor();
                break;
            case '1':
                slot = new Wall();
                break;
            case '2':
                slot = new GhostDoor();
                break;
            case '3':
                slot = new Void();
                break;
        }

        return slot;
    }

    void load() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("assets/level-1.txt"))) {
            String line = bufferedReader.readLine();

            width = Integer.parseInt(line);
            line = bufferedReader.readLine();
            height = Integer.parseInt(line);
            line = bufferedReader.readLine();

            matrix = new Slot[width][height];

            int y = 0;
            while (line != null && y < height) {

                char[] characters = line.toCharArray();
                int x = 0;

                for (char character : characters) {
                    Slot slot = characterToSlot(character);
                    if (slot != null) {
                        matrix[x][y] = slot;
                        x += 1;
                    }
                }

                line = bufferedReader.readLine();
                y += 1;
            }
            setChanged();
            notifyObservers("Changed");
        } catch (Exception ex) {
            System.err.println("Could not correctly process level file.");
            System.err.println(ex.toString());
        }
    }

    Slot[][] getMatrix() {
        return matrix;
    }

    int getHeight() {
        return height;
    }


    int getWidth() {
        return width;
    }


    public boolean isEnded() {
        return false;
    }

    public void move(Entity entity, Direction direction) {

    }
}
