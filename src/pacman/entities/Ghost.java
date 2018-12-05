package pacman.entities;

import javafx.application.Platform;
import pacman.Entity;
import pacman.Game;
import pacman.Direction;
import pacman.Slot;
import pacman.slots.Corridor;
import pacman.slots.GhostDoor;

import java.util.Random;

public class Ghost extends Entity implements Runnable {
    private Game game;
    private Direction currentDirection = Direction.UP;
    private boolean hasExited = false;

    public void setHasExited(boolean exited) {
        hasExited = exited;
    }

    public Ghost(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        Random rand = new Random();

        Entity entity = this;

        while (true) {
            Platform.runLater(() -> {
                Slot[][] matrix = game.getMatrix();
                int x = getPosition().x;
                int y = getPosition().y;

                if (x == 0) {
                    x += 1;
                } else if (x == game.getWidth() - 1) {
                    x = game.getWidth() - 2;
                }

                boolean up = matrix[x][y - 1] instanceof Corridor || (!hasExited && matrix[x][y - 1] instanceof GhostDoor);
                boolean down = matrix[x][y + 1] instanceof Corridor || (!hasExited && matrix[x][y + 1] instanceof GhostDoor);
                boolean right = matrix[x + 1][y] instanceof Corridor || (!hasExited && matrix[x + 1][y] instanceof GhostDoor);
                boolean left = matrix[x - 1][y] instanceof Corridor || (!hasExited && matrix[x - 1][y] instanceof GhostDoor);

                if (matrix[x][y] instanceof GhostDoor) {
                    currentDirection = Direction.UP;
                    hasExited = true;
                } else {
                    int two_choice = rand.nextInt(2);
                    int three_choice = rand.nextInt(3);

                    switch (currentDirection) {
                        case UP:
                            if (up && right && left) {
                                currentDirection = Direction.UP;
                            } else if (right && left) {
                                currentDirection = Direction.RIGHT;
                            } else if (right && up) {
                                currentDirection = two_choice == 0 ? Direction.RIGHT : Direction.UP;
                            } else if (left && up) {
                                currentDirection = two_choice == 0 ? Direction.LEFT : Direction.UP;
                            } else if (up) {
                                currentDirection = Direction.UP;
                            } else if (left) {
                                currentDirection = Direction.LEFT;
                            } else if (right) {
                                currentDirection = Direction.RIGHT;
                            }
                            break;

                        case RIGHT:
                            if (up && right && down) {
                                currentDirection = Direction.UP;
                            } else if (up && down) {
                                currentDirection = Direction.UP;
                            } else if (right && up) {
                                currentDirection = two_choice == 0 ? Direction.RIGHT : Direction.UP;
                            } else if (down && right) {
                                currentDirection = two_choice == 0 ? Direction.RIGHT : Direction.DOWN;
                            } else if (right) {
                                currentDirection = Direction.RIGHT;
                            } else if (down) {
                                currentDirection = Direction.DOWN;
                            } else if (up) {
                                currentDirection = Direction.UP;
                            }
                            break;


                        case LEFT:
                            if (up && left && down) {
                                currentDirection = three_choice == 0 ? Direction.UP : (three_choice == 1 ? Direction.LEFT : Direction.DOWN);
                            } else if (up && down) {
                                currentDirection = two_choice == 0 ? Direction.UP : Direction.DOWN;
                            } else if (left && up) {
                                currentDirection = two_choice == 0 ? Direction.LEFT : Direction.UP;
                            } else if (down && left) {
                                currentDirection = two_choice == 0 ? Direction.LEFT : Direction.DOWN;
                            } else if (left) {
                                currentDirection = Direction.LEFT;
                            } else if (down) {
                                currentDirection = Direction.DOWN;
                            } else if (up) {
                                currentDirection = Direction.UP;
                            }
                            break;

                        case DOWN:
                            if (down && right && left) {
                                currentDirection = three_choice == 0 ? Direction.DOWN : (three_choice == 1 ? Direction.LEFT : Direction.LEFT);
                            } else if (right && left) {
                                currentDirection = two_choice == 0 ? Direction.RIGHT : Direction.DOWN;
                            } else if (right && down) {
                                currentDirection = two_choice == 0 ? Direction.RIGHT : Direction.DOWN;
                            } else if (left && down) {
                                currentDirection = two_choice == 0 ? Direction.LEFT : Direction.DOWN;
                            } else if (down) {
                                currentDirection = Direction.DOWN;
                            } else if (left) {
                                currentDirection = Direction.LEFT;
                            } else if (right) {
                                currentDirection = Direction.RIGHT;
                            }
                            break;

                    }
                }

                game.move(entity, currentDirection);
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
