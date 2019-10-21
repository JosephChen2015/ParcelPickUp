package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParcelStrategy implements Strategy {

    private ArrayList<Coordinate> parcels;

    public ParcelStrategy(HashMap<Coordinate, MapTile> parcels) {
        this.parcels = new ArrayList<>();
        for (Map.Entry<Coordinate, MapTile> entry: parcels.entrySet())
        {
            this.parcels.add(entry.getKey());
        }
    }


    public Coordinate getGoal(Coordinate myPosit) {
        int max = 0;
        Coordinate parcel = myPosit;
        for (Coordinate temp: parcels)
        {
            int tempDis =
                    Math.abs(temp.x - myPosit.x) + Math.abs(temp.y - myPosit.y);
            if( tempDis > max)
            {
                max = tempDis;
                parcel = temp;
            }
        }
        return parcel;
    }
}
