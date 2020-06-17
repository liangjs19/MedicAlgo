package com.mbaxajl3.medicalgo.models;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class AlgorithmMetadataTest {
    private AlgorithmMetadata algorithmMetadata;

    @Before
    public void setUp() throws Exception {
        algorithmMetadata = new AlgorithmMetadata("algorithm", 0, 0, "filename", "imagePath");
    }

    @Test
    public void getAlgorithmName() {
        assertThat(algorithmMetadata.getAlgorithmName()).isEqualTo("algorithm");
    }

    @Test
    public void setAlgorithmName() {
        algorithmMetadata.setAlgorithmName("a");
        assertThat(algorithmMetadata.getAlgorithmName()).isEqualTo("a");
    }

    @Test
    public void getAlgorithmId() {
        assertThat(algorithmMetadata.getAlgorithmId()).isEqualTo(0);
    }

    @Test
    public void setAlgorithmId() {
        algorithmMetadata.setAlgorithmId(1);
        assertThat(algorithmMetadata.getAlgorithmId()).isEqualTo(1);
    }

    @Test
    public void getCategoryId() {
        assertThat(algorithmMetadata.getCategoryId()).isEqualTo(0);
    }

    @Test
    public void setCategoryId() {
        algorithmMetadata.setCategoryId(1);
        assertThat(algorithmMetadata.getCategoryId()).isEqualTo(1);
    }

    @Test
    public void getFilename() {
        assertThat(algorithmMetadata.getFilename()).isEqualTo("filename");
    }

    @Test
    public void setFilename() {
        algorithmMetadata.setFilename("a");
        assertThat(algorithmMetadata.getFilename()).isEqualTo("a");
    }

    @Test
    public void getImagePath() {
        assertThat(algorithmMetadata.getImagePath()).isEqualTo("imagePath");
    }

    @Test
    public void setImagePath() {
        algorithmMetadata.setImagePath("a");
        assertThat(algorithmMetadata.getImagePath()).isEqualTo("a");
    }
}
