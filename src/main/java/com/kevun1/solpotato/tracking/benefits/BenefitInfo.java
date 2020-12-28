package com.kevun1.solpotato.tracking.benefits;

public class BenefitInfo {
    public String type;
    public String name;
    public double value;
    public double threshold;

    public BenefitInfo(String type, String name, double value, double threshold) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.threshold = threshold;
    }
}
