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
public class IndexCostTuple implements Comparable<IndexCostTuple> {

    int index;
    double cost;

    public IndexCostTuple(int index, double cost) {
        this.index = index;
        this.cost = cost;
    }

    public int getIndex() {
        return index;
    }

    public double getCost() {
        return cost;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public int compareTo(IndexCostTuple other) {
        if (this.cost < other.getCost()) {
            return -1;
        } else if (this.cost == other.getCost()) {
            return 0;
        }
        return 1;
    }

    // Tuples are equal if they share the same index
    // referenced Nicolai Parlgo's SitePont article when writing this method
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        }
        return (((IndexCostTuple) o).getIndex() == this.getIndex());
    }

}
