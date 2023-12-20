package team.oheck.commom;

import java.util.Map;

public class CollectionUtil {


    /**
     * merge map value by key (value accumulation
     */
    public static <S> void mergeMapValue(Map<S, Integer> dist, Map<S, Integer> source) {
        for (Map.Entry<S, Integer> entry : source.entrySet()) {
            dist.put(entry.getKey(), dist.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }
}
