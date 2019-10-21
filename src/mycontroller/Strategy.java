package mycontroller;

import utilities.Coordinate;

public interface Strategy {

    public Coordinate getGoal(Coordinate myPosit);
}
