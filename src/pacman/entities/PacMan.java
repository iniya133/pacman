package pacman.entities;

import javafx.application.Platform;
import pacman.Direction;
import pacman.Entity;
import pacman.Game;

import java.util.Timer;
import java.util.TimerTask;

public class PacMan extends Entity implements Runnable {
    private boolean superPacman = false;
    private Direction direction;
    private Direction next_direction;
    private int lifes = 3;
    private final int SUPER_PACMAN_TIMEOUT = 10; // seconds
    private final int STOPPED_TIMEOUT = 1; // seconds
    private Timer timer = new Timer();
    private Game game;
    private boolean stopped;

    public PacMan(Game _game) {
        game = _game;
    }

    public boolean getSuperPacman() {
        return superPacman;
    }

    public int getLifes() {
        return lifes;
    }

    public void setSuperPacman() {
        superPacman = true;
        stopped = true;
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

    public void setNextDirection(Direction _next_direction) {
        next_direction = _next_direction;
    }

    public void die() {
        lifes--;
    }

    @Override
    public void run() {
        while (true) {
            Platform.runLater(() -> {
                if (next_direction != null) {
                    game.playerMove(next_direction);
                    next_direction = null;
                }
            });

            try {
                if (stopped) {
                    Thread.sleep(STOPPED_TIMEOUT * 1000);
                    stopped = false;
                }
                else {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
