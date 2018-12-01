package pacman.entities;
import pacman.Entity;

public class PacMan extends Entity {
    private boolean superPacman = false;

    public void setSuperPacman(){
        superPacman = !superPacman;
    }
    public boolean getSuperPacman(){
        return superPacman;
    }
}
