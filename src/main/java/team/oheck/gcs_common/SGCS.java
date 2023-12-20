package team.oheck.gcs_common;

import javafx.util.Pair;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import team.oheck.commom.HFunctions;
import team.oheck.gcs_common.FVNode;
import team.oheck.gcs_common.SDNode;
import team.oheck.gcs_common.SFVDNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 特定类的sfvdNode初始化，dNode初始化
 */
@Log4j2
@Data
public class SGCS {
    private Object[][] originalDT;

    private List<Map<Object, FVNode>> fdvlList; //list contain all f
    private Map<Object, List<SFVDNode>> sfListHash;
    private SDNode sdNode;
    private Set<Object> dvSet;

    private Integer rows = 0;
    private Integer columnsF = 0;
    private Integer columnDIdx = 0;
    private Map<Object, Object[][]> csDTMap;

    public SGCS() {
    }

    /**
     * init decision table
     *
     * @param DT decision table
     */
    public void init(Object[][] DT) {
        if (DT.length == 0) {
            return;
        }

        this.originalDT = DT;
        this.rows = DT.length;
        this.columnsF = DT[0].length - 1;
        this.columnDIdx = columnsF;

        sfListHash = new HashMap<>();

        // dvSet init
        dvSet = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            dvSet.add(originalDT[i][columnDIdx]);
        }

        // fdvlList, dHash init
        sdNode = new SDNode();
        sdNode.dHash = new HashMap<>();
        this.fdvlList = new ArrayList<>(this.columnsF);
        for (int i = 0; i < columnsF; i++) {
            Map<Object, FVNode> fdvlHash = new HashMap<>();
            fdvlList.add(fdvlHash);
            for (int j = 0; j < rows; j++) {
                FVNode fvNode = fdvlHash.getOrDefault(originalDT[j][i], new FVNode());
                fvNode.vNum++;
                fvNode.dvHash.put(originalDT[j][columnDIdx], fvNode.dvHash.getOrDefault(originalDT[j][columnDIdx], 0) + 1);
                fdvlHash.put(originalDT[j][i], fvNode);
            }
        }

        // dHash init
        for (int i = 0; i < rows; i++) {
            sdNode.dHash.put(originalDT[i][columnDIdx], sdNode.dHash.getOrDefault(originalDT[i][columnDIdx], 0) + 1);
        }

        // sdHash init
        for (Map.Entry<Object, Integer> kv : sdNode.dHash.entrySet()) {
            Map<Object, Integer> item = new HashMap<>();
            // specific class
            item.put(kv.getKey(), kv.getValue());
            // others
            item.put("#", this.rows - kv.getValue());
            sdNode.sdHash.put(kv.getKey(), item);
        }

        // shHash init
        for (Map.Entry<Object, Integer> kv : sdNode.dHash.entrySet()) {
            sdNode.shHash.put(kv.getKey(), HFunctions.HX(sdNode.sdHash.get(kv.getKey()), rows));
        }



        log.info("Init Finish: rows: %d, columnsF: %d, columnDIdx: %d".formatted(rows, columnsF, columnDIdx));
    }


    /**
     * 第一种class specific变换
     */
    public void transformDT1() {
        // sfList init

        // for each dValue
        for (Object d : this.dvSet) {
            List<SFVDNode> sfList = new ArrayList<>();

            for (int i = 0; i < columnsF; i++) {
                SFVDNode sfvdNode = new SFVDNode();
                sfvdNode.setFId(i);
                sfList.add(sfvdNode);

                Map<Object, FVNode> objectFVNodeMap = this.fdvlList.get(i);

                // sfvlHash
                Integer tmpCounter = 0; // 计数那些被改变的行，对应的值用#标记
                for (Map.Entry<Object, FVNode> kv : objectFVNodeMap.entrySet()) {
                    Integer curDInKey = kv.getValue().getDvHash().get(d);
                    if (curDInKey != null) {
                        // 不需要改变的行
                        sfvdNode.getSfvlHash().put(kv.getKey(), kv.getValue().getVNum());
                    } else {
                        // 改变的行
                        tmpCounter += kv.getValue().getVNum();
                    }
                }
                if (tmpCounter > 0){
                    sfvdNode.getSfvlHash().put("#", tmpCounter);
                }

                // sfdHash
                AtomicInteger pairTmpCounter = new AtomicInteger();
                for (Object k : sfvdNode.getSfvlHash().keySet()) {
                    if ("#".equals(k)) {
                        break;
                    }
                    objectFVNodeMap.get(k).getDvHash().forEach((key, value) -> {
                        Pair<Object, Object> pair;
                        if (d.equals(key)) {
                            pair = new Pair<>(k, d);
                        } else {
                            pair = new Pair<>(k, "#");
                        }
                        sfvdNode.getSfdHash().put(pair, sfvdNode.getSfdHash().getOrDefault(pair, 0) + value);
                        pairTmpCounter.addAndGet(value);
                    });
                }
                if (tmpCounter > 0) {
                    sfvdNode.getSfdHash().put(new Pair<>("#", "#"), tmpCounter);
                }
                // sH
                sfvdNode.setSHF(HFunctions.HX(sfvdNode.getSfvlHash(), rows));
                sfvdNode.setSHFAndD(HFunctions.HX(sfvdNode.getSfdHash(), rows));
                // SU(HX, HY, HXY) = (H(X)+H(Y)-H(XY)) / (H(X)+H(Y))
                // -- F变换 D变换 Start --
                // sfvdNode.setSuFD(HFunctions.SU(sfvdNode.getSHF(), sdNode.getShHash().get(d), sfvdNode.getSHFAndD()));
                // -- F变换 D变换 End --

                // -- F不变 D变换 --
                Map<Object, Integer> originFHash = new HashMap<>();
                for (Map.Entry<Object, FVNode> kv : objectFVNodeMap.entrySet()) {
                    originFHash.put(kv.getKey(), kv.getValue().getVNum());
                }
                double hFOrigin = HFunctions.HX(originFHash, rows);
                Object[][] csD = new Object[rows][1];
                for (int j = 0; j < rows; j++) {
                    csD[j][0] = originalDT[j][columnDIdx].equals(d) ? d : "#";
                }
                Map<Pair<Object, Object>, Integer> fAndCsDCounter = HFunctions.counterBy2Column(originalDT, csD, i, 0);
                double hDCS = HFunctions.HX(csD, 0);
                double hfAndDOrigin = HFunctions.HX(fAndCsDCounter, rows);
                sfvdNode.setSuFD(HFunctions.SU(hFOrigin, hDCS, hfAndDOrigin));
                // -- F不变 D变换 --
            }
            sfListHash.put(d, sfList);
        }

        // csDTMap init
        csDTMap = new HashMap<>();
        for (Object dv: dvSet) {
            csDTMap.put(dv, getCSTransformDT1(dv));
        }
    }

    /**
     * class specific 变换
     * @param dV d Value
     * @return class specific 变换后的数据集
     */
    public Object[][] getCSTransformDT1(Object dV) {
        List<SFVDNode> sfList = sfListHash.get(dV);

        Object[][] transformedDT = new Object[rows][columnsF + 1];

        for (int i = 0; i < columnsF; i++) {
            SFVDNode sfvdNode = sfList.get(i);
            for (int j = 0; j < rows; j++) {
                if (sfvdNode.sfvlHash.containsKey(originalDT[j][i])){
                    transformedDT[j][i] = originalDT[j][i];
                } else {
                    transformedDT[j][i] = "#";
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            if (originalDT[i][columnDIdx].equals(dV)) {
                transformedDT[i][columnDIdx] = dV;
            } else {
                transformedDT[i][columnDIdx] = "#";
            }
        }
        return transformedDT;
    }

}
