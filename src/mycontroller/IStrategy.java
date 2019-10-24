package mycontroller;

import utilities.Coordinate;

/**
 * public interface of strategies.
 */
public interface IStrategy {

    public Coordinate getGoal(Coordinate myPosit);
}
