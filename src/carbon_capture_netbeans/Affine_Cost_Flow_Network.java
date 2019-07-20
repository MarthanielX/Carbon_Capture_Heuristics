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
public class Affine_Cost_Flow_Network {
    int n; // node count
    int source;
    int sink;
    Edge[][] matrix;

    public Affine_Cost_Flow_Network(int n, int source, int sink, Edge[][] matrix) {
        this.n = n;
        this.source = source;
        this.sink = sink;
        this.matrix = matrix;
    }
    
    
    
}

