/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Note: the source is index 0 and sink is index n-1
 *
 * @author sauerberg
 */
public class Affine_Cost_Flow_Network {

    int n; // node count
    Edge[][] matrix;

    public Affine_Cost_Flow_Network(int n, Edge[][] matrix) {
        this.n = n;
        this.matrix = matrix;
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

    public CCS_Network convertToCCS_Network() {
        ArrayList<Terminal> sources = new ArrayList<Terminal>();
        for (int i = 0; i < n; i++) {
            if (matrix[0][i] != null) {
                sources.add(new Terminal(i - 1, true, matrix[0][i].getCapacity(),
                        matrix[0][i].getFixed_cost(),
                        matrix[0][i].getVariable_cost(), 
                        matrix[0][i].isOpen(),
                        matrix[0][i].getFlow() ));
            }
        }
        ArrayList<Terminal> sinks = new ArrayList<Terminal>();
        for (int i = 0; i < n; i++) {
            if (matrix[i][n - 1] != null) {
                sinks.add(new Terminal(i - 1, false, matrix[i][n-1].getCapacity(),
                        matrix[i][n-1].getFixed_cost(),
                        matrix[i][n-1].getVariable_cost(),
                        matrix[i][n-1].isOpen(),
                        matrix[i][n-1].getFlow()));
            }
        }
        Edge[][] new_matrix = new Edge[n - 2][n - 2];
        for (int i = 0; i < n - 2; i++) {
            for (int j = 0; j < n - 2; j++) {
                if (matrix[i + 1][j + 1] != null) {
                    new_matrix[i][j] = new Edge(i, j,
                            matrix[i + 1][j + 1].getCapacity(),
                            matrix[i + 1][j + 1].getFixed_cost(),
                            matrix[i + 1][j + 1].getVariable_cost(),
                            matrix[i + 1][j + 1].isOpen(),
                            matrix[i + 1][j + 1].getFlow());
                }
            }
        }

        return new CCS_Network(n - 2, sources, sinks, new_matrix);
    }

}
