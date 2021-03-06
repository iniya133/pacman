package pacman;

import pacman.entities.BonusPickable;
import pacman.entities.PacMan;
import pacman.entities.Pickable;
import pacman.entities.Ghost;

import pacman.slots.Corridor;
import pacman.slots.GhostDoor;
import pacman.slots.Wall;
import pacman.slots.Void;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Observable {
    private Slot[][] matrix;
    private ArrayList<Entity> entities;
    private int score = 0;
    private int level = 1;
    private int pickableLeft = 0;
    private int width;
    private int height;
    private boolean won = false;
    private boolean lost = false;
    private ReentrantLock lock;
    private PacMan pacman;
    private final Position pacmanRespawnPos = new Position(14, 17);
    private final Position ghostRespawnPos = new Position(14, 15);
    private ArrayList<Thread> threads;

    Game() {
        lock = new ReentrantLock();
        threads = new ArrayList<>();
    }

    /**
     * Parse a character into a slot.
     * Returns the slot or null if no match found.
     *
     * @param character {char}
     * @return {Slot | null}
     */
    private Slot characterToSlot(char character) {
        Slot slot = null;

        switch (character) {
            case '0':
                slot = new Corridor();
                break;
            case '1':
                slot = new Wall();
                break;
            case '2':
                slot = new GhostDoor();
                break;
            case '3':
                slot = new Void();
                break;
        }

        return slot;
    }

    /**
     * Parse a character into an entity.
     * Returns the entity or null if no match found.
     *
     * @param character {char}
     * @return {Entity | null}
     */
    private Entity characterToEntity(char character) {
        Entity entity = null;

        switch (character) {
            case '4':
                entity = new Pickable();
                break;
            case '6':
                entity = new BonusPickable();
                break;
            case '9':
                entity = new Ghost(this);
                break;
            case '8':
                entity = new PacMan(this);
                break;
        }
        return entity;
    }

    int getScore() {
        return score;
    }

    int getLevel() {
        return level;
    }

    public PacMan getPacman() {
        return pacman;
    }

    /**
     * Loads a level in the game instance.
     */
    void load() {
        lock.lock();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("assets/levels/level-1.txt"))) {
            String line = bufferedReader.readLine();

            width = Integer.parseInt(line);
            line = bufferedReader.readLine();
            height = Integer.parseInt(line);
            line = bufferedReader.readLine();

            matrix = new Slot[width][height];
            entities = new ArrayList<>();

            int y = 0;
            while (line != null && y < height) {

                char[] characters = line.toCharArray();
                int x = 0;

                for (char character : characters) {
                    Slot slot = characterToSlot(character);
                    if (slot != null) {
                        matrix[x][y] = slot;
                        x += 1;
                    }
                }

                line = bufferedReader.readLine();
                y += 1;
            }

            line = bufferedReader.readLine();

            y = 0;
            while (line != null && y < height) {
                char[] characters = line.toCharArray();
                int x = 0;

                for (char character : characters) {
                    Entity entity = characterToEntity(character);
                    if (entity != null) {
                        entity.setPosition(x, y);
                        entities.add(entity);
                        if (entity instanceof Pickable || entity instanceof BonusPickable) {
                            pickableLeft++;
                        }
                    }
                    x += 1;
                }

                line = bufferedReader.readLine();
                y += 1;
            }
        } catch (Exception ex) {
            System.err.println("Could not correctly process level file.");
            System.err.println(ex.toString());
        }
        System.out.println("Level has been loaded");
        lock.unlock();
    }

    void start() {
        for (Entity entity : entities) {
            if (entity instanceof Ghost) {
                Ghost ghost = (Ghost) entity;
                Thread thread = new Thread(ghost);
                threads.add(thread);
                thread.setDaemon(true);
                thread.start();
            }
            if (entity instanceof PacMan) {
                pacman = (PacMan) entity;
                Thread thread = new Thread(pacman);
                threads.add(thread);
                thread.setDaemon(true);
                thread.start();
            }
        }

        setChanged();
        notifyObservers();
    }

    void restart() {
        won = false;
        lost = false;
        load();
        start();
    }

    public Slot[][] getMatrix() {
        lock.lock();

        Slot[][] matrixClone = matrix.clone();

        lock.unlock();

        return matrixClone;
    }

    public int getHeight() {
        return height;
    }


    public int getWidth() {
        return width;
    }

    ArrayList<Entity> getEntities() {
        lock.lock();

        ArrayList<Entity> entitiesClone = (ArrayList<Entity>) entities.clone();

        lock.unlock();

        return entitiesClone;
    }

    private Position getPositionWithDirection(Entity entity, Direction direction) {
        Position position = entity.getPosition();
        int new_x = position.x;
        int new_y = position.y;

        switch (direction) {
            case UP:
                if (position.y - 1 >= 0) {
                    new_y--;
                }
                break;
            case DOWN:
                if (position.y + 1 <= height - 1) {
                    new_y++;
                }
                break;
            case LEFT:
                if (position.x - 1 >= 0) {
                    new_x--;
                } else {
                    new_x = width - 1;
                }
                break;
            case RIGHT:
                if (position.x + 1 <= width - 1) {
                    new_x++;
                } else {
                    new_x = 0;
                }
                break;
        }

        return new Position(new_x, new_y);
    }

    private boolean canMove(Entity entity, Position position) {
        lock.lock();

        int x = position.x;
        int y = position.y;

        boolean canMove;
        try {
            canMove = matrix[x][y] instanceof Corridor;
            if (entity instanceof Ghost) {
                canMove = canMove || matrix[x][y] instanceof GhostDoor;
            }
        } catch (Exception ex) {
            canMove = false;
        }

        lock.unlock();

        return canMove;
    }

    /**
     * Move an entity into a certain direction.
     * Will return true if the move was made or false if it was rejected.
     * Use `canMoveTo` to test if move is allowed.
     *
     * @param entity    {Entity}
     * @param direction {Direction}
     */
    public void move(Entity entity, Direction direction) {
        if (!(entity instanceof Ghost) && !(entity instanceof PacMan)) {
            return;
        }

        lock.lock();

        boolean hasMoved = false;
        Position newPosition = getPositionWithDirection(entity, direction);
        if (canMove(entity, newPosition)) {
            entity.setPosition(newPosition);
            hasMoved = true;
        }

        if (hasMoved) {
            testDeath();
            testEnded();
            setChanged();
            notifyObservers();
        }

        lock.unlock();
    }

    /**
     * Move the entity into a certain direction.
     *
     * @param direction {Direction}
     */
    public void playerMove(Direction direction) {
        lock.lock();

        boolean hasMoved = false;
        Position newPosition = getPositionWithDirection(pacman, direction);
        if (canMove(pacman, newPosition)) {
            pacman.setPosition(newPosition);
            hasMoved = true;
        }

        if (hasMoved) {
            pacman.setDirection(direction);

            for (Entity entity : entities) {
                if (entity instanceof Pickable) {
                    pacman.entities.Pickable pick = (Pickable) entity;
                    if (pick.getPosition().equals(pacman.getPosition())) {
                        score += 10;
                        entities.remove(entity);
                        pickableLeft--;
                        break;
                    }
                } else if (entity instanceof BonusPickable) {
                    pacman.entities.BonusPickable pick = (BonusPickable) entity;
                    if (pick.getPosition().equals(pacman.getPosition())) {
                        score += 50;
                        entities.remove(entity);
                        pickableLeft--;
                        pacman.setSuperPacman();
                        break;
                    }
                }
            }
            testDeath();
            testEnded();

            setChanged();
            notifyObservers();
        }

        lock.unlock();
    }

    /**
     * Test if pacman collided with a ghost.
     */
    private void testDeath() {
        lock.lock();
        for (Entity entity : entities) {
            if (pacman != null && entity instanceof Ghost && entity.getPosition().equals(pacman.getPosition())) {
                if (pacman.isSuperPacman()) {
                    Ghost ghost = (Ghost) entity;
                    ghost.setHasExited(false);
                    ghost.setPosition(ghostRespawnPos);
                    score += 100;
                } else {
                    pacman.die();
                    if (pacman.getLifes() > 0) {
                        pacman.setPosition(pacmanRespawnPos);
                    }
                }
                break;
            }
        }
        lock.unlock();
    }

    private void testEnded() {
        if (pacman == null || pacman.getLifes() == 0) {
            lost = true;
            cleanThreads();
        } else if (pickableLeft == 0) {
            won = true;
            level += 1;
            cleanThreads();
            restart();
        }
    }

    private void cleanThreads() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        threads.clear();
    }

    boolean hasWon() {
        return won;
    }

    boolean hasLost() {
        return lost;
    }
}
