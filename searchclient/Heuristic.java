package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.awt.Point;
import static java.lang.Math.abs;

public abstract class Heuristic implements Comparator<Node> {
	private ArrayList<Point> Goals = new ArrayList<>();
    private static final short WALL = -1;
    private short GoalsBoard[][][];
    public Heuristic(Node initialState) {
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            for (int column = 0; column < Node.MAX_COL; ++column) {
                 if(Node.goals[row][column] > 0) {
                    Goals.add(new Point(column, row));
                }
            }
        }
        GoalsBoard = new short[Goals.size()][Node.MAX_ROW][Node.MAX_COL];
        for(int currentGoal = 0; currentGoal < Goals.size(); ++currentGoal) {
            populateDistanceBoard(GoalsBoard[currentGoal], Goals.get(currentGoal));

        }
    }
    private void populateDistanceBoard(short board [][], Point location) {
        //setting appropriate values to recognize visited cells
        for(short row[] : board) {
            Arrays.fill(row, WALL);
        }
        short distanceFromGoal = 0;
        board[location.y][location.x] = distanceFromGoal;
        ArrayList<Point> possibleNeighbours = FindNeighbours(location, board, ++distanceFromGoal);
        while (!possibleNeighbours.isEmpty()) {
            ++distanceFromGoal;
            ArrayList<Point> newNeighbours = new ArrayList<>();
            for(int Neighbour =0; Neighbour < possibleNeighbours.size(); ++Neighbour) {
                newNeighbours.addAll(FindNeighbours(possibleNeighbours.get(Neighbour), board, distanceFromGoal));
            }
            possibleNeighbours = newNeighbours;
        }
    }

    private ArrayList<Point> FindNeighbours(Point Home, short board[][], short distance) {
        ArrayList<Point> possibleNeighbour = new ArrayList<>();
        findNeighbour(Home.x, Home.y + 1, board, possibleNeighbour, distance);
        findNeighbour(Home.x, Home.y - 1, board, possibleNeighbour, distance);
        findNeighbour(Home.x - 1, Home.y, board, possibleNeighbour, distance);
        findNeighbour(Home.x + 1, Home.y, board, possibleNeighbour, distance);
        return  possibleNeighbour;
    }

    private void findNeighbour(int x, int y, short board[][], ArrayList<Point> possibleNeighbour, short distance) {
        if(x >= Node.MAX_COL || y >= Node.MAX_ROW) return;
        if( !Node.walls[y][x] && (board[y][x] == WALL)) {
            board[y][x] = distance;
            possibleNeighbour.add(new Point( x, y ));
        }
    }

    //strictly for testing purposes
    void printBoard(short board[][]) {
        System.err.println();
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            StringBuilder line = new StringBuilder();
            for (int column = 0; column < Node.MAX_COL; ++column) {
                line.append( board[row][column] );
            }
            System.err.println(line.toString());
        }
        System.err.println();
    }

	public int h(Node n) {
        int result = 0;
        int closestBoxToGoal = Integer.MAX_VALUE;
        int closestBoxToAgent = 0;
        for (int row = 0; row < n.boxes.length; row++) {
            for (int col = 0; col < n.boxes[row].length; col++) {
                char currentBoxGoal = Character.toLowerCase(n.boxes[row][col]);
                if ((n.boxes[row][col] > 0) && ((Node.goals[row][col]) != currentBoxGoal)) { // is box already at goal?
                    for (int currentGoal = 0; currentGoal<GoalsBoard.length; ++currentGoal) {
                        if (Node.goals[Goals.get(currentGoal).y][Goals.get(currentGoal).x] == currentBoxGoal && // is the goal value equal to the box value?
                                n.boxes[Goals.get(currentGoal).y][Goals.get(currentGoal).x] != n.boxes[row][col]) { // is the goal already taken by other box?
                            result += GoalsBoard[currentGoal][row][col]; //add the distance to heuristic
                            int boxToAgent = ManhattanDistane(n.agentCol, n.agentRow, col, row );
                            int BoxToGoal =   GoalsBoard[currentGoal][row][col];
                            if (closestBoxToGoal > BoxToGoal) { //find the closest box to goal
                                closestBoxToGoal = BoxToGoal;
                                closestBoxToAgent = boxToAgent;
                            }
                        }
                    }
                }
            }
        }
        result *= GoalsBoard.length; // multiplication to enhance decision-making in multi-goal levels
        result += closestBoxToAgent; // go to the closest box!
        return result;
	}

    int ManhattanDistane(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
	public abstract int f(Node n);

	@Override
	public int compare(Node n1, Node n2) {
		return this.f(n1) - this.f(n2);
	}

	public static class AStar extends Heuristic {
		public AStar(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return n.g() + this.h(n);
		}

		@Override
		public String toString() {
			return "A* evaluation";
		}
	}

	public static class WeightedAStar extends Heuristic {
		private int W;

		public WeightedAStar(Node initialState, int W) {
			super(initialState);
			this.W = W;
		}

		@Override
		public int f(Node n) {
			return n.g() + this.W * this.h(n);
		}

		@Override
		public String toString() {
			return String.format("WA*(%d) evaluation", this.W);
		}
	}

	public static class Greedy extends Heuristic {
		public Greedy(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return this.h(n);
		}

		@Override
		public String toString() {
			return "Greedy evaluation";
		}
	}
}
