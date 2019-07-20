/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author sauerberg
 */
public class Carbon_Capture_NetBeans {
   
    // ToDo List
    // Implement Sean's Heuristic Alg
    // Implement Brendan's Heuristic Alg (just cheapest path, not most cost effective)
    
    // Code Improvement:
    // make a superclass of terminal and edge to handle cap, costs, flow, and isOpen 
    // make methods just return false and do nothing if the action was invalid, rather than throwing an exception

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Terminal> sources = new ArrayList<Terminal>();
        sources.add(new Terminal(0, true, 100, 25, 1));
        sources.add(new Terminal(1, true, 50, 10, 1));

        ArrayList<Terminal> sinks = new ArrayList<Terminal>();
        sinks.add(new Terminal(4, false, 100, 25, 1));
        sinks.add(new Terminal(5, false, 50, 10, 1));
        
        ArrayList<Edge> edges = new ArrayList<Edge>();
        edges.add(new Edge(0, 2, 200, 10, .1));
        edges.add(new Edge(0, 3, 200, 10, .1));
        edges.add(new Edge(1, 2, 200, 10, .1));
        edges.add(new Edge(1, 3, 200, 10, .1));
        edges.add(new Edge(2, 4, 200, 10, .1));
        edges.add(new Edge(2, 5, 200, 10, .1));
        edges.add(new Edge(3, 4, 200, 10, .1));
        edges.add(new Edge(3, 5, 200, 10, .1));
        
        CCS_Network graph = new CCS_Network(6, sources, sinks, edges);
        System.out.println(graph.checkIsValid());
        graph.printFlow();
        
        graph.augmentAlongPath(100, new int[]{0, 2, 4});
        graph.printFlow();
        System.out.println(graph.checkIsValid());

    }
    
    private void solve_Sean_Heuristic(CCS_Network graph, double demand){
        
    }

}
