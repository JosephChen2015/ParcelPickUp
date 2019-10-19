package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import java.util.HashMap;

public class ExploreStrategy {

    private HashMap<Coordinate, MapTile> parcels = new HashMap<Coordinate, MapTile>();
    private HashMap<Coordinate, Boolean> explore = new HashMap<Coordinate, Boolean>();

    public  ExploreStrategy(HashMap<Coordinate, MapTile> map){
        for (Coordinate coord : map.keySet()){
            if (map.get(coord).isType(MapTile.Type.ROAD)){
                this.explore.put(coord,false);
            }
        }
    }

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

    public void remove(Coordinate coord){
        this.explore.remove(coord);
    }

    public Coordinate goal(Coordinate posit){
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
    public void pickUp(Coordinate position){
        if (this.parcels.containsKey(position)){
            parcels.remove(position);
        }
    }
}
