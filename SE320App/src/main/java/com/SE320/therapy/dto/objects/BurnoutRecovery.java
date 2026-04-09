package com.SE320.therapy.dto.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Most of this is fluffed framework
// Limited medical info
public class BurnoutRecovery {

    private MaslachBurnoutInventoryDimensions maslachBurnoutInventoryDimensions;
    private List<String> recoveryStrategies;
    private List<String> workLifeBalanceTechniques;
    private List<String> boundarySetting;

    public BurnoutRecovery() {
        this.maslachBurnoutInventoryDimensions = new MaslachBurnoutInventoryDimensions();
        this.recoveryStrategies = new ArrayList<>();
        this.workLifeBalanceTechniques = new ArrayList<>();
        this.boundarySetting = new ArrayList<>();
    }

    public BurnoutRecovery(MaslachBurnoutInventoryDimensions maslachBurnoutInventoryDimensions,
                           List<String> recoveryStrategies,
                           List<String> workLifeBalanceTechniques,
                           List<String> boundarySetting) {
        if (maslachBurnoutInventoryDimensions != null) {
            this.maslachBurnoutInventoryDimensions = maslachBurnoutInventoryDimensions;
        } else {
            this.maslachBurnoutInventoryDimensions = new MaslachBurnoutInventoryDimensions();
        }

        if (recoveryStrategies != null) {
            this.recoveryStrategies = new ArrayList<>(recoveryStrategies);
        } else {
            this.recoveryStrategies = new ArrayList<>();
        }

        if (workLifeBalanceTechniques != null) {
            this.workLifeBalanceTechniques = new ArrayList<>(workLifeBalanceTechniques);
        } else {
            this.workLifeBalanceTechniques = new ArrayList<>();
        }

        if (boundarySetting != null) {
            this.boundarySetting = new ArrayList<>(boundarySetting);
        } else {
            this.boundarySetting = new ArrayList<>();
        }
    }

    public MaslachBurnoutInventoryDimensions getMaslachBurnoutInventoryDimensions() {
        return maslachBurnoutInventoryDimensions;
    }

    public void setMaslachBurnoutInventoryDimensions(MaslachBurnoutInventoryDimensions maslachBurnoutInventoryDimensions) {
        if (maslachBurnoutInventoryDimensions != null) {
            this.maslachBurnoutInventoryDimensions = maslachBurnoutInventoryDimensions;
        } else {
            this.maslachBurnoutInventoryDimensions = new MaslachBurnoutInventoryDimensions();
        }
    }

    public List<String> getRecoveryStrategies() {
        return Collections.unmodifiableList(recoveryStrategies);
    }

    public void setRecoveryStrategies(List<String> recoveryStrategies) {
        if (recoveryStrategies != null) {
            this.recoveryStrategies = new ArrayList<>(recoveryStrategies);
        } else {
            this.recoveryStrategies = new ArrayList<>();
        }
    }

    public List<String> getWorkLifeBalanceTechniques() {
        return Collections.unmodifiableList(workLifeBalanceTechniques);
    }

    public void setWorkLifeBalanceTechniques(List<String> workLifeBalanceTechniques) {
        if (workLifeBalanceTechniques != null) {
            this.workLifeBalanceTechniques = new ArrayList<>(workLifeBalanceTechniques);
        } else {
            this.workLifeBalanceTechniques = new ArrayList<>();
        }
    }

    public List<String> getBoundarySetting() {
        return Collections.unmodifiableList(boundarySetting);
    }

    public void setBoundarySetting(List<String> boundarySetting) {
        if (boundarySetting != null) {
            this.boundarySetting = new ArrayList<>(boundarySetting);
        } else {
            this.boundarySetting = new ArrayList<>();
        }
    }
}
