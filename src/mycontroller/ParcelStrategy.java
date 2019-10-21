package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParcelStrategy implements IStrategy {

    private ArrayList<Coordinate> parcels;

    public ParcelStrategy() {
    }

    public void update(HashMap<Coordinate, MapTile> parcels) {
        this.parcels = new ArrayList<>();
        for (Map.Entry<Coordinate, MapTile> entry: parcels.entrySet())
        {
            this.parcels.add(entry.getKey());
        }

    }

    @Override
    public Coordinate getGoal(Coordinate myPosit) {
        int min = 9999;
        Coordinate goal = null;
        for (Coordinate temp: parcels)
        {
            int tempDis =
                    Math.abs(temp.x - myPosit.x) + Math.abs(temp.y - myPosit.y);
            if( tempDis < min)
            {
                min = tempDis;
                goal = temp;
            }
        }
        return goal;
    }
}
