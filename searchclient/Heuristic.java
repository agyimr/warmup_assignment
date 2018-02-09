package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.awt.Point;
import java.util.List;

import searchclient.NotImplementedException;

import static java.lang.Math.abs;

public abstract class Heuristic implements Comparator<Node> {
	private ArrayList<GoalDistanceMatrix> goalDistances;

	public Heuristic(Node initialState) {
		// Here's a chance to pre-process the static parts of the level.
		goalDistances = countDistancesFromGoals(initialState);
		for (int[] row : goalDistances.get(0).distanceMatrix) {
			for (int val : row) {
				System.err.print(val);
			}
			System.err.print("\n");
		}
	}

	public int h(Node n) {
		int result = 0;
		// Basically the sum of all of the shortest path distances for every box to a goal
		int closestToGoal = Integer.MAX_VALUE;
		int closestToGoalBoxDistance = 0;
		for (int i = 0; i < n.boxes.length; i++) {        // i: row
			for (int j = 0; j < n.boxes[i].length; j++) { // j: column
				if (SearchClient.isBox(n.boxes[i][j])) {
					if(Character.toUpperCase(Node.goals[i][j]) != n.boxes[i][j]) {
						char currentBoxGoal = Character.toLowerCase(n.boxes[i][j]);
						for (GoalDistanceMatrix goalDistance : goalDistances) {
							if (goalDistance.goal == currentBoxGoal) {
								result += goalDistance.distanceMatrix[i][j] * 10;
								if (closestToGoal > goalDistance.distanceMatrix[i][j]) {
									closestToGoal = goalDistance.distanceMatrix[i][j];
									closestToGoalBoxDistance = (Math.abs(n.agentCol - j) + Math.abs(n.agentRow - i));

								}
							}
						}
					}
				}
			}
		}
		result += closestToGoalBoxDistance;
		return result;
	}

	public abstract int f(Node n);

	private ArrayList<GoalDistanceMatrix> countDistancesFromGoals(Node n) {
		// getting positions of goals
		ArrayList<Coordinate> goals = new ArrayList<>();
		for (int i = 0; i < Node.goals.length; i++) {        // i: row
			for (int j = 0; j < Node.goals[i].length; j++) { // j: column
				if (SearchClient.isGoal(Node.goals[i][j])) {
					goals.add(new Coordinate(j, i, Node.goals[i][j]));
				}
			}
		}

		// creating distance matrix for all goals
		ArrayList<GoalDistanceMatrix> goalDistances = new ArrayList<>();
		for (Coordinate goal : goals) {
			int[][] distances = new int[Node.MAX_ROW][Node.MAX_COL];
			for (int[] row: distances)
				Arrays.fill(row, -1);
			int distance = 0;
			ArrayList<Coordinate> endpoints = new ArrayList<>();
			System.err.print(goal.getX() + ", " + goal.getY() + "\n");
			endpoints.add(goal);
			while (!endpoints.isEmpty()) {
				for (Coordinate endpoint : endpoints) {
					distances[endpoint.getY()][endpoint.getX()] = distance;
				}
				distance++;
				ArrayList<Coordinate> newEndpoints = getValidNeighbours(endpoints, distances);
				endpoints.clear();
				endpoints.addAll(newEndpoints);
			}
			goalDistances.add(new GoalDistanceMatrix(distances, goal.getValue()));
		}
		return goalDistances;
	}

	private ArrayList<Coordinate> getValidNeighbours(ArrayList<Coordinate> endpoints, int[][] distances) {
		ArrayList<Coordinate> validNeighbours = new ArrayList<>();
		for (Coordinate endpoint : endpoints) {
			Coordinate[] neighbours = endpoint.getNeighbours();
			for (Coordinate neighbour : neighbours) {
				if (neighbour.getX() < Node.MAX_COL && neighbour.getY() < Node.MAX_ROW
						&& neighbour.getX() >= 0 && neighbour.getY() >= 0) { // neighbour is inside map range
					if (!Node.walls[neighbour.getY()][neighbour.getX()] &&
						distances[neighbour.getY()][neighbour.getX()] == -1 &&
						!validNeighbours.contains(neighbour)	) { // neighbour is not populated yet and not wall
						validNeighbours.add(neighbour);
					}
				}
			}
		}
		return validNeighbours;
	}

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

