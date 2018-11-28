package pacman;

public abstract class Entity implements Runnable {
    private Position position;

    public void run() {

    }
    public void setPosition(int x, int y){
        this.position = new Position(x, y);
    }
}
