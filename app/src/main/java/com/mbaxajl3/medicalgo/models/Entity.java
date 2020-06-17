package com.mbaxajl3.medicalgo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity implements Serializable {
    private String type;
    private List<String> value = new ArrayList<>();

    public Entity(String type) {
        this.type = type;
    }

    public Entity(String type, List<String> value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public String getLastValue() {
        if (value != null && !value.isEmpty()) {
            return value.get(value.size() - 1);
        } else {
            return "";
        }
    }
}
