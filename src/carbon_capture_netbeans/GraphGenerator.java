package carbon_capture_netbeans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author lally
 */

public class GraphGenerator {
    private int node_count;
    private int x_max;
    private int y_max;

    private static double[] CAP = new double[]{50, 200, 500, 1000};
    private static double[] FC = new double[]{75, 150, 300, 550};
    private static double[] VC = new double[]{1, .8, .6, .4};

    //generates a graph with known dimensions
    public GraphGenerator(int node_count, int x_max, int y_max) {
        this.node_count = node_count;
        this.x_max = x_max;
        this.y_max = y_max;
    }

    //generates a graph with default dimensions
    public GraphGenerator (int node_count) {
        this.node_count = node_count;
        this.x_max = 100;
        this.y_max = 100;
    }

    // creates a graph, writes nodes and edges to two csv files for optional later processing
    public MultiEdge [] generateGraph () {
        // make the graph nodes
        Node[] nodes = new Node[node_count];
        ArrayList<Node> source_nodes = new ArrayList<Node>();
        ArrayList<Node> sink_nodes = new ArrayList<Node>();

        // makes nodes, adds nodes to arrayList of appropriate type for connection to super nodes later
        for (int i = 0; i < node_count; i++) {
            nodes[i] = generateNode(x_max, y_max, i );
            switch (nodes[i].getNode_type()) {
                case SOURCE:
                    source_nodes.add(nodes[i]);
                    break;
                case SINK:
                    sink_nodes.add(nodes[i]);
                    break;
            }
        }
        countNodeTypes(nodes);
        //TODO comment out csv line below for testing
        //writeNodesToCSV(nodes);

        // find the distances between each node
        double [][] distance_adjacency_matrix = new double [node_count][node_count];
        for (int i = 0; i < node_count; i ++) {
            for (int j = 0; j < node_count; j ++) {
                double y1 = nodes[i].getY();
                double y2 = nodes[j].getY();
                double x1 = nodes[i].getX();
                double x2 = nodes[j].getX();
                distance_adjacency_matrix[i][j] = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
            }
        }

        // makes empty matrix of node connections
        boolean [][] connection_adjacency_matrix = new boolean [node_count][node_count];
        for (int i = 0; i < connection_adjacency_matrix.length; i ++) {
            for (int j = 0; j < connection_adjacency_matrix[i].length; j ++) {
                connection_adjacency_matrix[i][j] = false;
            }
        }

        int neighbor_count = 3;

        // makes a copy of the distance matrix
        double [][] decision_matrix = new double [distance_adjacency_matrix.length][distance_adjacency_matrix.length];
        for (int i = 0; i < distance_adjacency_matrix.length; i ++) {
            System.arraycopy(distance_adjacency_matrix [i], 0, decision_matrix[i], 0, distance_adjacency_matrix[i].length);
        }

        // finds the nodes with the smallest distances from each other, marks them to be connected in the connection
        // matrix
        for (int i = 0; i < node_count; i ++) {
            for (int k = 0; k < neighbor_count; k ++) {
                int smallestDistanceIndex = findSmallest(decision_matrix[i]);
                decision_matrix[i][smallestDistanceIndex] = 0;
                connection_adjacency_matrix[i][smallestDistanceIndex] = true;
            }
        }

        // makes an arraylist of edges between connected nodes
        ArrayList <MultiEdge> edges = new ArrayList<>();

        for (int i = 0; i < node_count; i ++) {
            for (int j = i; j < node_count; j++) {
                if (connection_adjacency_matrix[i][j]) {
                    edges.add(generateMultiEdge(i + 1, j + 1, distance_adjacency_matrix[i][j]));
                }
            }
        }

        // add the super source and super sink edges
        for (Node n: source_nodes){
            edges.add(generateVirtualEdge(0, n.getId() + 1));
        }

        for (Node n: sink_nodes) {
            edges.add(generateVirtualEdge(n.getId() + 1, node_count + 1));
        }

        MultiEdge [] edge_array = new MultiEdge [edges.size()];

        // converts arraylist to array
        for (int i = 0; i < edge_array.length; i ++){
            edge_array[i] = edges.get(i);
            //System.out.println(edge_array[i]);
        }

        //TODO comment out csv line below for testing
        //writeEdgesToCSV(edge_array);
        return edge_array;
    }

    // creates a node with a random position and a semi random type (40% source, 40% sink, 20% pipe junction)
    private Node generateNode (int max_x, int max_y, int node_id) {
        Random rand = new Random ();
        int x = rand.nextInt(max_x);
        int y = rand.nextInt(max_y);
        NodeType nodeType = generateRandomNodeType();

        return new Node(x, y, node_id, nodeType);
    }

    // generates a random node type (source, sink, junction)
    private NodeType generateRandomNodeType () {
        Random rand = new Random();
        int key = rand.nextInt(10);
        if (key >= 0 && key < 4) {
            return NodeType.SOURCE;
        } else if (key >= 4 && key < 8) {
            return NodeType.SINK;
        }
        return NodeType.JUNCTION;
    }

    // writes a given list of nodes to a csv file
    private void writeNodesToCSV (Node [] nodes) {
        try {
            File node_file = new File("C:\\Users\\book_\\Documents\\Summer2019\\graphVisualizer\\node_file_tester.csv");
            boolean fvar = node_file.createNewFile();
            if (!fvar) {
                node_file.delete();
                fvar = node_file.createNewFile();
            }

            FileWriter csv_node_writer = new FileWriter("C:\\Users\\book_\\Documents\\Summer2019\\graphVisualizer\\node_file_tester.csv");
            csv_node_writer.append("id");
            csv_node_writer.append(",");
            csv_node_writer.append("x_coordinate");
            csv_node_writer.append(",");
            csv_node_writer.append("y_coordinate");
            csv_node_writer.append(",");
            csv_node_writer.append("node_type");
            csv_node_writer.append("\n");

            for (Node n: nodes) {
                csv_node_writer.append(String.valueOf(n.getId() + 1));
                csv_node_writer.append(",");
                csv_node_writer.append(String.valueOf(n.getX()));
                csv_node_writer.append(",");
                csv_node_writer.append(String.valueOf(n.getY()));
                csv_node_writer.append(",");
                csv_node_writer.append(String.valueOf(n.getNode_type()));
                csv_node_writer.append("\n");
            }

            csv_node_writer.flush();
            csv_node_writer.close();

        } catch (IOException e) {
            System.out.println("Exception Occurred:");
            e.printStackTrace();
        }

    }

    // counts the different kinds of nodes from a given array and prints them
    private void countNodeTypes (Node [] nodes) {
        int sink_counter = 0;
        int source_counter = 0;
        int junction_counter = 0;

        for (int i = 0; i < node_count; i++) {
            switch (nodes[i].getNode_type()) {
                case SOURCE:source_counter++;
                    break;
                case SINK: sink_counter++;
                    break;
                case JUNCTION: junction_counter++;
                    break;
            }
        }
        //System.out.println(source_counter + ", " + sink_counter + ", " + junction_counter);
    }

    // finds the smallest value in a given array
    private int findSmallest (double [] array) {
        int index = -1;
        double largestNumber = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i ++) {
            if (array[i] > 0 && array[i] < largestNumber) {
                index = i;
                largestNumber = array[i];
            }
        }
        return index;
    }

    // generates a random multiedge used for real edges
    private MultiEdge generateMultiEdge (int start, int end, double length) {
        Random rand = new Random();
        double [] capacities = new double[CAP.length];
        double [] fixed = new double[CAP.length];
        double [] variable = new double[CAP.length];
        for (int i = 0; i < CAP.length; i++){
            capacities[i] = CAP[i] * (rand.nextDouble()+.5);
            fixed[i] = FC[i] * length * (rand.nextDouble()+.5);
            variable[i] = VC[i] * (rand.nextDouble()+.5);
        }
        return new MultiEdge(start, end, capacities, fixed ,variable);
    }

    private MultiEdge generateVirtualEdge (int start, int end) {
        Random rand = new Random();
        double [] capacity = new double [] {rand.nextInt( 2000 - 500)+ 500};
        double [] fixed_cost = new double [] {rand.nextInt(1500 - 500) + 500};
        double [] variable_cost = new double [] {rand.nextInt(50 - 10) + 10};

        return new MultiEdge(start, end, capacity, fixed_cost, variable_cost);
    }

    // writes edge array to a csv file
    private void writeEdgesToCSV (Edge [] edges) {
        try{
            File edge_file = new File("C:\\Users\\book_\\Documents\\Summer2019\\graphVisualizer\\edge_file_tester.csv");
            boolean fvar = edge_file.createNewFile();
            if (!fvar) {
                edge_file.delete();
                fvar = edge_file.createNewFile();
            }

            FileWriter csv_edge_writer = new FileWriter("C:\\Users\\book_\\Documents\\Summer2019\\graphVisualizer\\edge_file_tester.csv");

            // header info
            csv_edge_writer.append("id");
            csv_edge_writer.append(",");
            csv_edge_writer.append("start");
            csv_edge_writer.append(",");
            csv_edge_writer.append("end");
            csv_edge_writer.append(",");
            csv_edge_writer.append("flow");
            csv_edge_writer.append("\n");

            for (int i = 0; i < edges.length; i ++) {
                csv_edge_writer.append(String.valueOf(i));
                csv_edge_writer.append(",");
                csv_edge_writer.append(String.valueOf(edges[i].getStart()));
                csv_edge_writer.append(",");
                csv_edge_writer.append(String.valueOf(edges[i].getEnd()));
                csv_edge_writer.append(",");
                csv_edge_writer.append(String.valueOf(edges[i].getFlow()));
                csv_edge_writer.append("\n");
            }

            csv_edge_writer.flush();
            csv_edge_writer.close();

        } catch (IOException e) {
            System.out.println("Exception Occurred:");
            e.printStackTrace();
        }

    }
    
    enum NodeType {
        SOURCE, SINK, JUNCTION, SUPER_SOURCE, SUPER_SINK;

        @Override
        public String toString() {
            switch (this) {
                case SOURCE:
                    return "source";
                case SINK:
                    return "sink";
                case JUNCTION:
                    return "junction";
                case SUPER_SOURCE:
                    return "super source";
                case SUPER_SINK:
                    return "super sink";
                default:
                    return "error: no type";
            }
        }

    }

    public class Node {

        private double x;
        private double y;
        private int id;
        private NodeType node_type;

        // basic constructor
        public Node(double x, double y, int id, NodeType node_type) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.node_type = node_type;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getId() {
            return id;
        }

        public NodeType getNode_type() {
            return node_type;
        }

        @Override
        public String toString() {
            return "Node{"
                    + "x=" + x
                    + ", y=" + y
                    + ", id=" + id
                    + ", node_type=" + node_type
                    + '}';
        }
    }
}
