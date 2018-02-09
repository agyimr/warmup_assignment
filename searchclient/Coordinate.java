package searchclient;

import java.util.ArrayList;

public class Coordinate {
    public static char EMPTY_PLACEHOLDER_VALUE = '-';
	private int x;
	private int y;
	private char value;

	Coordinate(int x, int y, char value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    Coordinate(int x, int y) {
	    this.x = x;
	    this.y = y;
	    this.value = EMPTY_PLACEHOLDER_VALUE;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate[] getNeighbours() {
        Coordinate[] neighbours = new Coordinate[4];
        neighbours[0] = new Coordinate(x + 1, y);
        neighbours[1] = new Coordinate(x - 1, y);
        neighbours[2] = new Coordinate(x, y + 1);
        neighbours[3] = new Coordinate(x, y - 1);
        return neighbours;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (this.getY() != other.getY() || this.getX() != other.getX())
            return false;
        return true;
    }
}
