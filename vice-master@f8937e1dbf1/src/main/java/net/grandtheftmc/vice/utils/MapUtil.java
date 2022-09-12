package net.grandtheftmc.vice.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Luke Bingham on 26/08/2017.
 */
public class MapUtil {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean highToLow) {
        return map.entrySet()
                .stream()
                .sorted(highToLow ? Map.Entry.comparingByValue() : Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static <K, V extends Comparable<? super V>> TreeMap<K, V> sortByValue(HashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort(Comparator.comparing(e -> (e.getValue())));
        TreeMap<K, V> result = new TreeMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
