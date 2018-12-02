package pacman.entities;

import pacman.Direction;
import pacman.Entity;

public class PacMan extends Entity {
    private boolean superPacman = false;
    private Direction direction;
    private int lifes = 3;

    public void setLifes(int _lifes){
        lifes = _lifes;
    }
    public void upLifes(){
        lifes ++;
    }
    public void loseLife(){
        lifes --;
    }

    public int getLifes(){
        return lifes;
    }

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
    public void die(){
        System.out.println("You Died");
        loseLife();
        System.out.println(lifes);
    }
}
