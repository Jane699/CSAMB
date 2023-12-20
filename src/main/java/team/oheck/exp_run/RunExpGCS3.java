package team.oheck.exp_run;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import team.oheck.commom.ConfigUtil;
import team.oheck.exp.ExpGCS3;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Data
public class RunExpGCS3 {
    private int csJsStatus = 0;

    public void run(String name, String[] datasets, int type, boolean enableSearch, int maxThreads) {
        Properties props = ConfigUtil.getProp();
        String dataPath = (String) props.get("data_path");

        if (maxThreads == -1) {
            maxThreads = 5;
        }
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        for (String dataset : datasets) {
            executor.submit(
                    new RunExpGCS3Handle(dataPath + dataset, name, type, enableSearch, dataset, csJsStatus)
            );
        }
    }
}

@Log4j2
@Data
class RunExpGCS3Handle implements Runnable {
    private final String filePath;
    private final String name;
    private final int type;
    private final boolean enableSearch;
    private final String dataSet;
    private int csJsStatus;

    public RunExpGCS3Handle(String filePath, String name, int type, boolean enableSearch, String dataSet, int csJsStatus) {
        this.filePath = filePath;
        this.name = name;
        this.type = type;
        this.enableSearch = enableSearch;
        this.dataSet = dataSet;
        this.csJsStatus = csJsStatus;
    }

    @Override
    public void run() {
        ExpGCS3 expGCS3 = new ExpGCS3();
        expGCS3.setName(name);
        expGCS3.setType(type);
        expGCS3.setCsJsStatus(csJsStatus);
        expGCS3.setDataSet(dataSet);
        expGCS3.setEnableSearch(enableSearch);
        expGCS3.loadData(new File(filePath));
        expGCS3.run();
    }
}