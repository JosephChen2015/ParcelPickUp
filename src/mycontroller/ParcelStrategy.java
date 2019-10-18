package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

public class ParcelStrategy implements Strategy {
    private Coordinate parcel = null;

    public ParcelStrategy(HashMap<Coordinate, MapTile> parcels, Coordinate myPosit)
    {
        int max = 0;
        Coordinate parcel = myPosit;
        for (Coordinate temp: parcels.keySet())
        {
            int tempDis = MyAutoController.Heurisitic(temp, myPosit);
            if( tempDis > max)
            {
                max = tempDis;
                parcel = temp;
            }
        }
        this.parcel = parcel;
    }

    public Coordinate getGoal()
    {
        return parcel;
    }
}
