package mycontroller;

import controller.CarController;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		private HashMap<Coordinate,MapTile> map = getMap();
		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;
		
		public MyAutoController(Car car) {
			super(car);
		}
		
		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
//			Coordinate posit = new Coordinate(getPosition());
//			ArrayList<CarState> successors = this.getSuccessors(new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed()));
			// checkStateChange();
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}
			if (isFollowingWall) {
				// If wall no longer on left, turn left
				if(!checkFollowingWall(getOrientation(), currentView)) {
					turnLeft();
				} else {
					// If wall on left and wall straight ahead, turn right
					if(checkWallAhead(getOrientation(), currentView)) {
						turnRight();
					}
				}
			} else {
				// Start wall-following (with wall on left) as soon as we see a wall straight ahead
				if(checkWallAhead(getOrientation(),currentView)) {
					turnRight();
					isFollowingWall = true;
				}
			}
		}

		/**
		 * Check if you have a wall in front of you!
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
			switch(orientation){
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
			}
		}
		
		/**
		 * Check if the wall is on your left hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			
			switch(orientation){
			case EAST:
				return checkNorth(currentView);
			case NORTH:
				return checkWest(currentView);
			case SOUTH:
				return checkEast(currentView);
			case WEST:
				return checkSouth(currentView);
			default:
				return false;
			}	
		}
		
		/**
		 * Method below just iterates through the list and check in the correct coordinates.
		 * i.e. Given your current position is 10,10
		 * checkEast will check up to wallSensitivity amount of tiles to the right.
		 * checkWest will check up to wallSensitivity amount of tiles to the left.
		 * checkNorth will check up to wallSensitivity amount of tiles to the top.
		 * checkSouth will check up to wallSensitivity amount of tiles below.
		 */
		public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
			// Check tiles to my right
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to my left
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to towards the top
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles towards the bottom
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
	private Coordinate move(Coordinate posit, WorldSpatial.Direction direct){
		switch (direct){
			case NORTH:
				return new Coordinate(posit.x,posit.y+1);
			case SOUTH:
				return new Coordinate(posit.x,posit.y-1);
			case EAST:
				return new Coordinate(posit.x+1,posit.y);
			case WEST:
				return new Coordinate(posit.x-1,posit.y);
		}
		return null;
	}

	private ArrayList<CarState> getSuccessors(CarState carState){
		ArrayList<CarState> successors = new ArrayList<CarState>();
		WorldSpatial.Direction direct = carState.getDirec();
		float speed = carState.getVelocity();
		Coordinate posit = carState.getCoord();

		switch ((int) speed){
			case 1:
				successors.add(new CarState(move(posit,WorldSpatial.changeDirection(
						direct, WorldSpatial.RelativeDirection.LEFT)),
						WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.LEFT),1));
				successors.add(new CarState(move(posit,WorldSpatial.changeDirection(
						direct, WorldSpatial.RelativeDirection.RIGHT)),
						WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.RIGHT),1));
				successors.add(new CarState(move(posit,direct), direct,1));
				break;
			case 0:
				successors.add(new CarState(move(posit, WorldSpatial.reverseDirection(direct))
						, WorldSpatial.reverseDirection(direct),-1));
				successors.add(new CarState(move(posit,direct), direct,1));
				break;
			case -1:
				successors.add(new CarState(move(posit,WorldSpatial.changeDirection(
						direct, WorldSpatial.RelativeDirection.LEFT)),
						WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.LEFT),-1));
				successors.add(new CarState(move(posit,WorldSpatial.changeDirection(
						direct, WorldSpatial.RelativeDirection.RIGHT)),
						WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.RIGHT),-1));
				successors.add(new CarState(move(posit, WorldSpatial.reverseDirection(direct))
						, WorldSpatial.reverseDirection(direct),-1));
				break;
		}
		Iterator<CarState> iter = successors.iterator();
		while(iter.hasNext()){
			CarState s = iter.next();
			if(map.get(s.getCoord()).isType(MapTile.Type.WALL)){
				iter.remove();
			}
		}
		return successors;
	}
	}
