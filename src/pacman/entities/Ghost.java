package pacman.entities;

import pacman.Entity;
import pacman.Game;
import pacman.Direction;

import java.util.Random;

public class Ghost extends Entity implements Runnable {
    private Game game;
    private Direction currentDirection = Direction.UP;

    public Ghost(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        Random rand = new Random();

        while (true) {
            while (!this.game.move(this, currentDirection)) {
                Direction direction;
                do {
                    direction = Direction.values()[rand.nextInt(Direction.values().length)];
                }
                while (direction == currentDirection);
                currentDirection = direction;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
