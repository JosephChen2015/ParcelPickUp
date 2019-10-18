package mycontroller;

import controller.CarController;
import world.Car;

import java.util.*;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController {
    // Car Speed to move at
    private final int CAR_MAX_SPEED = 1;
    // How many minimum units the wall is away from the player.
    private int wallSensitivity = 1;
    private HashMap<Coordinate, MapTile> map = getMap();
    private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
    private ExploreStrategy exploreStrategy;
    private ParcelStrategy parcelStrategy;
    private DeliverStrategy deliverStrategy;

    public MyAutoController(Car car) {
        super(car);
        this.exploreStrategy = new ExploreStrategy(getMap());
        for (Coordinate coord : map.keySet()){
            if (Astar(new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed()), coord) == null){
                exploreStrategy.remove(coord);
            }
        }
    }

    public static int Heurisitic(Coordinate pos1, Coordinate pos2) {
        return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
    }

    // Coordinate initialGuess;
    // boolean notSouth = true;
    @Override
    public void update() {
        // Gets what the car can see
        HashMap<Coordinate, MapTile> currentView = getView();
        HashMap<Coordinate, Boolean> explored = exploreStrategy.update(currentView);
        HashMap<Coordinate, MapTile> parcels = exploreStrategy.getParcels();
        parcelStrategy = new ParcelStrategy(exploreStrategy.getParcels(), new Coordinate(getPosition()));
        deliverStrategy = new DeliverStrategy(getMap(), new Coordinate(getPosition()));
        Coordinate goal;
        int eaten = 0;
        Coordinate myPos = new Coordinate(getPosition());
        if(parcels.containsKey(myPos))
        {
            parcels.remove(myPos);
        }

        if(numParcels() - numParcelsFound()>0)
        {
            if(parcels.size() != 0)
            {
                goal = parcelStrategy.getGoal();
            }
            else {
                goal = exploreStrategy.goal(new Coordinate(getPosition()));
            }
        }
        else
        {
            goal = deliverStrategy.getGoal();
        }
        CarState car = new CarState(new Coordinate(getPosition()), getOrientation(), getSpeed());
        CarState a = Astar(car, goal);
        if (a != null) {
            WorldSpatial.Direction direction = a.getDirec();
            Coordinate coord = a.getCoord();
            float speed = a.getVelocity();

            if (getSpeed() < speed) {
                applyForwardAcceleration();
            } else if (getSpeed() > speed) {
                applyReverseAcceleration();
            }

            if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.LEFT))) {
                turnLeft();
            } else if (direction.equals(WorldSpatial.changeDirection(getOrientation(), WorldSpatial.RelativeDirection.RIGHT))) {
                turnRight();
            }
        }

    }

    private Coordinate move(Coordinate posit, WorldSpatial.Direction direct) {
        switch (direct) {
            case NORTH:
                return new Coordinate(posit.x, posit.y + 1);
            case SOUTH:
                return new Coordinate(posit.x, posit.y - 1);
            case EAST:
                return new Coordinate(posit.x + 1, posit.y);
            case WEST:
                return new Coordinate(posit.x - 1, posit.y);
        }
        return null;
    }

    private ArrayList<CarState> getSuccessors(CarState carState) {
        ArrayList<CarState> successors = new ArrayList<CarState>();
        WorldSpatial.Direction direct = carState.getDirec();
        float speed = carState.getVelocity();
        Coordinate posit = carState.getCoord();

        switch ((int) speed) {
            case 1:
                successors.add(new CarState(move(posit, WorldSpatial.changeDirection(
                        direct, WorldSpatial.RelativeDirection.LEFT)),
                        WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.LEFT), 1));
                successors.add(new CarState(move(posit, WorldSpatial.changeDirection(
                        direct, WorldSpatial.RelativeDirection.RIGHT)),
                        WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.RIGHT), 1));
                successors.add(new CarState(move(posit, direct), direct, 1));
                successors.add(new CarState(posit, direct, 0));
                break;
            case 0:
                successors.add(new CarState(move(posit, WorldSpatial.reverseDirection(direct))
                        , direct, -1));
                successors.add(new CarState(move(posit, direct), direct, 1));
                break;
            case -1:
                successors.add(new CarState(move(posit, WorldSpatial.changeDirection(
                        direct, WorldSpatial.RelativeDirection.RIGHT)),
                        WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.LEFT), -1));
                successors.add(new CarState(move(posit, WorldSpatial.changeDirection(
                        direct, WorldSpatial.RelativeDirection.LEFT)),
                        WorldSpatial.changeDirection(direct, WorldSpatial.RelativeDirection.RIGHT), -1));
                successors.add(new CarState(move(posit, WorldSpatial.reverseDirection(direct))
                        ,direct, -1));
                successors.add(new CarState(posit, direct, 0));
                break;
        }
        Iterator<CarState> iter = successors.iterator();
        while (iter.hasNext()) {
            CarState s = iter.next();
            if (map.get(s.getCoord()).isType(MapTile.Type.WALL)) {
                iter.remove();
            }
        }
        return successors;
    }

    public CarState Astar(CarState myState, Coordinate goal) {
        PriorityQueue<NodeExpand> openList = new PriorityQueue<NodeExpand>(1000, (Comparator) (o1, o2) -> {
            NodeExpand n1 = (NodeExpand) o1;
            NodeExpand n2 = (NodeExpand) o2;
            if (n1.getCost() < n2.getCost()) {
                return -1;
            } else if (n1.getCost() > n2.getCost()) {
                return 1;
            } else {
                return 0;
            }
        });
        ArrayList<Coordinate> closeList = new ArrayList<Coordinate>();

        ArrayList<CarState> trace = new ArrayList<CarState>();
        NodeExpand node = new NodeExpand(myState, trace, 0);
        openList.add(node);

        ArrayList<CarState> successors = new ArrayList<CarState>();

        while (!openList.isEmpty()) {
            NodeExpand currentNode = openList.poll();
            CarState currentState = currentNode.getState();
            trace = currentNode.getTrace();
            if (currentNode.getState().getCoord().equals(goal)) {
                if (trace.size() == 0) {
                    return null;
                }
                return trace.get(0);
            }

            if (!closeList.contains(currentState.getCoord())) {
                successors.clear();
                closeList.add(currentState.getCoord());
                successors = getSuccessors(currentState);
                for (CarState temp : successors) {
                    Coordinate nextPos = temp.getCoord();
                    WorldSpatial.Direction nextDirec = temp.getDirec();
                    float nextVelocity = temp.getVelocity();
                    int cost = currentNode.getCost() + 1;
                    CarState nextState = new CarState(nextPos, nextDirec, nextVelocity);
                    int Priority = cost + Heurisitic(currentState.getCoord(), goal);
                    if (!closeList.contains(nextState.getCoord())) {
                        ArrayList<CarState> nextTrace = (ArrayList<CarState>) currentNode.getTrace().clone();
                        nextTrace.add(nextState);
                        NodeExpand nextNode = new NodeExpand(nextState, nextTrace, Priority);
                        openList.add(nextNode);
                    }
                }
            }

        }
        return null;
    }

}
