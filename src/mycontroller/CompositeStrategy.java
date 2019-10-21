package mycontroller;

import utilities.Coordinate;

import java.util.ArrayList;

public class CompositeStrategy implements IStrategy{
    private ArrayList<IStrategy> strategies = new ArrayList<IStrategy>();

    @Override
    public Coordinate getGoal(Coordinate myPosit) {
        return null;
    }

    public void addStrategy(IStrategy strategy){
        this.strategies.add(strategy);
    }
}
