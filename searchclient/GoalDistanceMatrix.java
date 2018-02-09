package searchclient;

public class GoalDistanceMatrix {
    public int[][] distanceMatrix;
    public char goal;

    GoalDistanceMatrix(int[][] distanceMatrix, char goal) {
        this.distanceMatrix = distanceMatrix;
        this.goal = goal;
    }
}
