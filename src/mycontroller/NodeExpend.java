package mycontroller;

import java.util.ArrayList;

public class NodeExpend {
	private CarState state;
	private ArrayList<CarState> trace;
	private int cost;
	
	@SuppressWarnings("unchecked")
	public NodeExpend(CarState state, ArrayList<CarState> trace)
	{
		this.state = state;
		this.trace = (ArrayList<CarState>) trace.clone();
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
