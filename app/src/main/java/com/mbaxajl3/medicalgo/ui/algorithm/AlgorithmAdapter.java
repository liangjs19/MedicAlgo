package com.mbaxajl3.medicalgo.ui.algorithm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.SpeechService;
import com.mbaxajl3.medicalgo.Util;
import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;
import com.mbaxajl3.medicalgo.models.Entity;
import com.mbaxajl3.medicalgo.models.Option;
import com.mbaxajl3.medicalgo.models.Pair;
import com.mbaxajl3.medicalgo.models.Section;
import com.mbaxajl3.medicalgo.models.Step;
import com.mbaxajl3.medicalgo.ui.algorithms.AlgorithmsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmAdapter extends RecyclerView.Adapter<AlgorithmAdapter.ViewHolder> {

    private final static String TAG = "AlgorithmAdapter";
    private Algorithm algorithm;
    private List<Step> mData = new ArrayList<>();
    private List<Step> steps;
    private LayoutInflater mInflater;
    private View view;
    private AlgorithmsAdapter.ItemClickListener mClickListener;
    private List<Section> sections;
    private RecyclerView.LayoutManager manager;
    private Context context;
    private AssessmentController assessmentController;
    private Assessment assessment;
    private List<Pair<Step, Boolean>> stepsSoFar = new ArrayList<>();
    private boolean inAssessment = false;
    private JSONController jsonController;
    private SpeechService speechService;
    private int currentSectionId = -1;

    // data is passed into the constructor
    public AlgorithmAdapter(Context context, RecyclerView.LayoutManager manager, Algorithm algorithm) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        this.algorithm = algorithm;
        this.steps = algorithm.getSteps();
        this.sections = algorithm.getSections();
        this.mData.add(steps.get(0));
        this.assessmentController = Factory.getAssessmentController();
        this.jsonController = Factory.getJSONController();
        this.speechService = Factory.getSpeechService();
    }

    public AlgorithmAdapter(Context context, RecyclerView.LayoutManager manager, Assessment assessment) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        this.algorithm = assessment.getAlgorithm();
        this.sections = algorithm.getSections();
        this.steps = this.mData = assessment.getSteps();
        this.assessment = assessment;
    }

    // inflates the row layout from xml when needed
    @Override
    public AlgorithmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.algorithm_step, parent, false);
        return new AlgorithmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlgorithmAdapter.ViewHolder holder, int position) {
        int newPosition = holder.getAdapterPosition();
        int sectionId = getItem(newPosition).getSectionId();

        holder.chip.setVisibility(View.GONE);
        holder.tvStepNumber.setText(newPosition + 1 + "");

        // correct/wrong cards when in assessment view
        if (assessment != null) {
            if (assessment.getStepsTaken().get(newPosition).second) {
                holder.materialCardView.setCardBackgroundColor(Color.parseColor("#ffcccb"));
            } else {
                holder.materialCardView.setCardBackgroundColor(Color.parseColor("#ddfbbd"));
            }
        }

        if (sectionId != -1 && (sections != null || !sections.isEmpty())) {
            for (Section section : sections) {
                if (sectionId == section.getId()) {
                    holder.chip.setVisibility(View.VISIBLE);
                    holder.chip.setText(section.getTitle());

                    // only show section chip on latest step
                    if (currentSectionId == sectionId && mData.size() > 1)
                        if (mData.get(newPosition) == mData.get(mData.size() - 2))
                            holder.chip.setVisibility(View.GONE);

                    currentSectionId = sectionId;
                    break;
                }
            }
        }

        String title = getItem(newPosition).getTitle();
        if (title == null || title.isEmpty()) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        String content = getItem(newPosition).getStep();
        if (content == null || content.isEmpty()) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY));

            // only use tts for latest loaded step
            if (assessment == null) {
                String ssml = getLastStep().getSsml();
                speechService.speak(ssml, true);
            }
        }

        List<Option> options = getItem(newPosition).getOptions();

        if (options.size() == 0 || assessment != null) {
            holder.llButton.setVisibility(View.GONE);
            return;
        } else {
            holder.llButton.setVisibility(View.VISIBLE);
        }

        holder.llButton.removeAllViews();

        for (Option option : options) {
            Button button = new Button(view.getContext());
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, context.getResources().getDisplayMetrics()), 1.0f);
            button.setTag("button" + option.getNextStepId());
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setText(option.getOption());
            button.setTextColor(Util.getContrastColor(option.getColor()));
            button.getBackground().setColorFilter(option.getColor(), PorterDuff.Mode.MULTIPLY);
            button.setOnClickListener(v -> {
                resetSteps(newPosition);
                nextStep(option);

                if (mData.get(newPosition).getSectionId() == -1) {
                    currentSectionId = -1;
                }
            });
            holder.llButton.addView(button, buttonParams);
        }

        holder.materialCardView.setOnClickListener(v -> {
            String ssml = getItem(newPosition).getSsml();
            speechService.speak(ssml, true);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    Step getItem(int id) {
        return mData.get(id);
    }

    public void previousStep() {
        if (mData.size() <= 1) {
            return;
        }

        mData.remove(mData.size() - 1);
        this.notifyItemRemoved(mData.size() - 1);
        this.notifyItemRangeChanged(mData.size() - 1, mData.size());
    }

    // allows clicks events to be caught
    void setClickListener(AlgorithmsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private void scrollTo(int id) {
        if (manager == null) {
            return;
        }
        manager.scrollToPosition(id);
    }

    public boolean nextStep(Option option) {
        if (option == null) {
            return false;
        }

        Step step = steps.get(option.getNextStepId());
        mData.add(step);
        this.notifyItemInserted(mData.size() - 1);
        scrollTo(mData.size() - 1);

        return true;
    }


    public Step getLastStep() {
        return mData.get(mData.size() - 1);
    }

    public void resetSteps() {
        resetSteps(0);
    }

    public void resetSteps(int resetTo) {
        if (resetTo < 0 || mData.size() <= 1) {
            return;
        }

        int startIndex = ++resetTo;
        int endIndex = mData.size(); // exclusive
        int count = endIndex - startIndex;
        mData.subList(startIndex, endIndex).clear();
        this.notifyItemRangeRemoved(startIndex, count);
    }

    public Step getStep(int id) {
        return steps.get(id);
    }

    private void reverseSteps(List<Step> list) {
        for (int i = 0; i < list.size() / 2; i++) {
            Step temp = list.get(i);
            list.set(i, list.get(list.size() - i - 1));
            list.set(list.size() - i - 1, temp);
        }
    }

    // get step that is the most relevant in the algorithm based on their positions
    public boolean findStepByIntentAndEntities(Pair<String, List<Entity>> pair) {
        String intent = pair.first;
        List<Entity> entities = pair.second;

        // stores that steps that matches intents and entities
        List<Step> relevantSteps = new ArrayList<>();

        // search through all the steps
        for (Step step : this.steps) {
            //if the options of the steps has a match
            Step matchingStep = assessOptions(step, pair);
            if (matchingStep != null) {
                relevantSteps.add(matchingStep);
            }

            //check if the current step is a match as well
            if (!assessStep(step, pair)) {
                relevantSteps.add(step);
            }
        }

        if (relevantSteps.isEmpty()) {
            return true;
        }

        // get most relevant step by getting closest step that has already been read
        Step lastStep = relevantSteps.get(0);

        for (Step step : relevantSteps) {
            for (Option option : getLastStep().getOptions()) {
                if (option.getNextStepId() >= step.getId()) {
                    lastStep = step;
                    break;
                }
            }
        }

        return gotoStep(lastStep, assess(pair));
    }

    private boolean gotoStep(Step step, boolean error) {
        if (inAssessment) {
            stepsSoFar.add(new Pair<>(step, error));
            assessmentController.saveAssessment(stepsSoFar);
            if (error) {
                return true;
            }
        }

        mData.clear();
        mData.add(step);

        // get sequence of steps leading to current step
        for (int i = step.getId(); i >= 0; i--) {
            if (steps.get(i).getOptions() == null) {
                continue;
            }

            for (Option option : steps.get(i).getOptions()) {
                if (option.getNextStepId() == getLastStep().getId()) {
                    mData.add(steps.get(i));
                }
            }
        }

        reverseSteps(mData);

        this.notifyDataSetChanged();
        scrollTo(mData.size() - 1);
        return false;
    }

    private boolean assess(Pair<String, List<Entity>> pair) {
        return assess(getLastStep(), pair);
    }

    private boolean assess(Step step, Pair<String, List<Entity>> pair) {
        if (!inAssessment) {
            return false;
        }

        // see if matches current step
        if (!assessStep(step, pair)) {
            return false;
        }

        //else, step through its options
        if (assessOptions(step, pair) != null) {
            return false;
        }

        // check intent and entities of next step
        if (step.getOptions().size() == 1) {
            Step nextStep = getStep(step.getOptions().get(0).getNextStepId());

            if (nextStep == null) {
                return false;
            }

            if (!assessStep(nextStep, pair)) {
                return false;
            }

            // check through the options of the step
            if (assessOptions(nextStep, pair) != null) {
                return false;
            }

            // ignore optional step and find next non- optional step
            while (nextStep.isOptional() && nextStep.getOptions().size() > 0) {
                nextStep = getStep(nextStep.getOptions().get(0).getNextStepId());
            }

            return assess(nextStep, pair);
        }

        return true;
    }

    // see if step matches intent and entities
    private boolean assessStep(Step step, Pair<String, List<Entity>> pair) {
        String intent = pair.first;
        List<Entity> entities = pair.second;
        if (step.getIntent().equalsIgnoreCase(intent)) {
            int matches = 0;
            for (Entity entity : entities) {
                for (Entity stepEntity : step.getEntities()) {
                    if (stepEntity.getType().equalsIgnoreCase(entity.getType())) {
                        matches++;
                    }
                }
            }

            //if its a satisfactory match, step is correct
            return !(((double) matches / step.getEntities().size()) > 0.5);
        }
        return true;
    }

    // look through the steps of the current step's options to see if they match the intent and entities
    private Step assessOptions(Step step, Pair<String, List<Entity>> pair) {
        String intent = pair.first;
        List<Entity> entities = pair.second;
        for (Option option : step.getOptions()) {
            if (option.getIntent() == null) {
                continue;
            }

            if (option.getIntent().equalsIgnoreCase(intent)) {
                if (option.getEntities().isEmpty()) {
                    continue;
                }

                int matches = 0;
                for (Entity entity : entities) {
                    for (Entity stepEntity : option.getEntities()) {
                        if (stepEntity.getType().equalsIgnoreCase(entity.getType())) {
                            matches++;
                        }
                    }
                }

                if (((double) matches / option.getEntities().size()) > 0.5) {
                    return getStep(option.getNextStepId());
                }
            }
        }
        return null;
    }

    public void startAssessment() {
        inAssessment = true;
        assessmentController.createAssessment(algorithm);
        resetSteps();
    }

    public void stopAssessment() {
        inAssessment = false;
    }

    public boolean inAssessment() {
        return inAssessment;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        ImageView ivImage;
        TextView tvContent;
        Chip chip;
        MaterialCardView materialCardView;
        LinearLayout llButton;
        TextView tvStepNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.stepTitle);
            ivImage = itemView.findViewById(R.id.stepImage);
            tvContent = itemView.findViewById(R.id.stepContent);
            llButton = itemView.findViewById(R.id.buttonView);
            chip = itemView.findViewById(R.id.sectionTitle);
            materialCardView = itemView.findViewById(R.id.step_card);
            tvStepNumber = itemView.findViewById(R.id.step_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Assessment getAssessment() {
        return assessment;
    }
}
