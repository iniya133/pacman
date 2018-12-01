package pacman.entities;
import pacman.Direction;
import pacman.Entity;

public class PacMan extends Entity {
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction _direction) {
        direction = _direction;
    }
}
