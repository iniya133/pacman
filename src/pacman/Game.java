package pacman;

import java.util.Observable;

public class Game extends Observable {
    private Slot[][] matrix;
    private Entity[] entities;
    private int eateanPickables = 0;
    private int pickables;

    void load() {

    }

    public boolean isEnded() {
        return false;
    }

    public void move(Entity entity, Direction direction) {

    }
}
