package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.*;

/**
 * This class defines the algorithms for searching routes to a specific position.
 * A* is used for searching available approaches. And the manhattan distance is regarded as our heuristic.
 */
class SearchRoute {

    private HashMap<Coordinate, MapTile> map;

    SearchRoute(HashMap<Coordinate, MapTile> map) {
        this.map = map;
    }

    private int Heurisitic(Coordinate pos1, Coordinate pos2) {
        return Math.abs(pos1.x - pos2.x) + Math.abs(pos1.y - pos2.y);
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

    /**
     * This function finds all possible successors of a given car state.
     * @param carState
     * @return an array list of carStates, which are the successors of input.
     */
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
                        , direct, -1));
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

    /**
     * This function implements the A* algorithm using CarState representing each node explored.
     * It is used for searching the routes to a specific goal.
     * @param myState the current CarState
     * @param goal the position of our current target
     * @return The CarState can be reached in the next step.
     */
    private CarState aStarPlus(CarState myState, Coordinate goal) {

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
        });// defines the comparable NodeExpand Priority Queue

        ArrayList<CarState> closeList = new ArrayList<CarState>();
        ArrayList<CarState> trace = new ArrayList<CarState>();
        NodeExpand node = new NodeExpand(myState, trace, 0);
        openList.add(node);
        ArrayList<CarState> successors = new ArrayList<CarState>();
        CarState currentState;
        NodeExpand currentNode;

        while (!openList.isEmpty()) { //keep searching all the unexplored nedes
            currentNode = openList.poll();
            currentState = currentNode.getState();
            trace = currentNode.getTrace();
            if (currentNode.getState().getCoord().equals(goal)) {
                if (trace.size() == 0) {
                    return null;
                }
                return trace.get(0);
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

    public CarState routeSearch(CarState myState, Coordinate goal){
        CarState a = aStar(myState,goal);
        if (a == null){
            return aStarPlus(myState,goal);
        }
        else{
            return a;
        }

    }

    /**
     * This function implements the A* algorithm using Coordinate representing each node explored.
     * It is used for scanning maps before the game starts to find all reachable nodes.
     * @param myState the current CarState
     * @param goal the position of our current target
     * @return The CarState can be reached in the next step.
     */
    private CarState aStar(CarState myState, Coordinate goal) {

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

    public boolean isReachable(String posit, WorldSpatial.Direction orientation, Coordinate coord)
    {
        return aStar(new CarState(new Coordinate(posit), orientation, 0), coord) != null;
        //define the reachable node with there are at least one route to get to it.
    }
}




