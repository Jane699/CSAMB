package team.oheck.gcs_common;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class SDNode {
    /** 整体的计数Hash */
    public Map<Object, Integer> dHash;
    /** d全类上的H */
    public double h;
    /** 特定类上的计数Hash */
    public Map<Object, Map<Object, Integer>> sdHash;
    /** 特定类上D的熵值 */
    public Map<Object, Double> shHash;

    public SDNode() {
        this.dHash = new HashMap<>();
        this.h = 0d;
        this.sdHash = new HashMap<>();
        this.shHash = new HashMap<>();
    }
}
