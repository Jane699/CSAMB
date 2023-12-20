package team.oheck.exp;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.StopWatch;
import team.oheck.commom.ConfigUtil;
import team.oheck.commom.CsvOrExcelFileUtil;
import team.oheck.commom.CsvUtils;
import team.oheck.commom.DateTimeUtil;
import team.oheck.gcs.PGS;
import team.oheck.gcs_common.HeaderBean;
import team.oheck.gcs_common.SFVDNode;
import team.oheck.gcs_common.SGCS;

import java.io.File;
import java.util.*;

@Data
@Log4j2
public class ExpGCSMin {
    private StopWatch stopWatch = new StopWatch();
    private List<String[]> res;
    private int type;
    private String name;
    private boolean enableSearch = false;
    private String[][] table;
    private String dataSet;
    private long groupTime;
    private long searchTime;
    private int csJsStatus = 0;

    public void loadData(File csvFile) {
        this.table = CsvOrExcelFileUtil.readCsvFileToArray(csvFile.getAbsoluteFile().toString(), "utf-8");
    }

    public void run() {
        SGCS sgcs = new SGCS();
        sgcs.init(table);
        sgcs.transformDT1();
        Set<Object> dvSet = sgcs.getDvSet();
        Map<Object, List<SFVDNode>> sfListHash = sgcs.getSfListHash();
        List<List<SFVDNode>> resAllClass = new ArrayList<>();

        String[][] data1 = new String[dvSet.size()][HeaderBean.headerWithCS.length];
        int classIdx = 0;
        for (Object dv : dvSet) {
            StopWatch sw = new StopWatch();
            PGS pgs = new PGS();
            pgs.setOriginalDT(table);
            pgs.setCsDT(sgcs.getCSTransformDT1(dv));
            pgs.setCsDTMap(sgcs.getCsDTMap());
            pgs.setDvSet(dvSet);
            pgs.setCurDv(dv);
            pgs.setCsType(this.type);
            pgs.setSfvdNodes(new ArrayList<>(sfListHash.get(dv)));
            pgs.genFHash();
            pgs.sortFNodeListBySUFD();

            sw.start();
            pgs.gpgg();
            sw.stop();
            groupTime = sw.getTime();

            if (csJsStatus == 0) {
                sw.reset();
                sw.start();
                pgs.sfsOnlyInGroup();
                sw.stop();
                searchTime = sw.getTime();
            } else if (csJsStatus == 1) {
                sw.reset();
                sw.start();
                pgs.csSfsOnlyInGroup();
                sw.stop();
                searchTime = sw.getTime();
            } else if (csJsStatus == 2) {
                sw.reset();
                sw.start();
                pgs.sfsIncOnlyInGroup(0.01);
                sw.stop();
                searchTime = sw.getTime();
            }

            // add current solution
            resAllClass.add(pgs.getCurSolution());

            log.info("--groups--");
            List<List<SFVDNode>> groups = pgs.getGroups();
            List<List<Integer>> groupOutline = new ArrayList<>();
            List<Integer> groupItem;
            for (List<SFVDNode> g : groups) {
                groupItem = g.stream().map(SFVDNode::getFId).toList();
                groupOutline.add(groupItem);
                log.info(groupItem);
            }
            List<Integer> pFeatureOutline = groups.stream().map(a -> a.get(0).getFId()).toList();

            log.info("len(group): " + groups.size());
            log.info("--res--");
            List<SFVDNode> res = pgs.getCurSolution();
            List<Integer> resOutline = res.stream().map(SFVDNode::getFId).toList();
            log.info(resOutline);
            log.info("len(res): " + res.size());

            int idx = 0;
            data1[classIdx][idx++] = dataSet;
            data1[classIdx][idx++] = String.valueOf(dv);
            data1[classIdx][idx++] = String.valueOf(res.size());
            data1[classIdx][idx++] = String.valueOf(groups.size());
            data1[classIdx][idx++] = JSON.toJSONString(resOutline);
            data1[classIdx][idx++] = JSON.toJSONString(pFeatureOutline);
            data1[classIdx][idx++] = JSON.toJSONString(groupOutline);
            data1[classIdx][idx++] = JSON.toJSONString(res);
            data1[classIdx][idx++] = JSON.toJSONString(groups);
            data1[classIdx][idx++] = JSON.toJSONString(pgs.getSfvdNodes());
            data1[classIdx][idx++] = JSON.toJSONString(pgs.getFfSU());
            data1[classIdx][idx++] = JSON.toJSONString(this.groupTime);
            data1[classIdx][idx++] = JSON.toJSONString(this.searchTime);
            data1[classIdx][idx] = JSON.toJSONString("-");
            classIdx++;
        }

        Set<Integer> finalSolution = new HashSet<>();
        for (List<SFVDNode> res : resAllClass) {
            List<Integer> collect = res.stream().map(SFVDNode::getFId).toList();
            finalSolution.addAll(collect);
        }
        log.info(finalSolution);
        log.info("final res_size: " + finalSolution.size());

        String[][] data2 = new String[1][HeaderBean.classSpecificInfo.length];
        int idx = 0;
        data2[0][idx++] = dataSet;
        data2[0][idx++] = JSON.toJSONString(finalSolution.size());
        data2[0][idx] = JSON.toJSONString(finalSolution);

        saveToFile(data1, data2);
    }

    public void saveToFile(String[][] data1, String[][] data2) {
        Properties props = ConfigUtil.getProp();
        String savePath = (String) props.get("save_path");
        String saveFilePathDetail = savePath + "gcs_min/" + name + "/" + dataSet.substring(0, dataSet.lastIndexOf(".")) + "#" + DateTimeUtil.now() + ".csv";
        String saveFilePathFinalRes = savePath + "gcs_min/" + name + "/final_res/" + dataSet.substring(0, dataSet.lastIndexOf(".")) + "#" + DateTimeUtil.now() + ".csv";
        CsvUtils.toCsv(saveFilePathDetail, HeaderBean.headerWithCS, data1);
        CsvUtils.toCsv(saveFilePathFinalRes, HeaderBean.classSpecificInfo, data2);
        log.info("save to: " + saveFilePathDetail);
        log.info("save to: " + saveFilePathFinalRes);
    }
}
