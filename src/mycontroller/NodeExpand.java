package mycontroller;

import java.util.ArrayList;

/**
 * This class defines each node expanded of searching routes.
 * It includes the state of car, an array list of nodes explored and the cost accumulated.
 */
public class NodeExpand {

    private CarState state;
    private ArrayList<CarState> trace;
    private int cost;

    @SuppressWarnings("unchecked")
    public NodeExpand(CarState state, ArrayList<CarState> trace, int cost) {
        this.state = state;
        this.trace = (ArrayList<CarState>) trace.clone();
        this.cost = cost;
    }

    public CarState getState() {
        return state;
    }

    public ArrayList<CarState> getTrace() {
        return trace;
    }

    public int getCost() {
        return cost;
    }

}