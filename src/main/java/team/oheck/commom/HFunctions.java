package team.oheck.commom;


import javafx.util.Pair;

import java.util.*;

public class HFunctions {


    /**
     * collection element counter
     *
     * @param collection collection
     * @param <T>        element type
     * @return counter map
     */
    public static <T> Map<T, Integer> counter(Collection<T> collection) {
        Map<T, Integer> res = new HashMap<>();
        for (T t : collection) {
            res.put(t, res.getOrDefault(t, 0) + 1);
        }
        return res;
    }

    /**
     * array element counter
     *
     * @param array array
     * @param <T>   element type
     * @return counter map
     */
    public static <T> Map<T, Integer> counter(T[] array) {
        Map<T, Integer> res = new HashMap<>();
        for (T t : array) {
            res.put(t, res.getOrDefault(t, 0) + 1);
        }
        return res;
    }

    /**
     * count by column
     *
     * @param collection collection
     * @param columnIdx  column index
     * @param <T>        element type
     * @return counter map
     */
    public static <T> Map<T, Integer> counterByColumn(Collection<List<T>> collection, int columnIdx) {
        Map<T, Integer> res = new HashMap<>();
        for (List<T> list : collection) {
            T key = list.get(columnIdx);
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    /**
     * counter by column
     *
     * @param arrays    arrays
     * @param columnIdx column index
     * @param <T>       element type
     * @return counter map
     */
    public static <T> Map<T, Integer> counterByColumn(T[][] arrays, int columnIdx) {
        Map<T, Integer> res = new HashMap<>();
        for (T[] array : arrays) {
            T key = array[columnIdx];
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    /**
     * counter by tuple consisting of two column elements
     *
     * @param collection collection
     * @param columnIdx1 column_1 index
     * @param columnIdx2 column_2 index
     * @param <T>        element type
     * @return counter map
     */
    public static <T> Map<Pair<T, T>, Integer> counterBy2Column(Collection<List<T>> collection, int columnIdx1, int columnIdx2) {
        Map<Pair<T, T>, Integer> res = new HashMap<>();
        for (List<T> list : collection) {
            Pair<T, T> key = new Pair<>(list.get(columnIdx1), list.get(columnIdx2));
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    public static <T> Map<Pair<T, T>, Integer> counterBy2Column(T[][] arr1, T[][] arr2, int c1, int c2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("arr1.length != arr2.length");
        }
        Map<Pair<T, T>, Integer> res = new HashMap<>();
        for (int i = 0; i < arr1.length; i++) {
            Pair<T, T> key = new Pair<>(arr1[i][c1], arr2[i][c2]);
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    /**
     * counter by tuple consisting of two column elements
     *
     * @param arrays  2-dim array
     * @param column1 column1_index
     * @param column2 column2_index
     * @param <T>     counter type
     * @return counter map
     */
    public static <T> Map<Pair<T, T>, Integer> counterBy2Column(T[][] arrays, int column1, int column2) {
        Map<Pair<T, T>, Integer> res = new HashMap<>();
        for (T[] array : arrays) {
            Pair<T, T> key = new Pair<>(array[column1], array[column2]);
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    public static <T> Map<Pair<String, T>, Integer> counterBy2Column(T[][] arrays, List<Integer> xList, int column2) {
        Map<Pair<String, T>, Integer> res = new HashMap<>();
        for (T[] array : arrays) {
            StringBuilder key = new StringBuilder();
            for (Integer x : xList) {
                key.append(array[x]);
            }
            Pair<String, T> keyPair = new Pair<>(key.toString(), array[column2]);
            res.put(keyPair, res.getOrDefault(keyPair, 0) + 1);
        }
        return res;
    }

    /**
     * counter by tuple consisting of three column elements
     *
     * @param arrays 2-dim array
     * @param c1     column_1 index
     * @param c2     column_2 index
     * @param c3     column_3 index
     * @param <T>    element type
     * @return counter map
     */
    public static <T> Map<ThreeItem<T, T, T>, Integer> counterBy3Column(T[][] arrays, int c1, int c2, int c3) {
        Map<ThreeItem<T, T, T>, Integer> res = new HashMap<>();
        for (T[] array : arrays) {
            ThreeItem<T, T, T> key = new ThreeItem<>(array[c1], array[c2], array[c3]);
            res.put(key, res.getOrDefault(key, 0) + 1);
        }
        return res;
    }

    public static <T> Map<String, Integer> counterByMulColumn(T[][] arrays, List<Integer> fList) {
        int fListCol = fList.size();
        Map<String, Integer> res = new HashMap<>();
        for (T[] array : arrays) {
            StringBuilder key = new StringBuilder();
            for (int j = 0; j < fListCol; j++) {
                key.append(array[fList.get(j)]);
                key.append("#");
            }
            res.put(key.toString(), res.getOrDefault(key.toString(), 0) + 1);
        }
        return res;
    }

    /**
     * compute Shannon entropy from counter
     *
     * @param counter    counter map
     * @param sampleSize sample size
     * @param <T>        element type
     * @return Shannon entropy
     */
    public static <T> double HX(Map<T, Integer> counter, int sampleSize) {
        double h = 0d;
        for (Integer v : counter.values()) {
            double p = (double) v / sampleSize;
            h -= p * MathFunctions.log2(p);
        }
        return h;
    }

    /**
     * compute Shannon entropy of the column
     *
     * @param table     2-dim data table
     * @param columnIdx column index
     * @param <T>       element type
     * @return Shannon entropy
     */
    public static <T> double HX(T[][] table, int columnIdx) {
        int sampleSize = table.length;
        Map<T, Integer> counter = counterByColumn(table, columnIdx);
        return HX(counter, sampleSize);
    }

    public static <T> double HX(T[][] table, List<Integer> fList) {
        Map<String, Integer> counter = counterByMulColumn(table, fList);
        int sampleSize = table.length;
        return HX(counter, sampleSize);
    }

    /**
     * compute joint Shannon entropy of two column
     *
     * @param table      2-dim data table
     * @param columnIdx1 column_1 index
     * @param columnIdx2 column_2 index
     * @param <T>        element type
     * @return Shannon entropy
     */
    public static <T> double HXAndY(T[][] table, int columnIdx1, int columnIdx2) {
        Map<Pair<T, T>, Integer> counter = counterBy2Column(table, columnIdx1, columnIdx2);
        int sampleSize = table.length;
        return HX(counter, sampleSize);
    }

    public static <T> double HXAndY(T[][] table, List<Integer> xList, int y) {
        Map<Pair<String, T>, Integer> counter = counterBy2Column(table, xList, y);
        int sampleSize = table.length;
        return HX(counter, sampleSize);
    }

    public static <T> double HXAndY(T[][] table1, T[][] table2, int columnIdx1, int columnIdx2) {
        Map<Pair<T, T>, Integer> counter = counterBy2Column(table1, table2, columnIdx1, columnIdx2);
        int sampleSize = table1.length;
        return HX(counter, sampleSize);
    }


    public static <T> double HXAndYAndZ(T[][] table, int x, int y, int z) {
        Map<ThreeItem<T, T, T>, Integer> counter = counterBy3Column(table, x, y, z);
        int sampleSize = table.length;
        return HX(counter, sampleSize);
    }

    /**
     * compute information gain
     *
     * @param table      2-dim data table
     * @param columnIdx1 column_1 index
     * @param columnIdx2 column_2 index
     * @param <T>        element type
     * @return information gain
     */
    public static <T> double IXY(T[][] table, int columnIdx1, int columnIdx2) {
        double H1 = HX(table, columnIdx1);
        double H2 = HX(table, columnIdx2);
        double H3 = HXAndY(table, columnIdx1, columnIdx2);

        return H1 + H2 - H3;
    }

    public static <T> double IXY(T[][] table, List<Integer> xList, int y) {
        double H1 = HX(table, xList);
        double H2 = HX(table, y);
        double H3 = HXAndY(table, xList, y);

        return H1 + H2 - H3;
    }

    /**
     * compute information gain
     *
     * @param table 2-dim data table
     * @param x     column_1 index
     * @param y     column_2 index
     * @param z     column_3 index
     * @param <T>   element type
     * @return information gain
     */
    public static <T> double IXYZ(T[][] table, int x, int y, int z) {
        double HX = HX(table, x);
        double HY = HX(table, y);
        double HZ = HX(table, z);
        double HXY = IXY(table, x, y);
        double HXZ = IXY(table, x, z);
        double HYZ = IXY(table, y, z);
        double HXYZ = HXAndYAndZ(table, x, y, z);
        return HXYZ - HX - HY - HZ + HXY + HXZ + HYZ;
    }

    /**
     * compute su
     *
     * @param table      2-dim data table
     * @param columnIdx1 column_1 index
     * @param columnIdx2 column_2 index
     * @param <T>        element type
     * @return su
     */
    public static <T> double SU(T[][] table, int columnIdx1, int columnIdx2) {
        double H1 = HX(table, columnIdx1);
        double H2 = HX(table, columnIdx2);
        double H3 = HXAndY(table, columnIdx1, columnIdx2);

        return 2d * (1d - H3 / (H1 + H2));
    }

    /**
     * compute su
     *
     * @param HX    H(X)
     * @param HY    H(Y)
     * @param HAndY H(X,Y)
     * @return su
     */
    public static double SU(double HX, double HY, double HAndY) {
        return 2d * (1d - HAndY / (HX + HY));
    }

    /**
     * batch update entropy by new sample
     *
     * @param hOld old h
     * @param r    r, |U|
     * @param m    m, num of new sample
     * @return new entropy
     */
    public static double calculateHByNewSampleValueNoExistB(double hOld, double r, double m) {
        if (m == 0) {
            return hOld;
        }
        return (r / (r + m)) * hOld - (r / (r + m)) * MathFunctions.log2(r) - MathFunctions.log2(1d / (r + m));
    }


    /**
     * batch update entropy by old sample
     *
     * @param hOld old h
     * @param r    r, |U|
     * @param m    m
     * @param nf   Nf
     * @return new entropy
     */
    public static double calculateHByNewSampleValueExistB(double hOld, double r, double m, double nf) {
        if (m == 0) {
            return hOld;
        }
        return (r / (r + m)) * hOld - (r / (r + m)) * MathFunctions.log2(r) - MathFunctions.log2(1d / (r + m)) - (1d / (r + m)) * ((nf + m) * MathFunctions.log2(nf + m) - (nf) * MathFunctions.log2(nf));
    }

    /**
     * calculate cross su matrix
     *
     * @param table 2-dim data table
     * @param <T>   element type
     * @return 2-dim cross su matrix
     */
    public static <T> double[][] calculateCrossSU(T[][] table) {
        int columnSize = table[0].length;
        double[][] crossSU = new double[columnSize][columnSize];
        for (int i = 0; i < columnSize; i++) {
            for (int j = i; j < columnSize; j++) {
                crossSU[i][j] = SU(table, i, j);
                crossSU[j][i] = crossSU[i][j];
            }
        }
        return crossSU;
    }

    public static <T> double SU(T[][] table1, T[][] table2, int c1, int c2) {
        double hC1 = HX(table1, c1);
        double hC2 = HX(table2, c2);
        double hC1C2 = HXAndY(table1, table2, c1, c2);
        return SU(hC1, hC2, hC1C2);
    }

    public static <T> double[][] calculateCrossSUCS(T[][] origin, T[][] csDT) {
        int columnSize = csDT[0].length;
        double[][] crossSU = new double[columnSize][columnSize];
        for (int i = 0; i < columnSize; i++) {
            for (int j = i; j < columnSize; j++) {
                double su;
                if (i != columnSize - 1 && j == columnSize - 1) {
                    su = SU(origin, csDT, i, j);
                } else {
                    su = SU(csDT, i, j);
                }
                crossSU[i][j] = su;
                crossSU[j][i] = crossSU[i][j];
            }
        }
        return crossSU;
    }
}