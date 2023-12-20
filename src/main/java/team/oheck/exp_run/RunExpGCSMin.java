package team.oheck.exp_run;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import team.oheck.commom.ConfigUtil;
import team.oheck.exp.ExpGCSMin;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Data
public class RunExpGCSMin {
    private int csJsStatus;

    public void run(String name, String[] datasets, int type, boolean enableSearch, int maxThreads) {
        Properties props = ConfigUtil.getProp();
        String dataPath = (String) props.get("data_path");

        if (maxThreads == -1) {
            maxThreads = 5;
        }
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        for (String dataset : datasets) {
            executor.submit(
                    new RunExpMinHandle(dataPath + dataset, name, type, enableSearch, dataset, csJsStatus)
            );
        }
    }
}

@Log4j2
@Data
class RunExpMinHandle implements Runnable {
    private final String filePath;
    private final String name;
    private final int type;
    private final boolean enableSearch;
    private final String dataSet;
    private int csJsStatus;

    public RunExpMinHandle(String filePath, String name, int type, boolean enableSearch, String dataSet, int csJsStatus) {
        this.filePath = filePath;
        this.name = name;
        this.type = type;
        this.enableSearch = enableSearch;
        this.dataSet = dataSet;
        this.csJsStatus = csJsStatus;
    }

    @Override
    public void run() {
        ExpGCSMin expGCSMin = new ExpGCSMin();
        expGCSMin.setName(name);
        expGCSMin.setType(type);
        expGCSMin.setCsJsStatus(csJsStatus);
        expGCSMin.setDataSet(dataSet);
        expGCSMin.setEnableSearch(enableSearch);
        expGCSMin.loadData(new File(filePath));
        expGCSMin.run();
    }
}