/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

/**
 * Represents an instance of the Carbon Capture and Storage Problem: A set of
 * sources, sinks, and edges each with a capacity and affine cost
 *
 * @author sauerberg
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

public class CCS_Network {

    final private int n; // num vertices
    private ArrayList<Terminal> sources;
    private ArrayList<Terminal> sinks;
    private SingleEdge[][] matrix; // doesn't handle multi-edges. should create multi-edge class to handle that

    public CCS_Network(int n, ArrayList<Terminal> sources, ArrayList<Terminal> sinks, SingleEdge[][] matrix) {
        this.n = n;
        this.sources = sources;
        this.sinks = sinks;
        errorCheckTerminals();
        this.matrix = matrix;
    }

    public CCS_Network(int n, ArrayList<Terminal> sources, ArrayList<Terminal> sinks, ArrayList<SingleEdge> edge_list) {
        this.n = n;
        this.sources = sources;
        this.sinks = sinks;
        errorCheckTerminals();
        matrix = new SingleEdge[n][n];
        for (SingleEdge edge : edge_list) {
            // throws array index out of bounds exception if edges have bad node indices
            matrix[edge.getStart()][edge.getEnd()] = edge;
        }
    }

    private void errorCheckTerminals() {
        boolean[] exists = new boolean[n];
        for (Terminal s : sources) {
            if (s.getIndex() < 0 || s.getIndex() > n - 1) {
                throw new IllegalArgumentException("Invalid Terminal Index");
            }
            if (exists[s.getIndex()]) {
                throw new IllegalArgumentException("Multiple Terminals at Same Index");
            }
            exists[s.getIndex()] = true;
        }
        for (Terminal t : sinks) {
            if (t.getIndex() < 0 || t.getIndex() > n - 1) {
                throw new IllegalArgumentException("Invalid Terminal Index");
            }
            if (exists[t.getIndex()]) {
                throw new IllegalArgumentException("Multiple Terminals at Same Index");
            }
            exists[t.getIndex()] = true;
        }
    }

    public boolean checkIsValid() {

        // check cap constraints on terminals
        for (Terminal s : sources) {
            if (!s.isValid()) {
                return false;
            }
        }
        for (Terminal t : sinks) {
            if (!t.isValid()) {
                return false;
            }
        }

        // check cap constraints on edges
        for (SingleEdge[] row : matrix) {
            for (SingleEdge e : row) {
                if (e != null && !e.isValid()) {
                    return false;
                }
            }
        }

        // check for conversation of flow at all nodes
        for (int i = 0; i < n; i++) {
            // compute the flow in and out 
            double in = 0;
            double out = 0;
            for (int j = 0; j < n; j++) {
                if (matrix[j][i] != null) {
                    in += matrix[j][i].getFlow();
                }
            }
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] != null) {
                    out += matrix[i][j].getFlow();
                }
            }
            // make sure it aligns 
            if (!isSource(i) && !isSink(i)) {
                if (in != out) {
                    return false;
                }
            } else if (isSource(i)) {
                if (out - in != getNode(i).getFlow()) {
                    return false;
                }
            } else { // is sink
                if (in - out != getNode(i).getFlow()) {
                    return false;
                }
            }
        }
        return true;
    }

    public double getFlow() {
        double flow = 0;
        for (Terminal s : sources) {
            flow += s.getFlow();
        }
        return flow;
    }

    public double getCost() {
        double cost = 0;
        for (Terminal s : sources) {
            cost += s.getCurrentCost();
        }
        for (Terminal t : sinks) {
            cost += t.getCurrentCost();
        }
        for (SingleEdge[] row : matrix) {
            for (SingleEdge e : row) {
                if (e != null) {
                    cost += e.getCurrentCost();
                }
            }
        }
        return cost;
    }

    public boolean isSource(int i) {
        for (Terminal s : sources) {
            if (i == s.getIndex()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSink(int i) {
        for (Terminal t : sinks) {
            if (i == t.getIndex()) {
                return true;
            }
        }
        return false;
    }

    public Terminal getNode(int index) {
        for (Terminal s : sources) {
            if (index == s.getIndex()) {
                return s;
            }
        }
        for (Terminal t : sinks) {
            if (index == t.getIndex()) {
                return t;
            }
        }
        return null;
    }

    public void augmentAlongPath(ArrayList<Integer> path, double amount) {
        getNode(path.get(0)).augmentFlow(amount);
        getNode(path.get(path.size() - 1)).augmentFlow(amount);
        for (int i = 0; i < path.size() - 1; i++) {
            matrix[path.get(i)][path.get(i + 1)].augmentFlow(amount);
        }
    }

    /**
     * Uses Dijkstra's to find the cheapest start-end path to route flow equal
     * to the minimum of the start and end node capacities
     *
     * @param start the index of the start node
     * @param end the index of the end node
     * @param max_flow amount of flow not to be exceeded; generally is the
     * amount of flow remaining before reaching the demand
     * @return A tuple containing the path that was found and its cost
     * @throws Exception (shouldn't happen, just for error checking)
     */
    private PathCostFlow findCheapestPath(int start, int end, double max_flow) throws Exception {
        if (getNode(start) == null || getNode(end) == null
                || !getNode(start).isSource() || getNode(end).isSource()) {
            throw new IllegalArgumentException("start must be the index of a source and end must be the index of a sink");
        }
        double amount = Math.min(Math.min(getNode(start).getResidualCapacity(),
                getNode(end).getResidualCapacity()), max_flow);

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }
        double[] cost = new double[n];
        for (int i = 0; i < n; i++) {
            cost[i] = Integer.MAX_VALUE;
        }
        cost[start] = 0;

        PriorityQueue<IndexCostTuple> queue = new PriorityQueue<IndexCostTuple>();
        queue.add(new IndexCostTuple(start, 0));

        while (!queue.isEmpty()) {
            IndexCostTuple current = queue.poll();

            // we found the target node
            if (current.getIndex() == end) {
                ArrayList<Integer> path = new ArrayList<Integer>();
                path.add(new Integer(end));
                while (path.get(path.size() - 1) != start) {
                    path.add(parent[path.get(path.size() - 1)]);
                }
                Collections.reverse(path);
                if ((path.get(0) != start) || (path.get(path.size() - 1) != end)) {
                    throw new Exception("I fucked up coding Dijkstra's");
                }
                return new PathCostFlow(path, cost[end], amount);
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
     * path that saturates a (real) source with the best cost to flow ratio
     *
     * @param demand the required amount of flow
     * @return returns false if the max flow is less than demand, true o.w.
     * @throws Exception
     */
    public boolean solveSeanHeuristic(double demand) throws Exception {
        //Note: this heuristic is slightly different from Sean's original heuristic
        // bc it won't even consider using a (real) sink if that sink has residual capacity 
        // less than the lowest (real) source residual capacity

        while (demand - getFlow() > 0) {
            PathCostFlow cheapest = new PathCostFlow(new ArrayList<Integer>(), Double.MAX_VALUE, 0);
            for (Terminal s : sources) {
                for (Terminal t : sinks) {
                    PathCostFlow current = findCheapestPath(s.getIndex(), t.getIndex(), demand - getFlow());
                    if (current != null
                            && ((cheapest.getFlow() / cheapest.getCost()) < (current.getFlow() / current.getCost()))) {
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
        System.out.println("\nSources Purchased:");
        for (Terminal s : sources) {
            if (s.isOpen()) {
                System.out.println(s);
            }
        }
        System.out.println("\nSinks Purchased:");
        for (Terminal t : sinks) {
            if (t.isOpen()) {
                System.out.println(t);
            }
        }
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

    public Affine_Cost_Flow_Network convertToAffineCostFlowNetwork() {
        SingleEdge[][] new_matrix = new SingleEdge[n + 2][n + 2];
        for (Terminal s : sources) {
            new_matrix[0][s.getIndex() + 1] = new SingleEdge(0, s.getIndex() + 1,
                    s.getCapacity(), s.getFixed_cost(), s.getVariable_cost(),
                    s.isOpen(), s.getFlow());
        }
        for (Terminal t : sinks) {
            new_matrix[t.getIndex() + 1][n + 1] = new SingleEdge(0, t.getIndex() + 1,
                    t.getCapacity(), t.getFixed_cost(), t.getVariable_cost(),
                    t.isOpen(), t.getFlow());
        }
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                SingleEdge e = matrix[row][col];
                if (e != null) {
                    new_matrix[row + 1][col + 1] = new SingleEdge(e.getStart() + 1,
                            e.getEnd() + 1, e.getCapacity(), e.getFixed_cost(),
                            e.getVariable_cost(), e.isOpen(), e.getFlow());
                }
            }
        }
        return new Affine_Cost_Flow_Network(n + 2, new_matrix);
    }

}
