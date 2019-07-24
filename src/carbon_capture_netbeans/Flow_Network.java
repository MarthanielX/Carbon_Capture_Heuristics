/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Note: the source is index 0 and sink is index n-1
 *
 * @author sauerberg
 */
public class Flow_Network {

    int n; // node count
    Edge[][] matrix;

    public Flow_Network(int n, Edge[][] matrix) {
        this.n = n;
        this.matrix = matrix;
    }

    public Flow_Network(int n, MultiEdge[] edges) {
        this.n = n;
        matrix = new Edge[n][n];
        for (MultiEdge e : edges) {
            matrix[e.getStart()][e.getEnd()] = e;
            matrix[e.getEnd()][e.getStart()] = new Reverse_MultiEdge(e);
        }
    }

    public int getN() {
        return n;
    }

    public Edge[][] getMatrix() {
        return matrix;
    }

    public double getFlow() {
        double flow = 0;
        for (int i = 0; i < n; i++) {
            if (matrix[0][i] != null) {
                flow += matrix[0][i].getFlow();
            }
        }
        return flow;
    }

    public double getCost() {
        double cost = 0;
        for (Edge[] row : matrix) {
            for (Edge e : row) {
                if (e != null) {
                    cost += e.getCurrentCost();
                }
            }
        }
        return cost;
    }

    public void augmentAlongPath(ArrayList<Integer> path, double amount) {
        for (int i = 0; i < path.size() - 1; i++) {
            matrix[path.get(i)][path.get(i + 1)].augmentFlow(amount);
        }
    }

    /**
     * Uses Bellman-Ford to find the s-t path that is cheapest to 'open', ie the
     * path that's cheapest in terms of only the fixed cost to increase flow
     *
     * @return A tuple containing the path that was found, its cost, and its
     * maximum possible flow
     */
    public PathCostFlow findCheapestPath() {
        double[] cost = new double[n];
        int[] pred = new int[n];
        for (int i = 0; i < n; i++) {
            cost[i] = Double.MAX_VALUE;
            pred[i] = -1;
        }
        cost[0] = 0;

        // repeatedly relax # edges allowed
        for (int i = 0; i < n; i++) {
            for (Edge[] row : matrix) {
                for (Edge e : row) {
                    if (e != null && cost[e.getEnd()] > cost[e.getStart()] + e.getFixedCostToIncreaseFlow()) {
                        cost[e.getEnd()] = cost[e.getStart()] + e.getFixedCostToIncreaseFlow();
                        pred[e.getEnd()] = e.getStart();
                    }
                }
            }
        }

        // check for negative cycles
        for (Edge[] row : matrix) {
            for (Edge e : row) {
                if (e != null && cost[e.getEnd()] > cost[e.getStart()] + e.getFixedCostToIncreaseFlow()) {
                    throw new IllegalArgumentException("The graph contains negative cycles.");
                }
            }
        }

        // compute the path and capacity from the arrays
        double capacity = Double.MAX_VALUE;
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(n);
        while (path.get(path.size() - 1) != 0) {
            Edge e = matrix[pred[path.get(path.size() - 1)]][path.get(path.size() - 1)];
            if (e.getFixedCostToIncreaseFlow() == Double.MAX_VALUE) {
                return null; // no path exists
            }
            capacity = Math.min(capacity, e.getResidualCapacity());
            path.add(e.getStart());
        }
        Collections.reverse(path);

        double total_cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total_cost += matrix[path.get(i)][path.get(i + 1)].getCost(capacity);
        }

        return new PathCostFlow(path, total_cost, capacity);
    }

    /**
     * Uses Bellman-Ford to find the s-t path that would be cheapest to route
     * amount units of flow along
     *
     * @param amount the amount of flow desired
     * @return A tuple containing the path that was found, its cost, and its
     * maximum possible flow
     */
    public PathCostFlow findCheapestPath(double amount) {
        double[] cost = new double[n];
        int[] pred = new int[n];
        for (int i = 0; i < n; i++) {
            cost[i] = Double.MAX_VALUE;
            pred[i] = -1;
        }
        cost[0] = 0;

        // repeatedly relax # edges allowed
        for (int i = 0; i < n; i++) {
            for (Edge[] row : matrix) {
                for (Edge e : row) {
                    if (e != null && cost[e.getEnd()] > cost[e.getStart()] + e.getCost(amount)) {
                        cost[e.getEnd()] = cost[e.getStart()] + e.getCost(amount);
                        pred[e.getEnd()] = e.getStart();
                    }
                }
            }
        }

        // check for negative cycles
        for (Edge[] row : matrix) {
            for (Edge e : row) {
                if (e != null && cost[e.getEnd()]
                        > cost[e.getStart()] + e.getCost(amount)) {
                    throw new IllegalArgumentException(
                            "The graph contains negative cycles.");
                }
            }
        }

        // compute the path, cost, and capacity from the arrays
        double total_cost = 0;
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(n);
        while (path.get(path.size() - 1) != 0) {
            Edge e = matrix[pred[path.get(path.size() - 1)]][path.get(path.size() - 1)];
            if (e.getCost(amount) == Double.MAX_VALUE) {
                return null; // no path exists
            }
            total_cost += e.getCost(amount);
            path.add(e.getStart());
        }
        Collections.reverse(path);

        return new PathCostFlow(path, total_cost, amount);
    }

    /**
     * Greedily solves the Min-Cost Flow problem by repeatedly finding the s-t
     * path with the minimum cost to open and then fully saturating it
     *
     * @param demand the required amount of s-t flow
     * @return returns false if the max flow is less than demand, true o.w.
     */
    public boolean solveCheapestPathHeuristic(double demand) {
        while (demand - getFlow() > 0) {
            PathCostFlow cheapest = findCheapestPath();
            if (cheapest == null) {
                return false; // max flow of network is less than demand
            }
            augmentAlongPath(cheapest.getPath(), cheapest.getFlow());
        }
        return true;
    }

    /**
     * Greedily solves the Min-Cost Flow problem by repeatedly considering all
     * paths that fully saturate a source or sink and the single cheapest path
     * to open, and then fully saturating the most cost effective of these
     *
     * @param demand the required amount of s-t flow
     * @return false if the graph's max flow is less than demand, true o.w
     */
    public boolean solveNathanielHeuristic(double demand) {
        while (demand - getFlow() > 0) {
            PathCostFlow cheapest = findCheapestPath();
            for (int i = 0; i < n; i++) {
                if (matrix[0][i] != null) {
                    // consider the cheapest path saturating source i
                    PathCostFlow current = findCheapestPath(
                            Math.min(demand - getFlow(),
                                    matrix[0][i].getResidualCapacity(1)));
                    if (current != null
                            && (current.getFlowOverCost()
                            > cheapest.getFlowOverCost())) {
                        cheapest = current;
                    }
                }
            }
            if (cheapest.getFlow() == 0) {
                return false; // already at max flow
            }
            augmentAlongPath(cheapest.getPath(), cheapest.getFlow());
        }
        return true;
    }

    /**
     * Greedily solves the Min-Cost Flow problem by repeatedly considering all
     * paths that fully saturate a source or sink and augmenting along the path
     * that is cheapest per unit flow
     *
     * @param demand the required amount of s-t flow
     * @return false if the graph's max flow is less than demand, true o.w
     */
    public boolean solveSeanHeuristic(double demand) {
        while (demand - getFlow() > 0) {
            PathCostFlow cheapest = new PathCostFlow(
                    new ArrayList<Integer>(), Double.MAX_VALUE, 0);
            for (int i = 0; i < n; i++) {
                if (matrix[0][i] != null) {
                    // consider the cheapest path saturating source i
                    PathCostFlow current = findCheapestPath(
                            Math.min(demand - getFlow(),
                                    matrix[0][i].getResidualCapacity(1)));
                    if (current != null
                            && (current.getFlowOverCost()
                            > cheapest.getFlowOverCost())) {
                        cheapest = current;
                    }
                }
            }
            if (cheapest.getFlow() == 0) {
                return false; // already at max flow
            }
            augmentAlongPath(cheapest.getPath(), cheapest.getFlow());
        }
        return true;
    }

    public void printFlow() {
        System.out.println("\nTotal Flow:" + getFlow());
        System.out.println("Total Cost:" + getCost());

        System.out.println("\nEdge Flows:");
        String[][] flows = new String[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == null) {
                    flows[i][j] = "--";
                } else {
                    flows[i][j] = "" + matrix[i][j].getFlow();
                }
            }
        }
        System.out.println(Arrays.deepToString(flows)
                .replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

    }

}
