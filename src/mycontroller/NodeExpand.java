package mycontroller;

import java.util.ArrayList;

public class NodeExpand {

	private CarState state;
	private ArrayList<CarState> trace;
	private int cost;
	
	@SuppressWarnings("unchecked")
	public NodeExpand(CarState state, ArrayList<CarState> trace, int cost)
	{
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

	public void setCost(int cost) {
		this.cost = cost;
	}
}
