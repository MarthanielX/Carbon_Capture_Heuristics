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
import java.util.Arrays;

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
    public static void main(String[] args) throws Exception {

        /*
        ArrayList<Terminal> sources = new ArrayList<Terminal>();
        sources.add(new Terminal(0, true, 100, 25, 1));
        sources.add(new Terminal(1, true, 50, 10, 1));

        ArrayList<Terminal> sinks = new ArrayList<Terminal>();
        sinks.add(new Terminal(4, false, 100, 25, 1));
        sinks.add(new Terminal(5, false, 50, 10, 1));
        
        ArrayList<SingleEdge> edges = new ArrayList<SingleEdge>();
        edges.add(new SingleEdge(0, 2, 200, 10, .1));
        edges.add(new SingleEdge(0, 3, 200, 10, .1));
        edges.add(new SingleEdge(1, 2, 200, 10, .1));
        edges.add(new SingleEdge(1, 3, 200, 10, .1));
        edges.add(new SingleEdge(2, 4, 200, 10, .1));
        edges.add(new SingleEdge(2, 5, 200, 10, .1));
        edges.add(new SingleEdge(3, 4, 200, 10, .1));
        edges.add(new SingleEdge(3, 5, 200, 10, .1));
        
        Single_Edge_CCS_Network graph = new Single_Edge_CCS_Network(6, sources, sinks, edges);
        
        Single_Edge_Flow_Network macf = graph.convertToAffineCostFlowNetwork();
        
        macf.solveSeanHeuristic(150);
        macf.printFlow();
        */ 
        
        
        GraphGenerator generator = new GraphGenerator(20, 100, 100);
        MultiEdge[] edges = generator.generateGraph();
        Flow_Network graph = new Flow_Network(22, edges);
        System.out.println(graph.isValid());

        graph.solveCheapestPathHeuristic(200);

        System.out.println("\nFinal Cost: " + graph.getCost());
        System.out.println("Final Flow: " + graph.getFlow());

        System.out.println(graph.isValid());

        /*
        System.out.println(graph.checkIsValid());
        graph.printFlow();
        
        System.out.println(graph.solveSeanHeuristic(151));
        
        graph.printFlow();
        System.out.println(graph.checkIsValid());
        
        Single_Edge_Flow_Network net = graph.convertToAffineCostFlowNetwork();
        Single_Edge_CCS_Network graph2 = net.convertToCCS_Network();
        
        graph.printFlow();
        System.out.println("Graph2:");
        graph2.printFlow();
         */
    }

    /**
     * Ideas for other Heuristics: pick the path that's cheapest to open
     * (Brendan), fully saturate repeatedly route the single cheapest unit of
     * flow (slow)
     *
     */
}
