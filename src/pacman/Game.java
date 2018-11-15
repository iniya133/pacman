package pacman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Observable;

public class Game extends Observable {
    private Slot[][] matrix;
    private Entity[] entities;
    private int eateanPickables = 0;
    private int pickables;
    private int size = 21;

    private Slot characterToSlot(char character) throws Exception {
        Slot slot;

        switch (character) {
            case 'X':
                slot = new Wall();
                break;
            case 'O':
                slot = new Corridor();
                break;
            default:
                throw new Exception("Could not properly parse text file");
        }

        return slot;
    }

    void load() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("assets/level-1.txt"))) {
            String line = bufferedReader.readLine();
            matrix = new Slot[size][size];

            int y = 0;
            while (line != null) {
                char[] characters = line.toCharArray();
                int x = 0;

                for (char character : characters) {
                    matrix[x][y] = characterToSlot(character);
                    x += 1;
                }

                line = bufferedReader.readLine();
                y += 1;
            }
            System.out.println(y);
        } catch (Exception ex) {
            System.err.println("Could not correctly process level file.");
        }
    }

    Slot[][] getMatrix() {
        return matrix;
    }

    int getSize() {
        return size;
    }

    public boolean isEnded() {
        return false;
    }

    public void move(Entity entity, Direction direction) {

    }
}
