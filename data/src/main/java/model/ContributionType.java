package model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tkaczenko on 26.03.17.
 */
public enum ContributionType {
    @SerializedName("0")
    Demand(0, "до востребования"),

    @SerializedName("1")
    urgent(1, "срочный"),

    @SerializedName("2")
    current(2, "расчетный"),

    @SerializedName("3")
    savings(3, "накопительный"),

    @SerializedName("4")
    savings2(4, "сберегательный"),

    @SerializedName("5")
    metal(5, "металлический");

    private static final Map<Integer, ContributionType> lookup = Collections.unmodifiableMap(initializeMapping());
    private int code;
    private String description;

    ContributionType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private static Map<Integer, ContributionType> initializeMapping() {
        Map<Integer, ContributionType> map = new HashMap<>();
        for (ContributionType v : ContributionType.values()) {
            map.put(v.getCode(), v);
        }
        return map;
    }

    public static ContributionType get(int code) {
        return lookup.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
