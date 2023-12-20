package team.oheck.commom;

public class ShowUtil {
    /**
     * show a 2d array
     */
    public static void show(String[][] o) {
        for (String[] strings : o) {
            for (String string : strings) {
                System.out.print(string + " ");
            }
            System.out.println();
        }
    }
}
