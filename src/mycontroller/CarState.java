package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial.Direction;

/**
 * This class represents the states of cars including the position, direction and velocity of it.
 */
public class CarState {

    private Coordinate coord;
    private Direction direc;
    private float velocity;

    public Coordinate getCoord() {
        return coord;
    }

    public Direction getDirec() {
        return direc;
    }

    public float getVelocity() {
        return velocity;
    }

    public CarState(Coordinate coord, Direction direc, float velocity) {
        this.coord = coord;
        this.direc = direc;
        this.velocity = velocity;
    }

    @Override
    public boolean equals(Object obj) {
        CarState car = (CarState) obj;
        if (this.getCoord().x == car.getCoord().x &&
                this.getCoord().y == car.getCoord().y &&
                this.getVelocity() == car.getVelocity() &&
                this.getDirec() == car.getDirec()) {
            return true;
        } else {
            return false;
        }
    }
}
