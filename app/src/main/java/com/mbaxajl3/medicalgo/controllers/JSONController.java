package com.mbaxajl3.medicalgo.controllers;

import android.content.Context;
import android.graphics.Color;

import com.mbaxajl3.medicalgo.Util;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.models.Category;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Option;
import com.mbaxajl3.medicalgo.models.Section;
import com.mbaxajl3.medicalgo.models.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JSONController {
    private static final String TAG = "JSONController";
    private List<Category> categories;
    private List<AlgorithmMetadata> listOfAllAlgorithmsMetadata = new ArrayList<>();
    public Context context;
    private List<String> words = new ArrayList<>();

    public JSONController(Context context) {
        this.context = context;
        readWordsFile();
    }

    private String loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private AlgorithmMetadata parseAlgorithmMetadataObject(int categoryId, JSONObject obj) {
        try {
            String algorithmName = obj.getString("algorithm");
            String filename = obj.getString("filename");
            int algorithmId = obj.getInt("algorithm_id");
            String imagePath = obj.optString("image", "");
            return new AlgorithmMetadata(algorithmName, algorithmId, categoryId, filename, imagePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> getCategories() {
        if (categories != null) {
            return categories;
        }

        return parseCategories(loadJSONFromAsset("categories.json"));
    }

    private List<Category> parseCategories(String json) {
        categories = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray cats = jsonObj.getJSONArray("categories");

            for (int i = 0; i < cats.length(); i++) {
                JSONObject cat = cats.getJSONObject(i);
                int id = cat.getInt("category_id");
                String name = cat.getString("category");
                JSONArray algorithmArr = cat.optJSONArray("algorithms");
                String iconPath = cat.getString("icon");
                List<AlgorithmMetadata> algorithms = new ArrayList<>();

                if (algorithmArr != null) {
                    for (int j = 0; j < algorithmArr.length(); j++) {
                        JSONObject algorithmObj = algorithmArr.getJSONObject(j);
                        AlgorithmMetadata metadata = parseAlgorithmMetadataObject(id, algorithmObj);
                        algorithms.add(metadata);
                        listOfAllAlgorithmsMetadata.add(metadata);
                    }
                }
                Category category = new Category(id, name, iconPath, algorithms);
                categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Algorithm getAlgorithm(AlgorithmMetadata metadata) {
        if (metadata == null) {
            return null;
        }

        return parseAlgorithmObject(loadJSONFromAsset(metadata.getFilename()));
    }

    public Algorithm getAlgorithmByName(String name) {
        AlgorithmMetadata metadata = getAlgorithmMetadataByName(name);
        return getAlgorithm(metadata);
    }

    public Category getCategoryByName(String name) {
        if (categories == null) {
            return null;
        }

        for (Category category : categories) {
            if (Util.containsIgnoreCase(name, category.getName())) {
                return category;
            }
        }

        return null;
    }

    public Category getCategoryById(int id) {
        if (categories == null) {
            return null;
        }

        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }

        return null;
    }

    public Algorithm getAlgorithmById(int id) {
        AlgorithmMetadata metadata = getAlgorithmMetadataById(id);
        return getAlgorithm(metadata);
    }

    public AlgorithmMetadata getAlgorithmMetadataByName(String name) {
        for (AlgorithmMetadata algorithm : listOfAllAlgorithmsMetadata) {
            if (Util.containsIgnoreCase(name, algorithm.getAlgorithmName())) {
                return algorithm;
            }
        }
        return null;
    }

    public AlgorithmMetadata getAlgorithmMetadataById(int id) {
        for (AlgorithmMetadata metadata : listOfAllAlgorithmsMetadata) {
            if (metadata.getAlgorithmId() == id) {
                return metadata;
            }
        }
        return null;
    }

    private Algorithm parseAlgorithmObject(String json) {
        if (json == null) {
            return null;
        }

        try {
            JSONObject algorithm = new JSONObject(json);
            int id = algorithm.getInt("algorithm_id");
            int categoryId = algorithm.getInt("category_id");
            String name = algorithm.getString("algorithm");
            String image = algorithm.optString("image", null);
            JSONArray steps = algorithm.getJSONArray("steps");
            List<Step> algorithmSteps = new ArrayList<>();
            for (int stepIndex = 0; stepIndex < steps.length(); stepIndex++) {
                JSONObject step = steps.getJSONObject(stepIndex);

                List<Option> optionSteps = new ArrayList<>();
                JSONArray options = step.optJSONArray("options");

                if (options != null) {
                    for (int optionIndex = 0; optionIndex < options.length(); optionIndex++) {
                        JSONObject option = options.getJSONObject(optionIndex);
                        String optionName = option.getString("option");
                        int nextStepId = option.getInt("next_step_id");
                        int optionColor = Color.parseColor(option.optString("color", "#e57373"));
                        JSONArray entities = option.optJSONArray("entities");
                        String intent = option.optString("intent", null);
                        List<Entity> stepEntities = new ArrayList<>();
                        if (entities != null) {
                            for (int entityIndex = 0; entityIndex < entities.length(); entityIndex++) {
                                String entity = entities.optString(entityIndex);
                                stepEntities.add(new Entity(entity));
                            }
                        }
                        Option newOption = new Option(optionColor, optionName, nextStepId, intent, stepEntities);
                        optionSteps.add(newOption);
                    }
                }

                int stepId = step.getInt("step_id");
                String stepContent = step.optString("step", null);
                String stepTitle = step.optString("title", null);
                String stepSsml = step.optString("ssml", "null");
                boolean optional = step.optBoolean("optional", false);
                int stepSectionId = step.optInt("section_id", -1);
                int stepColor = Color.parseColor(step.optString("color", "#FFFFFF"));
                String stepImage = step.optString("image", "null");
                JSONArray entities = step.optJSONArray("entities");
                String intent = step.optString("intent", null);
                List<Entity> stepEntities = new ArrayList<>();
                if (entities != null) {
                    for (int entityIndex = 0; entityIndex < entities.length(); entityIndex++) {
                        String entity = entities.optString(entityIndex);
                        stepEntities.add(new Entity(entity));
                    }
                }

                Step newStep = new Step(stepId, stepSectionId, stepImage, stepColor, stepTitle, stepContent, stepSsml, optionSteps, intent, stepEntities, optional);
                algorithmSteps.add(newStep);
            }

            JSONArray sections = algorithm.optJSONArray("sections");
            List<Section> algorithmSections = new ArrayList<>();
            if (sections != null) {
                for (int sectionIndex = 0; sectionIndex < sections.length(); sectionIndex++) {
                    JSONObject section = sections.getJSONObject(sectionIndex);
                    int sectionId = section.getInt("section_id");
                    String sectionTitle = section.optString("title", null);
                    int sectionColor = section.optInt("color", Color.WHITE);
                    Section newSection = new Section(sectionId, id, sectionTitle, sectionColor);
                    algorithmSections.add(newSection);
                }
            }

            return new Algorithm(id, categoryId, name, image, algorithmSteps, algorithmSections);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Algorithm getAlgorithmByFileName(String filename) {
        return parseAlgorithmObject(loadJSONFromAsset(filename));
    }

    public List<AlgorithmMetadata> getAlgorithmMetadatasByCategory(Category category) {
        List<AlgorithmMetadata> metadatas = new ArrayList<>();

        for (AlgorithmMetadata metadata : listOfAllAlgorithmsMetadata) {
            if (metadata.getCategoryId() == category.getId()) {
                metadatas.add(metadata);
            }
        }

        return metadatas;
    }

    public List<AlgorithmMetadata> getListOfAllAlgorithmsMetadata() {
        return listOfAllAlgorithmsMetadata;
    }

    private void readWordsFile() {
        try {
            InputStream inputStream = context.getAssets().open("words.txt");
            InputStreamReader isReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isReader);
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }

            isReader.close();
            reader.close();
            inputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getWords() {
        return words;
    }
}
