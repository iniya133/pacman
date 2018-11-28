package pacman;

public abstract class Entity implements Runnable {
    private Position position;

    public void run() {

    }

    Position getPosition() {
        return position;
    }
    void setPosition(int x, int y){
        this.position = new Position(x, y);
    }
}
