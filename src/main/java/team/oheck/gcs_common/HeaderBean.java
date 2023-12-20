package team.oheck.gcs_common;

import lombok.Data;

@Data
public class HeaderBean {
    /**
     * 文件头，不适配class specific
     */
    public static String[] headerNoCS = new String[]{
            "dataset",
            "resSize",
            "groupSize",
            "resOutline",
            "pFeatureOutline",
            "groupOutline",
            "resDetail",
            "groupDetail",
            "sfvdNodes",
            "suffDetail",
            "groupTime",
            "searchTime",
            "finalJS",
    };

    /**
     * result header for FFSG
     */
    public static String[] headerWithFFSG = new String[]{
            "dataset",
            "resSize",
            "groupSize",
            "resOutline",
            "pFeatureOutline",
            "groupOutline",
            "resDetail",
            "groupDetail",
            "sfvdNodes",
            "suffDetail",
            "groupTime",
            "searchTime",
            "finalJS",
    };

    /**
     * result header for class specific
     */
    public static String[] headerWithCS = new String[]{
            "dataset",
            "label",
            "resSize",
            "groupSize",
            "resOutline",
            "pFeatureOutline",
            "groupOutline",
            "resDetail",
            "groupDetail",
            "sfvdNodes",
            "suffDetail",
            "groupTime",
            "searchTime",
            "finalJS",
    };

    /**
     * class specific Info
     */
    public static String[] classSpecificInfo = new String[]{
            "dataset",
            "resSize",
            "res"
    };

    public static String[] classSpecificFChange = new String[] {
            "dataset",
            "class",
            "class_idx",
            "change_f_count",
            "all_f_count",
            "change_rate"
    };
}
