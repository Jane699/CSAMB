package team.oheck.exp_run;

import lombok.extern.log4j.Log4j2;
import team.oheck.commom.ConfigUtil;
import team.oheck.exp.ExpPGS;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunExpPGS {
    public void run(String name, String[] datasets, int type, boolean enableSearch, int maxThreads) {
        Properties props = ConfigUtil.getProp();
        String dataPath = (String) props.get("data_path");

        if (maxThreads == -1) {
            maxThreads = 5;
        }
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        for (String dataset : datasets) {
            executor.submit(
                    new RunExpPGSHandle(dataPath + dataset, name, type, enableSearch, dataset)
            );
        }
    }
}

@Log4j2
class RunExpPGSHandle implements Runnable {
    private final String filePath;
    private final String name;
    private final int type;
    private final boolean enableSearch;
    private final String dataSet;

    public RunExpPGSHandle(String filePath, String name, int type, boolean enableSearch, String dataSet) {
        this.filePath = filePath;
        this.name = name;
        this.type = type;
        this.enableSearch = enableSearch;
        this.dataSet = dataSet;
    }

    @Override
    public void run() {
        ExpPGS expPGS = new ExpPGS();
        expPGS.setName(name);
        expPGS.setType(type);
        expPGS.setDataSet(dataSet);
        expPGS.setEnableSearch(enableSearch);
        expPGS.loadData(new File(filePath));
        expPGS.run();
    }
}