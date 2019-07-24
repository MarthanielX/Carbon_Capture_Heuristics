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
public class Reverse_MultiEdge implements Edge {
    MultiEdge edge;
    public Reverse_MultiEdge(MultiEdge edge){
        this.edge = edge;
    }

    public int getStart() {
        return edge.getEnd();
    }

    public int getEnd() {
        return edge.getStart();
    }

    public boolean isOpen() {
        return edge.isOpen();
    }

    public double getCurrentCost() {
        return edge.getCurrentCost();
    }

    public double getFlow() {
        return -edge.getFlow();
    }

    public boolean setFlow(double amount) {
        return edge.setFlow(-amount);
    }

    public boolean augmentFlow(double amount) {
        return edge.augmentFlow(-amount);
    }

    public double getCost(double additional_flow) {
        return edge.getCost(-additional_flow);
    }

    public boolean isValid() {
        return edge.isValid();
    }

    
}
