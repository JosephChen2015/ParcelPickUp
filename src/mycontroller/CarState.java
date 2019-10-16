package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial.Direction;

public class CarState {
	
	private Coordinate coord;
	private Direction direc;
	private int velocity;
	
	public Coordinate getCoord() {
		return coord;
	}

	public Direction getDirec() {
		return direc;
	}

	public int getVelocity() {
		return velocity;
	}

	public CarState(Coordinate coord, Direction direc, int velocity) {
		this.coord = coord;
		this.direc = direc;
		this.velocity = velocity;
	}
	
	
}
