package team.oheck;

import team.oheck.exp_run.*;


public class Setup {

    static String[] curDataset;

    static String[] dataset27 = new String[]{
            "11_tumors.csv",
            "AR10P.csv",
            "arrhythmia.csv",
            "braintumor.csv",
            "COIL20.csv",
            "connect-4.csv",
            "FPS-7.csv",
            "glass.csv",
            "GLIOMA.csv",
            "isolet.csv",
            "kddcup10.csv",
            "kddcup25.csv",
            "letter-recognition.csv",
            "lung.csv",
            "ORL.csv",
            "orlraws10P.csv",
            "segmentation.csv",
            "SRBCT.csv",
            "USPS.csv",
            "vehicle.csv",
            "waveform.csv",
            "wine.csv",
            "Yale.csv",
            "yeast.csv",
            "zoo.csv",
            "chess.csv",
            "internet-firewall-data.csv"
    };

    static void setCurDataset(String[] datasets) {
        curDataset = datasets;
    }

    // exp run PGS(PGVNS)
    public static void runPGS(String expInfo, int maxThreads) {
        RunExpPGS runExpPGS = new RunExpPGS();
        runExpPGS.run(expInfo, curDataset, 0, true, maxThreads);
    }

    // exp run GCS0(IM)
    public static void runGCS0(String expInfo, int maxThreads) {
        RunExpGCS3 runExpGCS3 = new RunExpGCS3();
        runExpGCS3.run(expInfo, curDataset, 0, true, maxThreads);
    }

    // exp run GCS1(DTM)
    public static void runGCS1(String expInfo, int maxThreads) {
        RunExpGCS3 runExpGCS3 = new RunExpGCS3();
        runExpGCS3.run(expInfo, curDataset, 1, true, maxThreads);
    }

    // exp run GCS2
    public static void runGCS2(String expInfo, int maxThreads) {
        RunExpGCS3 runExpGCS3 = new RunExpGCS3();
        runExpGCS3.run(expInfo, curDataset, 2, true, maxThreads);
    }

    // exp run GCS3(CSAMB)
    public static void runGCS3(String expInfo, int maxThreads) {
        RunExpGCS3 runExpGCS3 = new RunExpGCS3();
        runExpGCS3.run(expInfo, curDataset, 3, true, maxThreads);
    }

    // exp run GCSMIN(CSAMB-min)
    public static void runGCSMin(String expInfo, int maxThreads) {
        RunExpGCSMin runExpGCSMin = new RunExpGCSMin();
        runExpGCSMin.run(expInfo, curDataset, 4, true, maxThreads);
    }

    // exp run GCSMax(CSAMB-max)
    public static void runGCSMax(String expInfo, int maxThreads) {
        RunExpGCSMax runExpGCSMax = new RunExpGCSMax();
        runExpGCSMax.run(expInfo, curDataset, 5, true, maxThreads);
    }
}
