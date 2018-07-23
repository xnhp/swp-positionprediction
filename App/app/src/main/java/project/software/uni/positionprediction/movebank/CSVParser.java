package project.software.uni.positionprediction.movebank;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by simon on 23.05.18.
 */

public class CSVParser {

    /**
     * This Method splits a given CSV File into columns and rows
     * @param input CSV File as String
     * @return
     */

    public static String[][] parseRowsColumns(String input){

        ReadState state = ReadState.STATE_READ;

        StringBuilder stringBuilder = new StringBuilder();

        List<List<String>> resultList = new LinkedList<List<String>>();
        List<String> lineList = new LinkedList<String>();

        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            switch (state)
            {

                case STATE_READ:
                    switch(c) {
                        case ',':
                            // ',' indicates a new column
                            lineList.add(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            break;

                        case '\n':
                            // '\n' indicates a new row if it is outside a String
                            lineList.add(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            resultList.add(lineList);
                            lineList = new LinkedList<String>();
                            break;

                        case '\r':
                            // ignore '\r'
                            break;

                        case '"':
                            // A String starts
                            state = ReadState.STATE_STRING;
                            break;

                        default:
                            // nothing special. Add char to current column
                            stringBuilder.append(c);
                            break;
                    }
                    break;

                case STATE_STRING:
                    if(c == '"'){
                        // End of a String
                        state = ReadState.STATE_READ;
                    }else{
                        stringBuilder.append(c);
                    }
                    break;
            }
        }

        // add last line to the result list
        lineList.add(stringBuilder.toString());
        if(lineList.size() > 1){
            resultList.add(lineList);
        }

        // convert lists to array
        String result[][] = new String[resultList.size()][];

        int row_index = 0;
        while(resultList.size() > 0){

            List<String> line = resultList.remove(0);
            int col_index = 0;

            result[row_index] = new String[line.size()];

            while(line.size() > 0){
                result[row_index][col_index] = line.remove(0);
                col_index++;
            }

            row_index++;
        }

        return result;

    }

    /**
     * This Method parses a CSV-File to a 2 dimensional array of Strings.
     * The first dimension are the columns, the second the rows
     * @param input the CSV-File as String
     * @return 2 dimensional Array of Strings
     */
    public static String[][] parseColumnsRows(String input){

        // get Rows and Columns
        String rowCol[][] = parseRowsColumns(input);

        String result[][] = new String[rowCol[0].length][];

        // exchange dimensions of the array
        for(int i = 0; i < rowCol[0].length; i++){

            result[i] = new String[rowCol.length];

            for(int j = 0; j < rowCol.length; j++){
                result[i][j] = rowCol[j][i];
            }

        }

        return result;

    }

    /**
     * THis Method takes a 2 dimensional Array where the first dimension represents the columns
     * and the second dimension represents the rows. It returns the Array with the selected columns
     * only
     * @param input 2 dimensional Array
     * @param columns an Array of columns
     * @return 2 dimensional Array with the selected columns only
     */
    public static String[][] getColumns(String[][] input, String[] columns){

        // put all columns into a HashMap
        HashMap<String, String[]> columnMap = new HashMap<String, String[]>();

        for(int i = 0; i < input.length; i++){
            columnMap.put(input[i][0], input[i]);
        }

        String result[][] = new String[columns.length][];

        // get selected columns from HashMap
        for(int i = 0; i < columns.length; i++){
            result[i] = columnMap.get(columns[i]);
            if(result[i] == null){
                Log.e("missing Column", columns[i]);
            }
        }

        return result;

    }

}

enum ReadState{
    STATE_READ,
    STATE_STRING
}
