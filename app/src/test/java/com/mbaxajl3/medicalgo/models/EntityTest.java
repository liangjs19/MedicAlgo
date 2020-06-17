package com.mbaxajl3.medicalgo.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class EntityTest {
    Entity entity1;
    Entity entity2;

    @Before
    public void setUp() throws Exception {
        entity1 = new Entity("type");
        entity2 = new Entity("type", null);
    }

    @Test
    public void getType() {
        assertThat(entity1.getType()).isEqualTo("type");
    }

    @Test
    public void setType() {
        entity1.setType("a");
        assertThat(entity1.getType()).isEqualTo("a");
    }

    @Test
    public void getValue() {
        assertThat(entity2.getValue()).isNull();
    }

    @Test
    public void setValue() {
        List<String> values = new ArrayList<>();
        entity1.setValue(values);
        assertThat(entity1.getValue()).isNotNull();
    }

    @Test
    public void getLastValue_ShouldReturnB() {
        List<String> values = new ArrayList<>();
        values.add("a");
        values.add("b");
        entity1.setValue(values);
        assertThat(entity1.getLastValue()).isEqualTo("b");
    }

    @Test
    public void getLastValue_Empty() {
        List<String> values = new ArrayList<>();
        entity1.setValue(values);
        assertThat(entity1.getLastValue()).isEqualTo("");
        assertThat(entity2.getLastValue()).isEqualTo("");

    }

}
