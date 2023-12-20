package team.oheck;

public class Run {
    public static void main(String[] args) {
        Setup.curDataset = new String[]{"chess.csv"};
        String expInfo = "exp_default";
        Setup.runGCS3(expInfo, 1);
    }
}