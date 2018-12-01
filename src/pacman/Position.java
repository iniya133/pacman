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

    public Position clone() {
        return new Position(this.x, this.y);
    }
}
