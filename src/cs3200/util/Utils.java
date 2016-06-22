package cs3200.util;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Util class for the Database Front End
 */
public class Utils {

    /**
     * Parses the String s based on spaces
     * @param s The string to be parsed
     * @return The arraylist of strigns, parsed from S
     */
    public ArrayList<String> parseString(String s) {
        ArrayList<String> strings = new ArrayList<>();
        Scanner sc = new Scanner(s);
        while (sc.hasNext()) {
            strings.add(sc.next());
        }
        return strings;
    }

}
