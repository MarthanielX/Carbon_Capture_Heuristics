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
public class Single_Edge_Flow_Network {

    int n; // node count
    SingleEdge[][] matrix;

    public Single_Edge_Flow_Network(int n, SingleEdge[][] matrix) {
        this.n = n;
        this.matrix = matrix;
    }

    public int getN() {
        return n;
    }

    public SingleEdge[][] getMatrix() {
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
        for (SingleEdge[] row : matrix) {
            for (SingleEdge e : row) {
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
     * Uses Dijkstra's to find the cheapest s-t path using edge s-start to route
     * flow equal to the min of capacity(s-start) and max_flow
     *
     * @param start the index of the first node to route the flow through
     * @param max_flow amount of flow not to be exceeded; generally is the
     * amount of flow remaining before reaching the demand
     * @return A tuple containing the path that was found and its cost
     * @throws Exception (error checking or invalid input)
     */
    private PathCostFlow findCheapestPath(int start, double max_flow) throws Exception {
        if (matrix[0][start] == null) {
            // replace with return false;?
            throw new IllegalArgumentException("start must be adjacent to the source");
        }
        double amount = Math.min(matrix[0][start].getResidualCapacity(), max_flow);

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }
        parent[start] = 0;

        double[] cost = new double[n];
        for (int i = 0; i < n; i++) {
            cost[i] = Integer.MAX_VALUE;
        }
        cost[start] = matrix[0][start].getCost(amount);
        // we leave the supersource as having cost max_value so that we go through the desired edge

        PriorityQueue<IndexCostTuple> queue = new PriorityQueue<IndexCostTuple>();
        queue.add(new IndexCostTuple(start, 0));

        while (!queue.isEmpty()) {
            IndexCostTuple current = queue.poll();

            // we've reached the sink
            if (current.getIndex() == n) {
                ArrayList<Integer> path = new ArrayList<Integer>();
                path.add(new Integer(n));
                while (path.get(path.size() - 1) != 0) {
                    path.add(parent[path.get(path.size() - 1)]);
                }
                Collections.reverse(path);
                if ((path.get(0) != 0) || (path.get(1) != start)
                        || (path.get(path.size() - 1) != n)) {
                    throw new Exception("I fucked up coding Dijkstra's");
                }
                return new PathCostFlow(path, cost[n], amount);
            }

            // iterate through all possible neighbors of the current node
            for (int i = 0; i < n; i++) {
                SingleEdge e = matrix[current.getIndex()][i];
                if ((!visited[i]) && (e != null)
                        && (current.getCost() + e.getCost(amount) < cost[i])) {
                    cost[i] = current.getCost() + e.getCost(amount);
                    parent[i] = current.getIndex();

                    // update the entry for this node in the queue
                    IndexCostTuple newCost = new IndexCostTuple(i, cost[i]);
                    queue.remove(newCost);
                    queue.add(newCost);
                }
            }
            visited[current.getIndex()] = true;
        }

        return null; //the target node was unreachable
    }

     /**
     * Greedily solves the Min-Cost Flow problem by repeatedly adding the s-t
     * path with the best cost to flow ratio
     *
     * @param demand the required amount of flow
     * @return returns false if the max flow is less than demand, true o.w.
     * @throws Exception
     */
    public boolean solveSeanHeuristic(double demand) throws Exception {
        while (demand - getFlow() > 0) {
            PathCostFlow cheapest = new PathCostFlow(new ArrayList<Integer>(), Double.MAX_VALUE, 0);
            for (int i = 0; i < n; i++) {
                if (matrix[0][i] != null) {
                    // find cheapest s-t path using this edge to determine flow
                    PathCostFlow current = findCheapestPath(i, demand - getFlow());
                    if (current != null && 
                            ((cheapest.getFlow() / cheapest.getCost()) 
                            < (current.getFlow() / current.getCost()))) {
                        cheapest = current;
                    }
                }
            }
            if (cheapest.getFlow() == 0) {
                return false; // max flow of network is less than demand
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
                    flows[i][j] = matrix[i][j].getFlow() + "/" + matrix[i][j].getCapacity();
                }
            }
        }
        System.out.println(Arrays.deepToString(flows)
                .replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

    }

    public Single_Edge_CCS_Network convertToCCS_Network() {
        ArrayList<Terminal> sources = new ArrayList<Terminal>();
        for (int i = 0; i < n; i++) {
            if (matrix[0][i] != null) {
                sources.add(new Terminal(i - 1, true, matrix[0][i].getCapacity(),
                        matrix[0][i].getFixed_cost(),
                        matrix[0][i].getVariable_cost(),
                        matrix[0][i].isOpen(),
                        matrix[0][i].getFlow()));
            }
        }
        ArrayList<Terminal> sinks = new ArrayList<Terminal>();
        for (int i = 0; i < n; i++) {
            if (matrix[i][n - 1] != null) {
                sinks.add(new Terminal(i - 1, false, matrix[i][n - 1].getCapacity(),
                        matrix[i][n - 1].getFixed_cost(),
                        matrix[i][n - 1].getVariable_cost(),
                        matrix[i][n - 1].isOpen(),
                        matrix[i][n - 1].getFlow()));
            }
        }
        SingleEdge[][] new_matrix = new SingleEdge[n - 2][n - 2];
        for (int i = 0; i < n - 2; i++) {
            for (int j = 0; j < n - 2; j++) {
                if (matrix[i + 1][j + 1] != null) {
                    new_matrix[i][j] = new SingleEdge(i, j,
                            matrix[i + 1][j + 1].getCapacity(),
                            matrix[i + 1][j + 1].getFixed_cost(),
                            matrix[i + 1][j + 1].getVariable_cost(),
                            matrix[i + 1][j + 1].isOpen(),
                            matrix[i + 1][j + 1].getFlow());
                }
            }
        }

        return new Single_Edge_CCS_Network(n - 2, sources, sinks, new_matrix);
    }

}
