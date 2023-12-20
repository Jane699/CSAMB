package team.oheck.gcs_common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
public class FVNode {
    /** Feature上某个value的数量 */
    public Integer vNum;
    /** Feature上某个值对应的D值计数Hash */
    public Map<Object, Integer> dvHash;

    public FVNode() {
        this.vNum = 0;
        this.dvHash = new HashMap<>();
    }
}
