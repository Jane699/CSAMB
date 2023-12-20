package team.oheck.exp;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.StopWatch;
import team.oheck.commom.ConfigUtil;
import team.oheck.commom.CsvOrExcelFileUtil;
import team.oheck.commom.CsvUtils;
import team.oheck.commom.DateTimeUtil;
import team.oheck.gcs_common.HeaderBean;
import team.oheck.gcs_common.NGCS;
import team.oheck.gcs.PGS;
import team.oheck.gcs_common.SFVDNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
@Log4j2
public class ExpPGS {
    private StopWatch stopWatch = new StopWatch();
    private String name;
    private int type;
    private boolean enableSearch = false;
    private String[][] table;
    private String dataSet;

    private long groupTime = 0L;
    private long searchTime = 0L;

    public void loadData(File csvFile) {
        this.table = CsvOrExcelFileUtil.readCsvFileToArray(csvFile.getAbsoluteFile().toString(), "utf-8");
    }

    public void run() {
        NGCS ngcs = new NGCS(table);
        ngcs.init();
        List<SFVDNode> sfvdNodes = ngcs.getSfvdNodes();

        PGS pgs = new PGS();
        pgs.setOriginalDT(table);
        pgs.setSfvdNodes(sfvdNodes);
        pgs.genFHash();
        stopWatch.start();
        pgs.gpgg();
        stopWatch.stop();
        this.groupTime = stopWatch.getTime();
        if (enableSearch) {
            stopWatch.reset();
            stopWatch.start();
            pgs.vns(20, 5);
            stopWatch.stop();
            this.searchTime = stopWatch.getTime();
        } else {
            log.info("!!! search disabled");
        }

        saveToFile(pgs);
    }

    public void saveToFile(PGS pgs) {

        log.info("--groups--");
        List<List<SFVDNode>> groups = pgs.getGroups();
        List<List<Integer>> groupOutline = new ArrayList<>();
        List<Integer> groupItem;
        for (List<SFVDNode> g : groups) {
            groupItem = g.stream().map(SFVDNode::getFId).toList();
            groupOutline.add(groupItem);
            log.info(groupItem);
        }
        log.info("len(group): " + groups.size());
        log.info("--res--");
        List<SFVDNode> res = pgs.getCurSolution();
        List<Integer> resOutline = res.stream().map(SFVDNode::getFId).toList();
        log.info(resOutline);
        log.info("len(res): " + res.size());

        List<Integer> pFeatureOutline = groups.stream().map(a -> a.get(0).getFId()).toList();

        Properties props = ConfigUtil.getProp();
        String savePath = (String) props.get("save_path");
        String saveFilePath = savePath + "pgvns/" + name + "/" + dataSet.substring(0, dataSet.lastIndexOf(".")) + "#" + DateTimeUtil.now() + ".csv";

        String[][] data = new String[1][HeaderBean.headerNoCS.length];

        int idx = 0;
        data[0][idx++] = dataSet;
        data[0][idx++] = String.valueOf(res.size());
        data[0][idx++] = String.valueOf(groups.size());
        data[0][idx++] = JSON.toJSONString(resOutline);
        data[0][idx++] = JSON.toJSONString(pFeatureOutline);
        data[0][idx++] = JSON.toJSONString(groupOutline);
        data[0][idx++] = JSON.toJSONString(res);
        data[0][idx++] = JSON.toJSONString(groups);
        data[0][idx++] = JSON.toJSONString(pgs.getSfvdNodes());
        data[0][idx++] = JSON.toJSONString(pgs.getFfSU());
        data[0][idx++] = JSON.toJSONString(this.groupTime);
        data[0][idx++] = JSON.toJSONString(this.searchTime);
        data[0][idx] = JSON.toJSONString(pgs.getFinalJS());

        CsvUtils.toCsv(saveFilePath, HeaderBean.headerNoCS, data);
        log.info("save to: " + saveFilePath);
    }
}
