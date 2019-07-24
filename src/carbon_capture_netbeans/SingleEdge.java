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
public class SingleEdge implements Edge {

    private double capacity;
    private double fixed_cost;
    private double variable_cost;
    private int start;
    private int end;
    private boolean isOpen;
    private double flow;

    public SingleEdge(int start, int end, double capacity, double fixed_cost, double variable_cost) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Edges must have non-negative capacity.");
        }
        if (fixed_cost < 0) {
            throw new IllegalArgumentException("Edges must have non-negative fixed_cost.");
        }
        if (variable_cost < 0) {
            throw new IllegalArgumentException("Edges must have non-negative variable_cost.");
        }
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Edges must have non-negative start and end indices.");
        }

        this.capacity = capacity;
        this.fixed_cost = fixed_cost;
        this.variable_cost = variable_cost;
        this.start = start;
        this.end = end;
        this.isOpen = false;
        this.flow = 0;
    }
    
        public SingleEdge(int start, int end, double capacity, double fixed_cost, double variable_cost, boolean isOpen, double flow) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Edges must have non-negative capacity.");
        }
        if (fixed_cost < 0) {
            throw new IllegalArgumentException("Edges must have non-negative fixed_cost.");
        }
        if (variable_cost < 0) {
            throw new IllegalArgumentException("Edges must have non-negative variable_cost.");
        }
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Edges must have non-negative start and end indices.");
        }

        this.capacity = capacity;
        this.fixed_cost = fixed_cost;
        this.variable_cost = variable_cost;
        this.start = start;
        this.end = end;
        this.isOpen = isOpen;
        this.flow = flow;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getFixed_cost() {
        return fixed_cost;
    }

    public double getVariable_cost() {
        return variable_cost;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public double getFlow() {
        return flow;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean setFlow(double flow) {
        if (flow < 0) {
            return false;
        }
        if (capacity - flow < 0) {
            return false;
        }
        if (flow == 0) {
            isOpen = false;
        } else {
            isOpen = true;
        }
        this.flow = flow;
        return true;
    }

    public boolean augmentFlow(double delta) {
        if (flow + delta > capacity || flow + delta < 0){
            return false;
        }
        flow += delta;
        if (flow > 0) {
            isOpen = true;
        } else {
            isOpen = false;
        }
        return true;
    }

    public double getResidualCapacity() {
        return capacity - flow;
    }

    public double getCurrentCost() {
        if (!isOpen) {
            assert (flow == 0);
            return 0;
        }
        return fixed_cost + flow * variable_cost;
    }

    public double getCost(double additional_flow) {
        if (capacity - flow - additional_flow < 0) {
            return Double.MAX_VALUE;
        } else if (!isOpen) {
            return fixed_cost + additional_flow * variable_cost;
        }
        return additional_flow * variable_cost;
    }

    public boolean isValid() {
        if ((!isOpen && flow > 0) || flow > capacity) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Edge %d-%d; Flow: %f, Cost: %f",
                start, end, getFlow(), getCurrentCost());
    }

    @Override
    public double getResidualCapacity(int level) {
        return getResidualCapacity();
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public double getFixedCostToIncreaseFlow() {
        if (!isOpen){
            return fixed_cost;
        } else if (flow == capacity){
            return Double.MAX_VALUE;
        }
        return 0;
    }

}
