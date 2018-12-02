package pacman.entities;

import pacman.Direction;
import pacman.Entity;
import java.util.Timer;
import java.util.TimerTask;

public class PacMan extends Entity {
    private boolean superPacman = false;
    private Direction direction;
    private int lifes = 3;
    private int secondsSuperPacman = 10;
    Timer timer = new Timer();

    public void setLifes(int _lifes) {
        lifes = _lifes;
    }

    public void upLifes() {
        lifes++;
    }

    public void loseLife() {
        lifes--;
    }

    public int getLifes() {
        return lifes;
    }

    public void setSuperPacman() {
        superPacman = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                superPacman = false;
            }
        }, secondsSuperPacman*1000);
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

    public void die() {
        System.out.println("You Died");
        loseLife();
        System.out.println(lifes);
    }
}
