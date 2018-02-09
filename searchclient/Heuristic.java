package searchclient;

import java.util.ArrayList;
import java.util.Comparator;

import java.awt.Point;
import java.util.List;

import searchclient.NotImplementedException;

import static java.lang.Math.abs;

public abstract class Heuristic implements Comparator<Node> {
	public Heuristic(Node initialState) {
		// Here's a chance to pre-process the static parts of the level.
	}

	public int h(Node n) {
        List<Point> boxPosition = new ArrayList<>();
        List<Point> goalPosition = new ArrayList<>();
        for(int row = 0; row < Node.MAX_ROW; ++row) {
			for (int column = 0;column < Node.MAX_COL; ++column) {
				if(n.boxes[row][column] != 0) {
                    boxPosition.add(new Point(row, column));
                }
                else if(Node.goals[row][column] != 0) {
                    goalPosition.add(new Point(row, column));
                }
			}
		}
		int manhattanDistance = Integer.MAX_VALUE;
		for ( Point currentBox : boxPosition) {
            for (Point currentGoal : goalPosition) {
                int currentManhattanDistance = abs(currentBox.x - currentGoal.x) + abs(currentBox.y - currentGoal.y);
                if (currentManhattanDistance < manhattanDistance) manhattanDistance = currentManhattanDistance;
            }
        }
		return manhattanDistance;
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
