package com.mbaxajl3.medicalgo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.models.Category;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JSONControllerTest {
    JSONController jsonController;
    List<Category> categories;
    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        jsonController = new JSONController(activityRule.getActivity());
        categories = jsonController.getCategories();
    }

    @Test
    public void getCategories_Correct() {
        assertThat(categories.size()).isEqualTo(6);
    }

    @Test
    public void getCategoriesByName_Correct() {
        assertThat(jsonController.getCategoryByName("Airway Management").getName()).isEqualTo("Airway Management");
    }

    @Test
    public void getCategoriesByName_Incorrect() {
        assertThat(jsonController.getCategoryByName("Airwa Management")).isNull();
    }

    @Test
    public void getCategoriesById_Correct() {
        assertThat(jsonController.getCategoryById(0).getId()).isEqualTo(0);
    }

    @Test
    public void getCategoriesById_Incorrect() {
        assertThat(jsonController.getCategoryById(10)).isNull();
    }

    @Test
    public void getAlgorithmByName_Correct() throws Exception {
        assertThat(jsonController.getAlgorithmByName("Tracheostomy")).isNotNull();
    }

    @Test
    public void getAlgorithm_Null() throws Exception {
        assertThat(jsonController.getAlgorithm(null)).isNull();
    }

    @Test
    public void getAlgorithmMetadatasByCategory_Correct() throws Exception {
        List<AlgorithmMetadata> algorithmMetadata = jsonController.getAlgorithmMetadatasByCategory(categories.get(0));
        assertThat(algorithmMetadata.size()).isEqualTo(2);
    }

    @Test
    public void getAllAlgorithmMetadatas_Correct() throws Exception {
        List<AlgorithmMetadata> algorithmMetadata = jsonController.getListOfAllAlgorithmsMetadata();
        assertThat(algorithmMetadata.size()).isEqualTo(2);
    }

    @Test
    public void getAlgorithmMetadataByName_Correct() throws Exception {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataByName("Tracheostomy");
        assertThat(algorithmMetadata.getAlgorithmName()).isEqualTo("Tracheostomy");
    }

    @Test
    public void getAlgorithmMetadataByName_Incorrect() throws Exception {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataByName("aa");
        assertThat(algorithmMetadata).isNull();
    }

    @Test
    public void getAlgorithmMetadataById_Correct() throws Exception {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataById(0);
        assertThat(algorithmMetadata.getAlgorithmId()).isEqualTo(0);
    }

    @Test
    public void getAlgorithmMetadataById_Incorrect() throws Exception {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataById(4);
        assertThat(algorithmMetadata).isNull();
    }

    @Test
    public void getAlgorithmByMetadata_Correct() throws Exception {
        AlgorithmMetadata algorithmMetadata = jsonController.getAlgorithmMetadataByName("Tracheostomy");
        assertThat(algorithmMetadata.getAlgorithmName()).isEqualTo("Tracheostomy");
    }

    @Test
    public void getAlgorithmByFilename_Correct() throws Exception {
        Algorithm algorithm = jsonController.getAlgorithmByFileName("tracheostomy.json");
        assertThat(algorithm).isNotNull();
    }

    @Test
    public void getAlgorithmByFilename_Incorrect() throws Exception {
        Algorithm algorithm = jsonController.getAlgorithmByFileName("asdsa");
        assertThat(algorithm).isNull();
    }

    @Test
    public void getWords_NotEmpty() throws Exception {
        assertThat(jsonController.getWords()).isNotEmpty();
    }

}
