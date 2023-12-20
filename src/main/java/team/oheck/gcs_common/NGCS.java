package team.oheck.gcs_common;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import team.oheck.commom.HFunctions;
import team.oheck.gcs_common.SDNode;
import team.oheck.gcs_common.SFVDNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 非特定类的sfvdNode初始化，dNode初始化
 */
@Data
@Log4j2
public class NGCS {
    private Object[][] originalDT;
    private List<SFVDNode> sfvdNodes;
    private SDNode sdNode;
    private double delta = 0d;

    private Integer rows = 0;
    private Integer columnsF = 0;
    private Integer columnDIdx = 0;

    public NGCS(Object[][] table) {
        this.originalDT = table;
    }

    public void init() {
        log.info("init start");
        if (originalDT.length == 0) {
            return;
        }

        this.sfvdNodes = new ArrayList<>();
        this.sdNode = new SDNode();

        this.rows = originalDT.length;
        this.columnsF = originalDT[0].length - 1;
        this.columnDIdx = columnsF;

        // init dNode
        sdNode = new SDNode();
        sdNode.setH(HFunctions.HX(originalDT, columnDIdx));

        // init sfvdNodes
        for (int i = 0; i < this.columnsF; i++) {
            SFVDNode sfvdNode = new SFVDNode();
            sfvdNode.setFId(i);
            sfvdNode.setSHF(HFunctions.HX(originalDT, i));
            sfvdNode.setSuFD(HFunctions.SU(originalDT, i, columnDIdx));
            sfvdNode.setSHFAndD(HFunctions.HXAndY(originalDT, i, columnDIdx));
            sfvdNodes.add(sfvdNode);
        }

        log.info("init end");
    }
}
