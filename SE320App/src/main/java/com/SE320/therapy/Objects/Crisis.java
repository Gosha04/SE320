package com.SE320.therapy.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Crisis {

    private UUID ownerUserId;
    private List<String> warningSignRecognition;
    private List<String> deescalationTechniques;
    private List<String> safetyPlanningSteps;
    private List<String> emergencyResources;

    public Crisis() {
        this.ownerUserId = null;
        this.warningSignRecognition = new ArrayList<>();
        this.deescalationTechniques = new ArrayList<>();
        this.safetyPlanningSteps = new ArrayList<>();
        this.emergencyResources = new ArrayList<>();
    }

    public Crisis(List<String> warningSignRecognition,
                  List<String> deescalationTechniques,
                  List<String> safetyPlanningSteps,
                  List<String> emergencyResources) {
        this(null, warningSignRecognition, deescalationTechniques, safetyPlanningSteps, emergencyResources);
    }

    public Crisis(UUID ownerUserId,
                  List<String> warningSignRecognition,
                  List<String> deescalationTechniques,
                  List<String> safetyPlanningSteps,
                  List<String> emergencyResources) {
        this.ownerUserId = ownerUserId;
        setWarningSignRecognition(warningSignRecognition);
        setDeescalationTechniques(deescalationTechniques);
        setSafetyPlanningSteps(safetyPlanningSteps);
        setEmergencyResources(emergencyResources);
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerUserId != null && ownerUserId.equals(userId);
    }

    public boolean canBeViewedBy(UUID accessorUserId, UserType accessorUserType) {
        if (accessorUserType == null || ownerUserId == null) {
            return false;
        }

        return switch (accessorUserType) {
            case DOCTOR -> true;
            case PATIENT -> ownerUserId.equals(accessorUserId);
            case ADMIN -> false;
        };
    }

    public List<String> getWarningSignRecognition() {
        return Collections.unmodifiableList(warningSignRecognition);
    }

    public void setWarningSignRecognition(List<String> warningSignRecognition) {
        if (warningSignRecognition != null) {
            this.warningSignRecognition = new ArrayList<>(warningSignRecognition);
        } else {
            this.warningSignRecognition = new ArrayList<>();
        }
    }

    public List<String> getDeescalationTechniques() {
        return Collections.unmodifiableList(deescalationTechniques);
    }

    public void setDeescalationTechniques(List<String> deescalationTechniques) {
        if (deescalationTechniques != null) {
            this.deescalationTechniques = new ArrayList<>(deescalationTechniques);
        } else {
            this.deescalationTechniques = new ArrayList<>();
        }
    }

    public List<String> getSafetyPlanningSteps() {
        return Collections.unmodifiableList(safetyPlanningSteps);
    }

    public void setSafetyPlanningSteps(List<String> safetyPlanningSteps) {
        if (safetyPlanningSteps != null) {
            this.safetyPlanningSteps = new ArrayList<>(safetyPlanningSteps);
        } else {
            this.safetyPlanningSteps = new ArrayList<>();
        }
    }

    public List<String> getEmergencyResources() {
        return Collections.unmodifiableList(emergencyResources);
    }

    public void setEmergencyResources(List<String> emergencyResources) {
        if (emergencyResources != null) {
            this.emergencyResources = new ArrayList<>(emergencyResources);
        } else {
            this.emergencyResources = new ArrayList<>();
        }
    }
}
