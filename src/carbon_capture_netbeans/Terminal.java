/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carbon_capture_netbeans;

/**
 * Represents a terminal node (source/sink) in a CCS instance
 *
 * @author sauerberg
 */
public class Terminal {

    private int index;
    private boolean isSource;

    private double capacity;
    private double fixed_cost;
    private double variable_cost;

    private boolean isOpen;
    private double flow;

    public Terminal(int index, boolean isSource, double capacity, double fixed_cost, double variable_cost) {
        if (index < 0) {
            throw new IllegalArgumentException("Terminals must have a positive index.");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Terminals must have non-negative capacity.");
        }
        if (fixed_cost < 0) {
            throw new IllegalArgumentException("Terminals must have non-negative fixed_cost.");
        }
        if (variable_cost < 0) {
            throw new IllegalArgumentException("Termaminals must have non-negative variable_cost.");
        }
        this.index = index;
        this.isSource = isSource;
        this.capacity = capacity;
        this.fixed_cost = fixed_cost;
        this.variable_cost = variable_cost;
        this.isOpen = false;
        this.flow = 0;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSource() {
        return isSource;
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

    public boolean isOpen() {
        return isOpen;
    }

    public double getFlow() {
        return flow;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void setFlow(double flow) {
        if (flow < 0) {
            throw new IllegalArgumentException("Negative Flow is not allowed.");
        }
        if (capacity - flow < 0) {
            throw new IllegalArgumentException("Not enough capacity");
        }
        if (flow > 0) {
            isOpen = true;
        } else {
            isOpen = false;
        }
        this.flow = flow;
    }

    public void augmentFlow(double delta) {
        flow += delta;
        if (flow > capacity || flow < 0) {
            throw new IllegalArgumentException("Augmentation resulted in invalid flow");
        }
        if (flow > 0){
            isOpen = true;
        } else {
            isOpen = false;
        }
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
            return -1;
        } else if (!isOpen) {
            return fixed_cost + additional_flow * variable_cost;
        }
        return additional_flow * variable_cost;
    }

    public double getResidualCapacity() {
        return capacity - flow;
    }

    public boolean isValid() {
        if ((!isOpen && flow > 0) || flow > capacity) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %d; Flow: %f, Cost: %f",
                isSource() ? "Source" : "Sink", getIndex(), getFlow(), getCurrentCost());
    }

}
