package project.software.uni.positionprediction.classes;

/**
 * Contains methods to debug the code
 */
public class Debug {

    // Constructor
    public Debug() {

    }


    // Methods

    /**
     * PRINT ARRAY
     * Method for printing an array
     *
     * Example:
     * print(b, "Sorted Array") -> "Sorted Array: [1,2,3] \n"
     *
     * @param array
     */
    public void print(double array[], String name){
        int s = array.length;
        System.out.print("" + name + ":  [");
        for (int i = 0; i<s-1; i++){
            System.out.print("" + array[i] + " ,");
        }
        System.out.print("" + array[s-1] + "]\n");

    }


    // TODO 2D





}
