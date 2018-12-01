package pacman;

public abstract class Entity {
    private Position position;

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }

    public void setPosition(Position position) {
        this.position = position.clone();
    }
}
