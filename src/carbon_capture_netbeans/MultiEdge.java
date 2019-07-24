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
public class MultiEdge implements Edge {

    int start;
    int end;
    double[] capacities;
    double[] fixed_costs;
    double[] variable_costs;
    int level; // 0 means unopened
    double flow;

    public MultiEdge(int start, int end, double[] capacities, double[] fixed_costs, double[] variable_costs) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Edges must have non-negative start and end indices.");
        }
        if (capacities.length != fixed_costs.length || capacities.length != variable_costs.length) {
            throw new IllegalArgumentException("Capcity and cost arrays must have same length.");
        }
        for (int i = 0; i < capacities.length; i++) {
            if (capacities[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative capacity.");
            }
            if (fixed_costs[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative fixed_cost.");
            }
            if (variable_costs[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative variable_cost.");
            }
        }
        this.start = start;
        this.end = end;
        this.capacities = capacities;
        this.fixed_costs = fixed_costs;
        this.variable_costs = variable_costs;
        this.level = 0;
        this.flow = 0;

    }

    public MultiEdge(int start, int end, double[] capacities, double[] fixed_costs, double[] variable_costs, int level, double flow) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Edges must have non-negative start and end indices.");
        }
        if (capacities.length != fixed_costs.length || capacities.length != variable_costs.length) {
            throw new IllegalArgumentException("Capcity and cost arrays must have same length.");
        }
        for (int i = 0; i < capacities.length; i++) {
            if (capacities[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative capacity.");
            }
            if (fixed_costs[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative fixed_cost.");
            }
            if (variable_costs[i] < 0) {
                throw new IllegalArgumentException("Edges must have non-negative variable_cost.");
            }
        }
        this.start = start;
        this.end = end;
        this.capacities = capacities;
        this.fixed_costs = fixed_costs;
        this.variable_costs = variable_costs;
        this.level = level;
        this.flow = flow;

        if (!this.isValid()) {
            throw new IllegalArgumentException("New edge has larger flow than admitted by level and capcity.");
        }
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public boolean isOpen() {
        return level == 0;
    }

    @Override
    public double getFlow() {
        return flow;
    }

    @Override
    public double getCurrentCost() {
        return fixed_costs[level] + variable_costs[level] * Math.abs(flow);
    }

    @Override
    public double getCost(double additional_flow) {
        return 0;
    }

    @Override
    public boolean setFlow(double amount) {
        if (findMinLevel(amount) == -1) {
            return false;
        }
        level = findMinLevel(amount);
        this.flow = amount;
        return true;
    }

    @Override
    public boolean augmentFlow(double amount) {
        if (findMinLevel(flow + amount) == -1) {
            return false;
        }
        level = findMinLevel(amount);
        flow += amount;
        return true;
    }

    @Override
    public boolean isValid() {
        return capacities[level] >= Math.abs(flow);
    }

    private int findMinLevel(double flow) {
        for (int i = 0; i < capacities.length; i++) {
            if (capacities[i] >= Math.abs(flow)) {
                return i;
            }
        }
        return -1;
    }

}
