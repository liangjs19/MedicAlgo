package com.mbaxajl3.medicalgo.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class CategoryTest {
    private Category category;

    @Before
    public void setUp() throws Exception {
        category = new Category(0, "category", "iconPath", null);
    }

    @Test
    public void categoryWithAlgorithm_SizeShouldBe0() {
        List<AlgorithmMetadata> algorithmMetadata = new ArrayList<>();
        category.setAlgorithms(algorithmMetadata);
        Category cat = new Category(0, "category", "iconPath", algorithmMetadata);
        assertThat(cat.getNoOfAlgos()).isEqualTo(0);
    }

    @Test
    public void getName() {
        assertThat(category.getName()).isEqualTo("category");
    }

    @Test
    public void setName() {
        category.setName("a");
        assertThat(category.getName()).isEqualTo("a");
    }

    @Test
    public void getId() {
        assertThat(category.getId()).isEqualTo(0);
    }

    @Test
    public void setId() {
        category.setId(1);
        assertThat(category.getId()).isEqualTo(1);
    }

    @Test
    public void getNoOfAlgos() {
        assertThat(category.getNoOfAlgos()).isEqualTo(0);
    }

    @Test
    public void getAlgorithms() {
        assertThat(category.getAlgorithms()).isNull();
    }

    @Test
    public void setAlgorithms() {
        List<AlgorithmMetadata> algorithmMetadata = new ArrayList<>();
        category.setAlgorithms(algorithmMetadata);
        assertThat(category.getAlgorithms()).isNotNull();
    }

    @Test
    public void getIconPath() {
        assertThat(category.getIconPath()).isEqualTo("iconPath");
    }

    @Test
    public void setIconPath() {
        category.setIconPath("a");
        assertThat(category.getIconPath()).isEqualTo("a");
    }
}
