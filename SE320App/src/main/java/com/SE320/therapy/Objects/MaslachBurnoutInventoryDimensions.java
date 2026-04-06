package com.SE320.therapy.objects;

public class MaslachBurnoutInventoryDimensions {

    private double emotionalExhaustion;
    private double depersonalization;
    private double personalAccomplishment;

    public MaslachBurnoutInventoryDimensions() {
        this.emotionalExhaustion = 0.0;
        this.depersonalization = 0.0;
        this.personalAccomplishment = 0.0;
    }

    public MaslachBurnoutInventoryDimensions(double emotionalExhaustion,
                                             double depersonalization,
                                             double personalAccomplishment) {
        this.emotionalExhaustion = emotionalExhaustion;
        this.depersonalization = depersonalization;
        this.personalAccomplishment = personalAccomplishment;
    }

    public double getEmotionalExhaustion() {
        return emotionalExhaustion;
    }

    public void setEmotionalExhaustion(double emotionalExhaustion) {
        this.emotionalExhaustion = emotionalExhaustion;
    }

    public double getDepersonalization() {
        return depersonalization;
    }

    public void setDepersonalization(double depersonalization) {
        this.depersonalization = depersonalization;
    }

    public double getPersonalAccomplishment() {
        return personalAccomplishment;
    }

    public void setPersonalAccomplishment(double personalAccomplishment) {
        this.personalAccomplishment = personalAccomplishment;
    }
}
