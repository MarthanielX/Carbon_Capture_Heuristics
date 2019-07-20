/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

/**
 *
 * @author sauerberg
 */
import java.util.ArrayList;
import java.util.Arrays;

public class CCS_Network {

    final private int n; // num vertices
    private ArrayList<Terminal> sources;
    private ArrayList<Terminal> sinks;
    private Edge[][] matrix; // doesn't handle multi-edges. should create multi-edge class to handle that

    public CCS_Network(int n, ArrayList<Terminal> sources, ArrayList<Terminal> sinks, Edge[][] matrix) {
        this.n = n;
        this.sources = sources;
        this.sinks = sinks;
        errorCheckTerminals();
        this.matrix = matrix;
    }

    public CCS_Network(int n, ArrayList<Terminal> sources, ArrayList<Terminal> sinks, ArrayList<Edge> edge_list) {
        this.n = n;
        this.sources = sources;
        this.sinks = sinks;
        errorCheckTerminals();
        matrix = new Edge[n][n];
        for (Edge edge : edge_list) {
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
        for (Edge[] row : matrix) {
            for (Edge e : row) {
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
        for (Edge[] row : matrix) {
            for (Edge e : row) {
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

    public void augmentAlongPath(double amount, int[] path) {
        getNode(path[0]).augmentFlow(amount);
        getNode(path[path.length - 1]).augmentFlow(amount);
        for (int i = 0; i < path.length - 1; i++) {
            matrix[path[i]][path[i + 1]].augmentFlow(amount);
        }
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

}
