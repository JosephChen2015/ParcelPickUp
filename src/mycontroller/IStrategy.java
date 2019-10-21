package mycontroller;

import utilities.Coordinate;

public interface IStrategy {

    public Coordinate getGoal(Coordinate myPosit);
}
