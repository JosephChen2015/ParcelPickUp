package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import java.util.HashMap;

/**
 * This class defines the strategies applying on exploring the map.
 */
public class ExploreStrategy implements IStrategy{

    private HashMap<Coordinate, MapTile> parcels = new HashMap<Coordinate, MapTile>();
    private HashMap<Coordinate, Boolean> explore = new HashMap<Coordinate, Boolean>();

    public  ExploreStrategy(HashMap<Coordinate, MapTile> map){
        for (Coordinate coord : map.keySet()){
            if (map.get(coord).isType(MapTile.Type.ROAD)){
                this.explore.put(coord,false);
            }
        }
    }

    /**
     * For each time the car obtain a new view, this method is called to update the stored explored map.
     * @param observe a map of current view of car
     * @return th explored map so far
     */
    public HashMap<Coordinate, Boolean> update(HashMap<Coordinate, MapTile> observe){
        for (Coordinate coord : observe.keySet()){
            if (explore.containsKey(coord) && !explore.get(coord)){
                this.explore.remove(coord);
                this.explore.put(coord, true);
                if (observe.get(coord).isType(MapTile.Type.TRAP)){
                    if (((TrapTile)observe.get(coord)).getTrap().equals("parcel")){
                        this.parcels.put(coord,observe.get(coord));
                    }
                }
            }
        }
        return this.explore;
    }

    public void pickUp(Coordinate coord){
        parcels.remove(coord);
    }

    public void remove(Coordinate coord){
        this.explore.remove(coord);
    }

    /**
     * This function aims to find the nearest unexplored node as the goal of further exploration.
     * @param posit current position of our car
     * @return  the coordinate of the goal for next exploration
     */
    @Override
    public Coordinate getGoal(Coordinate posit){
        int minDis = 99999;
        Coordinate goal = new Coordinate(0,0);
        for (Coordinate coord : explore.keySet()){
            if (!explore.get(coord)){
                int dis =  Math.abs(posit.x - coord.x) + Math.abs(posit.y - coord.y);
                if (minDis>dis){
                    minDis = dis;
                    goal = coord;
                }
            }
        }
        return goal;
    }

    public HashMap<Coordinate, MapTile> getParcels() {
        return parcels;
    }
}
