package com.mbaxajl3.medicalgo;

import com.mbaxajl3.medicalgo.controllers.NLUController;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class NLUControllerTest {

    @Test
    public void testParse_PatientIsBreathing_Correct() throws IOException, URISyntaxException, JSONException {
        JSONObject jsonObj = new JSONObject(readJSONToString("patient_is_breathing.json"));
        Pair<String, List<Entity>> pair = NLUController.parse(jsonObj);

        assertThat(pair.first).isEqualTo("patient_symptom");
        assertThat(pair.second.size()).isEqualTo(1);
    }

    @Test
    public void testParse_OpenAirwayManagement_Correct() throws IOException, URISyntaxException, JSONException {
        JSONObject jsonObj = new JSONObject(readJSONToString("open_airway_management.json"));
        Pair<String, List<Entity>> pair = NLUController.parse(jsonObj);

        assertThat(pair.first).isEqualTo("navigate");
        assertThat(pair.second.size()).isEqualTo(1);
    }

    public String readJSONToString(String filename) throws IOException, URISyntaxException {
        java.net.URL url = this.getClass().getClassLoader().getResource(filename);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        return new String(java.nio.file.Files.readAllBytes(resPath), StandardCharsets.UTF_8);
    }
}
