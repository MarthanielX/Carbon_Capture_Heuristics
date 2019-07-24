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
public interface Edge {
    
    /**
     * @return The index of the start node for the edge
     */
    public int getStart();
    
    /**
     * @return The index of the ending node for the edge
     */
    public int getEnd();
    
    /**
     * @return True if the edge has been constructed, False otherwise
     */
    public boolean isOpen();
    
    /**
     * @return The cost to route the current amount of flow on the edge
     */
    public double getCurrentCost();
    
    /**
     * @return The amount of flow currently being routed along the edge
     */
    public double getFlow();
    
    /**
     * @param amount the total amount of flow to be routed along the edge
     * @return False if the method would have resulted in errors and was aborted; True o.w.
     */
    public boolean setFlow(double amount);
    
    /**
     * @param amount the amount of additional flow to be routed along the edge
     * @return False if the method would have resulted in errors and was aborted; True o.w.
     */
    public boolean augmentFlow(double amount);
    
    /**
     * @param additional_flow
     * @return The cost to route the additional flow along the edge
     */
    public double getCost(double additional_flow);
    
    /**
     * @return True if the flow obeys capacity constraints; False o.w. 
     */
    public boolean isValid();
    
    /**
     * @param level
     * @return the additional amount of flow that could be routed if the pipeline were changed to level
     */
    public double getResidualCapacity(int level);
    
        
    /**
     * @return the remaining capacity at the current upgrade level 
     */
    public double getResidualCapacity();
    
    /**
     * @return The current 'upgrade' level of the pipeline
     */
    public int getLevel();
    
    /**
     * @return the cost to upgrade the edge if it has no residual capacity, 0 o.w
     */
    public double getFixedCostToIncreaseFlow();
    
}
