package com.example.garbageremover.Model;

public class PerformModel {
    private String PerformImageUri;
    private String PerformerName;
    private String PerformerSurname;
    private String PerformerDescription;
    private String PerformDate;

    public PerformModel(){

    }

    public PerformModel(String performImageUri, String performerName, String performerSurname, String performerDescription, String performDate) {
        PerformImageUri = performImageUri;
        PerformerName = performerName;
        PerformerSurname = performerSurname;
        PerformerDescription = performerDescription;
        PerformDate = performDate;
    }

    public String getPerformImageUri() {
        return PerformImageUri;
    }

    public void setPerformImageUri(String performImageUri) {
        PerformImageUri = performImageUri;
    }

    public String getPerformerName() {
        return PerformerName;
    }

    public void setPerformerName(String performerName) {
        PerformerName = performerName;
    }

    public String getPerformerSurname() {
        return PerformerSurname;
    }

    public void setPerformerSurname(String performerSurname) {
        PerformerSurname = performerSurname;
    }

    public String getPerformerDescription() {
        return PerformerDescription;
    }

    public void setPerformerDescription(String performerDescription) {
        PerformerDescription = performerDescription;
    }

    public String getPerformDate() {
        return PerformDate;
    }

    public void setPerformDate(String performDate) {
        PerformDate = performDate;
    }
}
