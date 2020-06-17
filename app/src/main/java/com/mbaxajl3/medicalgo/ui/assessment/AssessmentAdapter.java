package com.mbaxajl3.medicalgo.ui.assessment;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;

import java.text.DateFormat;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.ViewHolder> {
    private static final String TAG = "AssessmentAdapter";
    private LayoutInflater mInflater;
    private Context context;
    private ItemClickListener mClickListener;
    private List<Assessment> assessments;
    private JSONController jsonController;

    // data is passed into the constructor
    public AssessmentAdapter(Context context, List<Assessment> assessments) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.assessments = assessments;
        this.jsonController = Factory.getJSONController();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.assessment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        holder.name.setText(assessment.getAlgorithm().getName());
        holder.category.setText(jsonController.getCategoryById(assessment.getAlgorithm().getCategoryId()).getName());

        DateFormat dateFormat
                = DateFormat.getDateTimeInstance();
        holder.date.setText(dateFormat.format(assessment.getDate()));

        int error = assessment.getErrors();
        int correct = assessment.getCorrect();

        String score = "<font color=#cc0029>" + error + "</font>/<font color=#008140>" + correct + "</font>";
        holder.score.setText(Html.fromHtml(score));
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    // convenience method for getting data at click position
    Algorithm getAlgorithm(int id) {
        return assessments.get(id).getAlgorithm();
    }

    Assessment getItem(int id) {
        return assessments.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(AssessmentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView date;
        TextView score;
        TextView category;
        MaterialCardView materialCardView;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.assessment_name);
            date = itemView.findViewById(R.id.assessment_date);
            score = itemView.findViewById(R.id.assessment_score);
            category = itemView.findViewById(R.id.assessment_category);

            materialCardView = itemView.findViewById(R.id.assessment_card);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

}
