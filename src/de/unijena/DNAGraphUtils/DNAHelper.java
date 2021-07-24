package de.unijena.DNAGraphUtils;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides multiple static functions that are useful for DNA encoding/decoding
 */
class DNAHelper {
    /**
     * Finds the first regex match.
     *
     * @param str {@link String} to operate regex on
     * @param regex Regex to use
     * @return first matched string
     */
    public static String findFirstOccurance(String str, String regex){
        Matcher m =  Pattern.compile(regex)
                .matcher(str);
        if (m.find())
            return m.group();

        throw new IllegalArgumentException("No matches found");
    }

    /**
     * Converts a number to DNA sequence with a fixed length given a radix.
     *
     * @param number Number that should be encoded in DNA
     * @param radix Count of DNA bases that should be used
     * @param length Lower bound of allowed length (if the number encoded in DNA is longer, it wont be truncated)
     * @return Encoded number
     */
    public static String toDNA(int number, int radix, int length){
        assert radix <= 4;
        return String.format("%1$" + length + "s", toDNA(number, radix)).replace(' ', 'A');
    }

    /**
     * Replaces all digits 0,1,2,3 by the DNA bases A,C,G,T.
     *
     * @param str original string
     * @return string with DNA bases instead of 0-3
     */
    public static String toDNA(String str){
        return str.replace('0', 'A')
                .replace('1','C')
                .replace('2', 'G')
                .replace('3', 'T');
    }

    /**
     * Converts a number to DNA sequence given a radix.
     *
     * @param number Number that should be encoded in DNA
     * @param radix Count of DNA bases that should be used
     * @return Encoded number
     */
    public static String toDNA(int number, int radix){
        return toDNA(Integer.toString(number, radix));
    }

    /**
     * Decodes a given DNA sequence with a given radix to the corresponding integer.
     *
     * @param number DNA sequence
     * @param radix Count of DNA bases that were be used
     * @return Decoded number
     */
    public static int parseDNA(String number, int radix){
        return Integer.parseInt(number
                .replace('A', '0')
                .replace('C','1')
                .replace('G', '2')
                .replace('T', '3'), radix);
    }

    /**
     * Inverts any HashMap.
     *
     * @param map original map
     * @param <T> original key type, will be value type in return value
     * @param <V> original value type, will be key type in return value
     * @return inverted map
     */
    public static <T,V> HashMap<T,V> invertMap(Map<V,T> map){
        HashMap<T,V> invMap = new HashMap<>();
        for (Map.Entry<V,T> entry : map.entrySet()) {
            invMap.put(entry.getValue(), entry.getKey());
        }
        return invMap;
    }

    /**
     * Writes a DNA representation of a given list to the given {@link StringBuilder}.
     * Can be read with {@link DNAHelper#parseList(Collection, String)}.
     *
     * @param vals values to append
     * @param sb object which values will be appended to
     */
    public static void appendList(List<Integer> vals, StringBuilder sb){
        // Länge l (Länge der Kodierung des maximalen Werts, Base 3)
        // Trennzeichen T
        // Jeden Wert in fixer Länge l (Base 3):
        // Trennzeichen T
        int maxValue = Collections.max(vals);
        int maxValueReprLength = toDNA(maxValue, 3).length();
        sb.append(toDNA(maxValueReprLength, 3));
        sb.append('T');
        vals.forEach(val -> sb.append(toDNA(val, 3, maxValueReprLength)));
        sb.append('T');
    }

    /**
     * Reads a list written with {@link DNAHelper#appendList(List, StringBuilder)} from the beginning of a given string.
     *
     * @param vals collection which will be filled with the read values
     * @param repr string which will be read from
     * @return rest of the string
     */
    public static String parseList(Collection<Integer> vals, String repr){
        String lengthStr = findFirstOccurance(repr, "^[ACG]*(?=T)");
        int length = parseDNA(lengthStr, 3);
        repr = repr.substring(lengthStr.length() + 1);
        String valsStr = findFirstOccurance(repr, "^[ACG]*(?=T)");
        for (int i = 0; i < valsStr.length(); i+= length) {
            String valStr = valsStr.substring(i, i+length);
            vals.add(parseDNA(valStr, 3));
        }
        return repr.substring(valsStr.length() + 1);
    }
}
