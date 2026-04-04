package com.SE320.therapy.objects;

public class ProgressPoint {

    private String label;
    private double value;

    // Doctor generated a la Achievement

    public ProgressPoint() {
    }

    public ProgressPoint(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
