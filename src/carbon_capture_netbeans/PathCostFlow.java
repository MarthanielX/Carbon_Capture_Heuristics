/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

import java.util.ArrayList;

/**
 *
 * @author sauerberg
 */
public class PathCostFlow {

    ArrayList<Integer> path;
    double cost;
    double flow;

    public PathCostFlow(ArrayList<Integer> path, double cost, double flow) {
        this.path = path;
        this.cost = cost;
        this.flow = flow;
    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }

    public double getFlow() {
        return flow;
    }

    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setFlow(double flow) {
        this.flow = flow;
    }

    public PathCostFlow copy() {
        return new PathCostFlow(new ArrayList<Integer>(path), cost, flow);
    }
    
    public double getFlowOverCost(){
        if (cost == 0){
            return Double.MAX_VALUE;
        }
        return flow/cost;
    }
}
