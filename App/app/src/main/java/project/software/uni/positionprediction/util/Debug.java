package project.software.uni.positionprediction.util;

import android.util.Log;

import java.util.List;

import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.LocationWithValue;
import project.software.uni.positionprediction.datatypes.Trajectory;


/**
 * Contains methods to debug the code
 */
public class Debug {

    // Constructor
    public Debug() {

    }


    // Methods



    public void print(List<Number> list, String name) {
        int s  = list.size();
        System.out.print("" +  name + ":  (");
        for (int i = 0; i < s - 1; i++) {
            System.out.print("" + list.get(i) + " -> ");
        }
        System.out.print("" + list.get(s-1) + ")\n");
    }




    /**
     * TJ: Tip: Java has an Arrays.toString() Method.
     *
     * Print array
     * Method for printing an array
     * <p>
     * Example:
     * print(b, "Sorted Array") -> "Sorted Array: [1,2,3] \n"
     *
     * @param array
     */
    public void print(double array[], String name) {
        int s = array.length;
        System.out.print("" + name + ":  [");
        for (int i = 0; i < s - 1; i++) {
            System.out.print("" + array[i] + " ,");
        }
        System.out.print("" + array[s - 1] + "]\n");
        //for benny

    }


    /**
     * Print numbers with text
     *
     * @param a
     * @param name
     */
    public void print(Number a, String name) {
        System.out.println(name + ": " + a);
    }


    /**
     *
     * @param data
     */
    public void printDates(Trajectory data) {

        for (int i = 0; i<data.size(); i++){
            Log.e("Date", ((LocationWithValue) data.getLocation(i)).getValue().toString() );
        }
    }


    public void log(String s, Location loc){
        Log.d(s, ""+loc.getLat() + ", " + loc.getLon() + ", " + loc.getAlt() + ", " + loc.hasAltitude());
    }




}
