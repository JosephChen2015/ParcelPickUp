package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial.Direction;

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
	
	
}
