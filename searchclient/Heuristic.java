package searchclient;

import java.util.ArrayList;
import java.util.Comparator;

import java.awt.Point;

import searchclient.NotImplementedException;

import static java.lang.Math.abs;

public abstract class Heuristic implements Comparator<Node> {
	private ArrayList<Point> goalPosition = new ArrayList<>();
	public Heuristic(Node initialState) {
        for(int row = 0; row < Node.MAX_ROW; ++row) {
            for (int column = 0; column < Node.MAX_COL; ++column) {
                 if(Node.goals[row][column] > 0) {
                    goalPosition.add(new Point(column, row));
                }
            }
        }
	}

	public int h(Node n) {
		ArrayList<Point> boxPosition = new ArrayList<>();
        for(int row = 0; row < Node.MAX_ROW; ++row) {
			for (int column = 0; column < Node.MAX_COL; ++column) {
				if(n.boxes[row][column] > 0) {
                    boxPosition.add(new Point(column, row));
                }
			}
		}
		int currentManhattanDistance = Node.MAX_ROW + Node.MAX_COL + 50;
		for ( int currentGoal = 0; currentGoal < goalPosition.size(); ++currentGoal) {
            for (int currentBox = 0; currentBox < boxPosition.size(); ++currentBox) {
                if(Character.toLowerCase(n.boxes[boxPosition.get(currentBox).y][boxPosition.get(currentBox).x]) ==
                                    Node.goals[goalPosition.get(currentGoal).y][goalPosition.get(currentGoal).x]) {
                    int boxToGoalManhattanDistance = abs(boxPosition.get(currentBox).x - goalPosition.get(currentGoal).x) +
                                                    abs(boxPosition.get(currentBox).y - goalPosition.get(currentGoal).y);
                    int playerToBoxManhattanDistance = abs(boxPosition.get(currentBox).x - n.agentCol) +
                                                        abs(boxPosition.get(currentBox).y - n.agentRow);
                    int heuristic = boxToGoalManhattanDistance + playerToBoxManhattanDistance;
                    if (currentManhattanDistance > heuristic) {
                        currentManhattanDistance = heuristic;
                    }
                }
            }
        }
        //System.err.println("Manhattan distance:" + currentManhattanDistance);
        return currentManhattanDistance;
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
