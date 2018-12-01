package pacman;

class Position {
    int x;
    int y;

    Position(int _x, int _y) {
        x = _x;
        y = _y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }

        Position position = (Position) obj;

        return position.x == this.x && position.y == this.y;
    }

    Position move(Direction direction) {
        int new_x = this.x;
        int new_y = this.y;

        switch (direction) {
            case UP:
                new_y--;
                break;
            case DOWN:
                new_y++;
                break;
            case LEFT:
                new_x--;
                break;
            case RIGHT:
                new_x++;
                break;
        }

        return new Position(new_x, new_y);
    }

    public Position clone() {
        return new Position(this.x, this.y);
    }
}
