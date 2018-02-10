package searchclient;

import java.util.ArrayList;
import java.util.Comparator;

import java.awt.Point;

import static java.lang.Math.abs;

public abstract class Heuristic implements Comparator<Node> {
	private ArrayList<Point> Goals = new ArrayList<>();
    private static final char WALL = 0xFF;
    private static final char TAKEN = 0x0;
    private char[][][] GoalsBoard;
    private char agentDistanceMap[][][][];
    public Heuristic(Node initialState) {
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            for (int column = 0; column < Node.MAX_COL; ++column) {
                 if(Node.goals[row][column] > 0) {
                    Goals.add(new Point(column, row));

                }
            }
        }
        GoalsBoard = new char[Goals.size()][Node.MAX_ROW][Node.MAX_COL];
        for(int currentGoal = 0; currentGoal < Goals.size(); ++currentGoal) {
            populateDistanceBoard(GoalsBoard[currentGoal], Goals.get(currentGoal));
        }
//        agentDistanceMap = new char[Node.MAX_ROW][Node.MAX_COL][Node.MAX_ROW][Node.MAX_COL];
//        for(int row = 1; row < agentDistanceMap.length - 1; ++row) {//assuming walls are surrounding the map
//            for (int column = 1; column < agentDistanceMap[row].length - 1; ++column) {
//                if(!Node.walls[row][column]) populateDistanceBoard(agentDistanceMap[row][column], new Point(column,row));
//            }
//        }
    }
    private void populateDistanceBoard(char board [][], Point location) {
        //setting appropriate values to recognize visited cells
        for(int row = 0; row < board.length; ++row) {
            for (int column = 0; column < board[row].length; ++column) {
                board[row][column] = WALL;
            }
        }
        char distanceFromGoal = 0;
        board[location.y][location.x] = distanceFromGoal;
        //Find Neighbours until you got them all
        ArrayList<Point> possibleNeighbours = FindNeighbours(location, board, ++distanceFromGoal);
        while (!possibleNeighbours.isEmpty()) {
            ++distanceFromGoal;
            ArrayList<Point> newNeighbours = new ArrayList<>();
            for(int Neighbour =0; Neighbour < possibleNeighbours.size(); ++Neighbour) {
                newNeighbours.addAll(FindNeighbours(possibleNeighbours.get(Neighbour), board, distanceFromGoal));
            }
            possibleNeighbours = null;
            System.gc();
            possibleNeighbours = newNeighbours;
        }
    }
    private ArrayList<Point> FindNeighbours(Point Home, char board[][], char distance) {
        ArrayList<Point> possibleNeighbour = new ArrayList<>();
        findNeighbour(Home.x, Home.y + 1, board, possibleNeighbour, distance);
        findNeighbour(Home.x, Home.y - 1, board, possibleNeighbour, distance);
        findNeighbour(Home.x - 1, Home.y, board, possibleNeighbour, distance);
        findNeighbour(Home.x + 1, Home.y, board, possibleNeighbour, distance);
        return  possibleNeighbour;
    }
    private void findNeighbour(int x, int y, char board[][], ArrayList<Point> possibleNeighbour, char distance) {
        if(x >= Node.MAX_COL || y >= Node.MAX_ROW) return;
        if( !Node.walls[y][x] && (board[y][x] == WALL)) {
            board[y][x] = distance;
            possibleNeighbour.add(new Point( x, y ));
        }
    }

    void printBoard(int board[][]) {
        System.err.println();
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            for (int column = 0; column < Node.MAX_COL; ++column) {
                System.err.print(board[row][column]);
            }
            System.err.println();
        }
        System.err.println();
    }
	public int h(Node n) {
        int shortestBoxGoalDistance = Integer.MAX_VALUE;
        int playerToShortestBoxDistance = 0;
        int totalHeuristic = 0;
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            for (int column = 0; column < Node.MAX_COL; ++column) {
                if((n.boxes[row][column] > 0)) {
                    if((Character.toLowerCase(n.boxes[row][column]) != Node.goals[row][column])) {
                        int currentHeuristic[] = getHeuristicDistance(row, column, n);
                        if(currentHeuristic[1] < shortestBoxGoalDistance) {
                            shortestBoxGoalDistance = currentHeuristic[1];
                            playerToShortestBoxDistance = currentHeuristic[2];
                        }
                        totalHeuristic +=  currentHeuristic[0];
                    }
                    else {
                        shortestBoxGoalDistance = 0;
                    }
                }
            }
        }
        return totalHeuristic * 10 + playerToShortestBoxDistance;
	}
	int[] getHeuristicDistance(int row, int column, Node n) {
        int heuristicDistance = 0;
        int closestBoxGoalRoute = Integer.MAX_VALUE;
        int PlayerToClosestBox = 0;
        for (int currentGoal = 0; currentGoal < Goals.size(); ++currentGoal) {
            if(Character.toLowerCase(n.boxes[row][column]) == Node.goals[Goals.get(currentGoal).y][Goals.get(currentGoal).x]) {
                int playerToBoxDistance = ManhattanDistane(n.agentCol, n.agentRow, column, row);
                char playerToGoalDistance = GoalsBoard[currentGoal][n.agentRow][n.agentCol];
                int boxToGoalDistance = GoalsBoard[currentGoal][row][column];
                int boxPlayerGoalRoute = boxToGoalDistance + playerToBoxDistance + playerToGoalDistance;
                heuristicDistance += boxToGoalDistance;
                if (closestBoxGoalRoute > boxPlayerGoalRoute) {
                    closestBoxGoalRoute = boxPlayerGoalRoute;
                    PlayerToClosestBox = playerToBoxDistance;
                }
            }
        }
        int heuristics[] = new int[3];
        heuristics[0] = heuristicDistance;
        heuristics[1] = closestBoxGoalRoute;
        heuristics[2] = PlayerToClosestBox;
        return heuristics;
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
