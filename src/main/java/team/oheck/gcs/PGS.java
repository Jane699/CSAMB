package team.oheck.gcs;

import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.math3.util.Pair;
import team.oheck.commom.HFunctions;
import team.oheck.commom.ThreeItem;
import team.oheck.gcs_common.JSHistory;
import team.oheck.gcs_common.SFVDNode;

import java.util.*;

/**
 * P-Features Grouping Feature Selection
 */
@Data
@ToString
@Log4j2
public class PGS {
    private Integer columnsF;
    private Integer columnsDIdx;
    private Integer rows;
    private List<List<SFVDNode>> groups;
    private Map<Pair<Integer, Integer>, Double> ffSU;
    private List<SFVDNode> curSolution;
    private Set<SFVDNode> curSolutionSet;
    List<SFVDNode> sfvdNodes;
    List<SFVDNode> sortSFVDNodes;
    private HashMap<SFVDNode, List<SFVDNode>> groupItemHashMap;
    private double finalJS = 0d;
    private Object[][] originalDT;
    private Object[][] csDT;
    private Integer strategy;
    private Map<Integer, SFVDNode> fHash;
    private double delta = 0d;
    private int csType = 0;
    private JSHistory jsHistory = new JSHistory();
    private Set<Object> dvSet;
    private List<Object> dvList;
    private Object curDv;
    private Map<Object, Object[][]> csDTMap;
    private Map<Pair<Object, Integer>, Double> csHF;
    private Map<Integer, Double> hF;
    private Map<ThreeItem<Object, Integer, Integer>, Double> csHFAndF;
    private Map<Pair<Integer, Integer>, Double> cs2FAndF;
    private Map<ThreeItem<Object, Integer, Integer>, Double> csFFSU;
    private Map<ThreeItem<Object, Integer, Integer>, Double> cs2FFSU;

    private Random random = new Random();

    public PGS() {
        this.csHF = new HashMap<>();
        this.csHFAndF = new HashMap<>();
        this.ffSU = new HashMap<>();
        this.csFFSU = new HashMap<>();
    }

    /**
     * filter sfvdNodes and generate fHash
     */
    public void genFHash() {
        this.fHash = new HashMap<>();
        for (SFVDNode sfvdNode : sfvdNodes) {
            this.fHash.put(sfvdNode.getFId(), sfvdNode);
        }
    }

    /**
     * sort FVDNode list by su(f, d) in descending order
     */
    public void sortFNodeListBySUFD() {
        if (sortSFVDNodes == null || sortSFVDNodes.isEmpty()) {
            sortSFVDNodes = new ArrayList<>(this.sfvdNodes.stream().filter(sfvdNode -> sfvdNode.getSuFD() >= delta).toList());
        } else {
            sortSFVDNodes.clear();
            sortSFVDNodes.addAll(this.sfvdNodes.stream().filter(sfvdNode -> sfvdNode.getSuFD() >= delta).toList());
        }
        sortSFVDNodes.sort((a, b) -> Double.compare(b.getSuFD(), a.getSuFD()));
    }

    /**
     * Greedy Predominant Grouping
     */
    public void gpgg() {
        log.info("GPGG start");
        sortFNodeListBySUFD();
        groups = new ArrayList<>();
        groupItemHashMap = new HashMap<>();

        List<SFVDNode> tmpGroup;
        List<SFVDNode> sortSFVDNodesCopy = new ArrayList<>(this.sortSFVDNodes);
        for (int i = 0; i < sortSFVDNodesCopy.size(); ) {
            SFVDNode pFeature = sortSFVDNodesCopy.get(0);
            tmpGroup = new ArrayList<>();
            tmpGroup.add(pFeature);
            groupItemHashMap.put(pFeature, tmpGroup);
            sortSFVDNodesCopy.remove(0);

            for (int j = 0; j < sortSFVDNodesCopy.size(); ) {
                // su(f_i, f_j) >= su(f_j, d)
                if (getCsSUFF(pFeature.getFId(), sortSFVDNodesCopy.get(j).getFId()) >= sortSFVDNodesCopy.get(j).getSuFD()) {
                    tmpGroup.add(sortSFVDNodesCopy.get(j));
                    groupItemHashMap.put(sortSFVDNodesCopy.get(j), tmpGroup);
                    sortSFVDNodesCopy.remove(sortSFVDNodesCopy.get(j));
                } else {
                    j++;
                }
            }

            if (!tmpGroup.isEmpty()) {
                groups.add(tmpGroup);
            }
        }

        curSolution = new ArrayList<>();
        for (List<SFVDNode> group : groups) {
            curSolution.add(group.get(0));
        }

        log.info("group: " + curSolution.stream().map(SFVDNode::getFId).toList());
        log.info("groups_size: " + groups.size());
        log.info("GPGG end");
    }

    /**
     * Greedy Predominant Grouping
     */
    public void gpggCsRandom() {
        log.info("GPGG start");
        sortFNodeListBySUFD();
        groups = new ArrayList<>();
        groupItemHashMap = new HashMap<>();

        List<SFVDNode> tmpGroup;
        List<SFVDNode> sortSFVDNodesCopy = new ArrayList<>(this.sortSFVDNodes);
        for (int i = 0; i < sortSFVDNodesCopy.size(); ) {
            SFVDNode pFeature = sortSFVDNodesCopy.get(0);
            tmpGroup = new ArrayList<>();
            tmpGroup.add(pFeature);
            groupItemHashMap.put(pFeature, tmpGroup);
            sortSFVDNodesCopy.remove(0);

            for (int j = 0; j < sortSFVDNodesCopy.size(); ) {
                // su(f_i, f_j) >= su(f_j, d)
                if (getRandomSUFFInAllClass(pFeature.getFId(), sortSFVDNodesCopy.get(j).getFId()) >= sortSFVDNodesCopy.get(j).getSuFD()) {
                    tmpGroup.add(sortSFVDNodesCopy.get(j));
                    groupItemHashMap.put(sortSFVDNodesCopy.get(j), tmpGroup);
                    sortSFVDNodesCopy.remove(sortSFVDNodesCopy.get(j));
                } else {
                    j++;
                }
            }

            if (!tmpGroup.isEmpty()) {
                groups.add(tmpGroup);
            }
        }

        curSolution = new ArrayList<>();
        for (List<SFVDNode> group : groups) {
            curSolution.add(group.get(0));
        }

        log.info("group: " + curSolution.stream().map(SFVDNode::getFId).toList());
        log.info("groups_size: " + groups.size());
        log.info("GPGG end");
    }

    /**
     * pFeatures as the base solution
     */
    public void genResAsPFeature() {
        curSolution = groups.stream().map(group -> group.get(0)).toList();
    }

    /**
     * get su(f_i, f_j)
     *
     * @param a feature index
     * @param b feature index
     * @return su(f_i, f_j)
     */
    public double getCsSUFF(int a, int b) {
        if (this.ffSU == null) {
            ffSU = new HashMap<>();
        }
        // maintain symmetry
        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }
        Pair<Integer, Integer> key = new Pair<>(a, b);

        // if cache hit
        if (ffSU.containsKey(key)) {
            return ffSU.get(key);
        }

        // if cache miss
        double v;
        double hFAndF;
        if (csType == 0) {
            hFAndF = HFunctions.HXAndY(originalDT, a, b);
            v = HFunctions.SU(
                    this.fHash.get(a).getSHF(),
                    this.fHash.get(b).getSHF(),
                    hFAndF);
        } else if (csType == 2){
            v = getCs2SUFF(curDv, a, b);
        }else if (csType == 3) {
            v = getCsSUFF(curDv, a, b);
        } else if (csType == 4) {
            v = getMinSUFFInAllClass(a, b);
        } else if (csType == 5) {
            v = getMaxSUFFInAllClass(a, b);
        } else if (csType == 6) {
            v = getRandomSUFFInAllClass(a, b);
        } else {
            log.error("csType error");
            return 0d;
        }
        this.ffSU.put(key, v);
        return v;
    }

    /**
     * 获取特定类上F的信息熵
     *
     * @param dV 特定类
     * @param c  F所在的列索引
     * @return 熵值
     */
    public double getCsHF(Object dV, int c) {
        Pair<Object, Integer> key = new Pair<>(dV, c);
        if (csHF.containsKey(key)) {
            return csHF.get(key);
        }
        double v = HFunctions.HX(csDTMap.get(dV), c);
        csHF.put(key, v);
        return v;
    }

    public double getHF(int c) {
        if (hF.containsKey(c)) {
            return hF.get(c);
        }

        double v = HFunctions.HX(originalDT, c);
        hF.put(c, v);
        return v;
    }

    /**
     * 获得特定类上F_a和F_b的联合熵
     *
     * @param dV 特定类
     * @param a  F_a所在的列索引
     * @param b  F_b所在的列索引
     * @return 联合熵
     */
    public double getCsHFAndF(Object dV, int a, int b) {
        ThreeItem<Object, Integer, Integer> key = new ThreeItem<>(dV, a, b);
        if (csHFAndF.containsKey(key)) {
            return csHFAndF.get(key);
        }
        double v = HFunctions.HXAndY(csDTMap.get(dV), a, b);
        csHFAndF.put(key, v);
        return v;
    }

    public double getCs2HFAndF(Object dV, int a, int b) {
        Pair<Integer, Integer> key = new Pair<>(a, b);
        if (cs2FAndF.containsKey(key)) {
            return cs2FAndF.get(key);
        }
        double v = HFunctions.HXAndY(csDTMap.get(dV), originalDT, a, b);
        cs2FAndF.put(key, v);
        return v;
    }

    /**
     * 获得特定类熵F_a和F_b的SU
     *
     * @param dV 特定类
     * @param a  F_a所在的列索引
     * @param b  F_b所在的列索引
     * @return F_a和F_b的SU
     */
    public double getCsSUFF(Object dV, int a, int b) {
        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }
        ThreeItem<Object, Integer, Integer> key = new ThreeItem<>(dV, a, b);
        if (csFFSU.containsKey(key)) {
            return csFFSU.get(key);
        }

        double hFA = getCsHF(dV, a);
        double hFB = getCsHF(dV, b);
        double hFAndF = getCsHFAndF(dV, a, b);
        double v = HFunctions.SU(hFA, hFB, hFAndF);
        this.csFFSU.put(key, v);
        return v;
    }


    public double getCs2SUFF(Object dV, int a, int b) {
        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        ThreeItem<Object, Integer, Integer> key = new ThreeItem<>(dV, a, b);
        if (csFFSU.containsKey(key)) {
            return csFFSU.get(key);
        }

        double hFA = getHF(a);
        double hFB = getHF(b);
        double hFCSA = getCsHF(dV, a);
        double hFCSB = getCsHF(dV, b);
        double hCsAAndB = getCs2HFAndF(dV, a, b);
        double hCsBAndA = getCs2HFAndF(dV, a, b);
        double su1 = HFunctions.SU(hFCSA, hFB, hCsAAndB);
        double su2 = HFunctions.SU(hFCSB, hFA, hCsBAndA);
        double v = Math.sqrt(su1 * su1 + su2 * su2);
        this.csFFSU.put(key, v);
        return v;
    }

    /**
     * 获得全部类上F_a和F_b的SU的最小值
     *
     * @param a F_a所在的列索引
     * @param b F_b所在的列索引
     * @return 全部类上F_a和F_b的SU的最小值
     */
    public double getMinSUFFInAllClass(int a, int b) {
        double minV = 0d;
        double curV;
        for (Object dv : dvSet) {
            curV = getCsSUFF(dv, a, b);
            if (minV == 0d) {
                minV = curV;
            }
            minV = Math.min(minV, curV);
        }
        return minV;
    }

    /**
     * 获得全部类上F_a和F_b的SU的最大值
     *
     * @param a F_a所在的列索引
     * @param b F_b所在的列索引
     * @return 全部类上F_a和F_b的SU的最大值
     */
    public double getMaxSUFFInAllClass(int a, int b) {
        double maxV = 0d;
        double curV;
        for (Object dv : dvSet) {
            curV = getCsSUFF(dv, a, b);
            if (maxV == 0d) {
                maxV = curV;
            }
            maxV = Math.max(maxV, curV);
        }
        return maxV;
    }

    /**
     * 获得全部类上随机一个F_a和F_b的SU值
     *
     * @param a F_a所在的列索引
     * @param b F_b所在的列索引
     * @return 全部类上随机一个F_a和F_b的SU值
     */
    public double getRandomSUFFInAllClass(int a, int b) {
        if (dvList == null || dvList.isEmpty()) {
            dvList = new ArrayList<>(dvSet);
        }
        return getCsSUFF(dvList.get(random.nextInt(dvList.size())), a, b);
    }

    /**
     * 目标函数
     *
     * @param s 初始解
     * @return 目标函数值
     */
    public double j(List<SFVDNode> s) {
        double SSUFD = 0d;
        double SSUFF = 0d;

        // calculate SSUFD
        for (SFVDNode fNode : s) {
            SSUFD += fNode.getSuFD();
        }

        // calculate SSUFF
        for (int i = 0; i < s.size(); i++) {
            for (int j = 0; j < s.size(); j++) {
                if (j != i) {
                    SSUFF += getCsSUFF(s.get(i).getFId(), s.get(j).getFId());
                }
            }
        }
        jsHistory.SSUFD = SSUFD;
        jsHistory.SSUFF = SSUFF;
        return SSUFD / Math.sqrt(s.size() + 2d * SSUFF);
    }

    // JOptimal 和 J必须配合使用，先用J初始化jsHistory，然后用JOptimal加速计算目标函数
    public double jOptimal(List<SFVDNode> solution, SFVDNode node) {
        double newSSUFD = jsHistory.SSUFD + node.getSuFD();
        double newSSUFF = jsHistory.SSUFF;
        for (SFVDNode fNode : solution) {
            newSSUFF += 2d * getCsSUFF(fNode.getFId(), node.getFId());
        }
        return newSSUFD / Math.sqrt(solution.size() + 1d + 2d * newSSUFF);
    }


    /**
     * 仅使用组长进行前向搜索
     * JS目标函数的suff使用Grouping第二比较条件中使用的
     */
    public void sfsOnlyInGroup() {
        log.info("groups: " + groups.size());
        log.info("sfs only in group");

        this.curSolution = new ArrayList<>();
        ArrayList<SFVDNode> pfSet = new ArrayList<>();
        ArrayList<SFVDNode> candidateNodes;
        for (List<SFVDNode> g : groups) {
            pfSet.add(g.get(0));
        }

        candidateNodes = new ArrayList<>(pfSet);
        curSolution.add(pfSet.get(0));
        candidateNodes.remove(0);

        double maxJ, curJ;
        SFVDNode selectNode;
        while (curSolution.size() != pfSet.size()) {
            maxJ = j(curSolution);
            selectNode = null;
            for (SFVDNode node : candidateNodes) {
                curJ = jOptimal(curSolution, node);
                if (curJ > maxJ) {
                    maxJ = curJ;
                    selectNode = node;
                }
            }
            if (selectNode != null) {
                curSolution.add(selectNode);
                candidateNodes.remove(selectNode);
            } else {
                break;
            }
        }
        log.info("sfs only in group end!");
    }

    /**
     * 目标函数
     *
     * @param s 初始解
     * @return 目标函数值
     */
    public double csJ(List<SFVDNode> s) {
        double SSUFD = 0d;
        double SSUFF = 0d;

        // calculate SSUFD
        for (SFVDNode fNode : s) {
            SSUFD += fNode.getSuFD();
        }

        // calculate SSUFF
        for (int i = 0; i < s.size(); i++) {
            for (int j = 0; j < s.size(); j++) {
                if (j != i) {
                    SSUFF += getCsSUFF(curDv, s.get(i).getFId(), s.get(j).getFId());
                }
            }
        }
        jsHistory.SSUFD = SSUFD;
        jsHistory.SSUFF = SSUFF;
        return SSUFD / Math.sqrt(s.size() + 2d * SSUFF);
    }

    // JOptimal 和 J必须配合使用，先用J初始化jsHistory，然后用JOptimal加速计算目标函数
    public double csJOptimal(List<SFVDNode> solution, SFVDNode node) {
        double newSSUFD = jsHistory.SSUFD + node.getSuFD();
        double newSSUFF = jsHistory.SSUFF;
        for (SFVDNode fNode : solution) {
            newSSUFF += 2d * getCsSUFF(curDv, fNode.getFId(), node.getFId());
        }
        return newSSUFD / Math.sqrt(solution.size() + 1d + 2d * newSSUFF);
    }


    /**
     * 仅使用组长进行前向搜索，使用当前dv的suff
     */
    public void csSfsOnlyInGroup() {
        log.info("groups: " + groups.size());
        log.info("sfs only in group");

        this.curSolution = new ArrayList<>();
        ArrayList<SFVDNode> pfSet = new ArrayList<>();
        ArrayList<SFVDNode> candidateNodes;
        for (List<SFVDNode> g : groups) {
            pfSet.add(g.get(0));
        }

        candidateNodes = new ArrayList<>(pfSet);
        curSolution.add(pfSet.get(0));
        candidateNodes.remove(0);

        double maxJ, curJ;
        SFVDNode selectNode;
        while (curSolution.size() != pfSet.size()) {
            maxJ = csJ(curSolution);
            selectNode = null;
            for (SFVDNode node : candidateNodes) {
                curJ = csJOptimal(curSolution, node);
                if (curJ > maxJ) {
                    maxJ = curJ;
                    selectNode = node;
                }
            }
            if (selectNode != null) {
                curSolution.add(selectNode);
                candidateNodes.remove(selectNode);
            } else {
                break;
            }
        }
        log.info("sfs only in group end!");
    }

    /**
     * 仅使用组长进行前向搜索，使用Grouping第二比较条件中使用的suff
     * 但是对J(S)增量提出要求
     */
    public void sfsIncOnlyInGroup(double thr) {
        log.info("groups: " + groups.size());
        log.info("sfs only in group");

        this.curSolution = new ArrayList<>();
        ArrayList<SFVDNode> pfSet = new ArrayList<>();
        ArrayList<SFVDNode> candidateNodes;
        for (List<SFVDNode> g : groups) {
            pfSet.add(g.get(0));
        }

        candidateNodes = new ArrayList<>(pfSet);
        curSolution.add(pfSet.get(0));
        candidateNodes.remove(0);

        double maxJ, curJ, lastSolutionJ;
        SFVDNode selectNode;
        while (curSolution.size() != pfSet.size()) {
            lastSolutionJ = maxJ = csJ(curSolution);
            selectNode = null;
            for (SFVDNode node : candidateNodes) {
                curJ = csJOptimal(curSolution, node);
                if (curJ > maxJ && curJ - lastSolutionJ > thr) {
                    maxJ = curJ;
                    selectNode = node;
                }
            }
            if (selectNode != null) {
                curSolution.add(selectNode);
                candidateNodes.remove(selectNode);
            } else {
                break;
            }
        }
        log.info("sfs only in group end!");
    }


    /**
     * sequential forward search
     */
    public void sfs() {
        this.curSolution = new ArrayList<>();
        System.out.println("groups: " + groups.size());
        for (List<SFVDNode> g : groups) {
            curSolution.add(g.get(0));
        }
        curSolution = sfs(curSolution);
    }

    /**
     * sequential forward search
     *
     * @param solution solution
     * @return a new solution
     */
    public List<SFVDNode> sfs(List<SFVDNode> solution) {
        log.debug("SFS search start");
        List<SFVDNode> candidateNodes = new ArrayList<>(sfvdNodes);

        SFVDNode selectNode;
        double curJS;

        while (solution.size() != sfvdNodes.size()) {
            log.debug(String.format("%d/%d", candidateNodes.size(), solution.size()));
            candidateNodes.removeAll(solution);
            double maxJS = j(solution);
            selectNode = null;
            for (SFVDNode sfvdNode : candidateNodes) {
                // 计算加入新元素的目标函数
                curJS = jOptimal(solution, sfvdNode);
                if (curJS > maxJS) {
                    maxJS = curJS;
                    selectNode = sfvdNode;
                }
            }
            // 如果选中了一个可以使得目标函数增大的元素，将其加入解中
            if (selectNode != null) {
                solution.add(selectNode);
                candidateNodes.remove(selectNode);
            } else {
                break;
            }
        }
        log.debug("SFS search end");
        return solution;
    }

    /**
     * sharking method
     *
     * @param solution a solution
     * @param k        number of sharking
     * @return a new solution
     */
    public List<SFVDNode> sharkingMethod(List<SFVDNode> solution, int k) {
        List<SFVDNode> sharkingSolution = new ArrayList<>(solution);
        Set<SFVDNode> sharkingSolutionSet = new HashSet<>(solution);
        List<SFVDNode> solutionCopy = new ArrayList<>(sharkingSolution);

        for (int i = 0; i < k; i++) {
            int j = random.nextInt(sortSFVDNodes.size());
            sharkingSolution = new ArrayList<>(solutionCopy);
            if (j < solutionCopy.size()) {
                // 先删除，如果该元素有其他组员，则从其他组员中随机选择一个替换
                SFVDNode tmpFNode = solutionCopy.get(j);
                sharkingSolution.remove(tmpFNode);
                List<SFVDNode> nodeGroup = groupItemHashMap.get(tmpFNode);

                List<SFVDNode> tmpGroup = new ArrayList<>(nodeGroup);
                tmpGroup.remove(tmpFNode);
                if (!tmpGroup.isEmpty()) {
                    SFVDNode newFNode = tmpGroup.get(random.nextInt(tmpGroup.size()));
                    if (!sharkingSolutionSet.contains(newFNode)) {
                        sharkingSolution.add(newFNode);
                        sharkingSolutionSet.add(newFNode);
                    }
                }
            } else {
                List<SFVDNode> other = new ArrayList<>(sortSFVDNodes);
                other.removeAll(sharkingSolution);
                SFVDNode newFNode = other.get(random.nextInt(other.size()));
                sharkingSolution.add(newFNode);
                sharkingSolutionSet.add(newFNode);
            }
            solutionCopy = new ArrayList<>(sharkingSolution);
        }
        return sharkingSolution;
    }

    /**
     * variable neighborhood search, the solution is stored in (res)
     *
     * @param kMax kMax
     * @param cr   cr
     */
    public void vns(int kMax, int cr) {
        if (groups == null || groups.isEmpty()) {
            log.info("group is empty, can't search");
            return;
        }
        log.info("vns search start");
        curSolution = new ArrayList<>();
        curSolutionSet = new HashSet<>();
        // Init solution
        for (List<SFVDNode> g : groups) {
            curSolution.add(g.get(0));
            curSolutionSet.add(g.get(0));
        }

        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < cr; i++) {
            for (int j = 0; j < kMax; j++) {
                List<SFVDNode> smSolution = sharkingMethod(curSolution, j);
                List<SFVDNode> sfsSolution = sfs(smSolution);
                double jSfsSolution = j(sfsSolution);
                double jCurSolution = j(curSolution);
                if (jSfsSolution > jCurSolution) {
                    curSolution = sfsSolution;
                    this.finalJS = jSfsSolution;
                    j = 0;
                }
                stopWatch.getTime();
                log.info("vns iter: " + j + ", time: " + stopWatch.getTime());
            }
        }

        log.info("vns search end");
    }
}
