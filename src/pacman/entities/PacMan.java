package pacman.entities;

import pacman.Direction;
import pacman.Entity;

public class PacMan extends Entity {
    private boolean superPacman = false;
    private Direction direction;

    public void setSuperPacman() {
        superPacman = !superPacman;
    }

    public boolean getSuperPacman() {
        return superPacman;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction _direction) {
        direction = _direction;
    }
}
