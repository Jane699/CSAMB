package team.oheck.gcs_common;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
public class SFVDNode {
    /** 特征的索引 */
    public Integer fId;
    /** 当前feature上的计数hash表，k: f_v, v: count */
    public Map<Object, Integer> sfvlHash;
    /** 当前feature和D的联合计数hash表, k: (f_v,d_v), v:count */
    public Map<Pair<Object, Object>, Integer> sfdHash;
    /** 当前特征上的信息熵 */
    public double sHF;
    /** 当前特征与D特征的联合信息熵 */
    public double sHFAndD;
    /** 当前特征与D特征的SU */
    public double suFD;

    public SFVDNode() {
        this.sHF = 0d;
        this.sHFAndD = 0d;
        this.suFD = 0d;
        this.sfdHash = new HashMap<>();
        this.sfvlHash = new HashMap<>();
    }
}
