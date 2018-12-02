package pacman.entities;

import pacman.Direction;
import pacman.Entity;

import java.util.Timer;
import java.util.TimerTask;

public class PacMan extends Entity {
    private boolean superPacman = false;
    private Direction direction;
    private int lifes = 3;
    private final int SUPER_PACMAN_TIMEOUT = 10; // seconds
    private Timer timer = new Timer();

    public void setLifes(int _lifes) {
        lifes = _lifes;
    }

    public void upLifes() {
        lifes++;
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
        }, SUPER_PACMAN_TIMEOUT * 1000);
    }

    public boolean isSuperPacman() {
        return superPacman;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction _direction) {
        direction = _direction;
    }

    public void die() {
        lifes--;
    }
}
