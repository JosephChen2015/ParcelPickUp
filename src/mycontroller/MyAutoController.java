package mycontroller;

import controller.CarController;
import world.Car;

import java.util.*;

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
        Coordinate goal = new Coordinate(1,17);
        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed());
        String a = Astar(car, goal);
        System.out.println(1);
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
    public int Heurisitic(Coordinate pos1, Coordinate pos2)
    {
        return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
    }

    public String Astar(CarState myState, Coordinate goal) {
        PriorityQueue<NodeExpand> openList = new PriorityQueue<NodeExpand>(1000, new Comparator() {
            public int compare(Object o1, Object o2) {
                NodeExpand n1 = (NodeExpand) o1;
                NodeExpand n2 = (NodeExpand) o2;
                if (n1.getCost() < n2.getCost()) {
                    return -1;
                } else if (n1.getCost() > n2.getCost()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        ArrayList<CarState> closeList = new ArrayList<CarState>();

        ArrayList<CarState> trace = new ArrayList<CarState>();
        NodeExpand node = new NodeExpand(myState, trace, 0);
        openList.add(node);

        ArrayList<CarState> successors = new ArrayList<CarState>();

        while (!openList.isEmpty()) {
            NodeExpand currentNode = openList.poll();
            CarState currentState = currentNode.getState();
            if (currentNode.getState().getCoord() == goal) {
                if (trace.size() == 0) {
                    return "STOP";
                }
                return trace.get(0).getDirec().toString();
            }

            if (!closeList.contains(currentState)) {
                successors.clear();
                closeList.add(currentState);
                successors = getSuccessors(currentState);
                for (CarState temp : successors) {
                    Coordinate nextPos = temp.getCoord();
                    WorldSpatial.Direction nextDirec = temp.getDirec();
                    float nextVelocity = temp.getVelocity();
                    int cost = currentNode.getCost() + 1;
                    CarState nextState = new CarState(nextPos, nextDirec, nextVelocity);
                    int Priority = cost + Heurisitic(currentState.getCoord(), goal);
                    if (!closeList.contains(nextState)) {
                        currentNode.getTrace().add(nextState);
                        ArrayList<CarState> nextTrace = (ArrayList<CarState>) currentNode.getTrace().clone();
                        NodeExpand nextNode = new NodeExpand(nextState, nextTrace, Priority);
                        openList.add(nextNode);
                    }
                }
            }

        }
        return "STOP";
    }

}
