
package org.itemscript.core.util;

import java.util.List;

/**
 * Various static utility methods.
 *  
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public final class ItemscriptUtil {
    /**
     * HTML-encode a string. This simple method only replaces the five characters &, <, >, ", and '.
     * 
     * @param input the String to convert
     * @return a new String with HTML encoded characters
     */
    public static String htmlEncode(String input) {
        String output = input.replaceAll("&", "&amp;");
        output = output.replaceAll("<", "&lt;");
        output = output.replaceAll(">", "&gt;");
        output = output.replaceAll("\"", "&quot;");
        output = output.replaceAll("'", "&#039;");
        return output;
    }

    /**
     * Returns true if the supplied int is even, false if it is odd.
     * 
     * @param i The number to test.
     * @return True if the number was even, false if it was odd.
     */
    public static boolean isEven(int i) {
        return (i % 2) == 0;
    }

    /**
     * Join a list of strings with the given joining string.
     * 
     * @param strings The strings to join.
     * @param join The string to join them with, or null if no string is to be used.
     * @return The string consisting of the strings joined together.
     */
    public static String join(List<String> strings, String join) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < strings.size(); ++i) {
            String entry = strings.get(i);
            buffer.append(entry);
            if (join != null) {
                if (i < (strings.size() - 1)) {
                    buffer.append(join);
                }
            }
        }
        return buffer.toString();
    }
}