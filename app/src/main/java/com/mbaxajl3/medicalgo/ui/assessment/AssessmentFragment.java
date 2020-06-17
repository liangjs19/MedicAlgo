package com.mbaxajl3.medicalgo.ui.assessment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.controllers.AssessmentController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Assessment;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;

import java.util.List;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;

public class AssessmentFragment extends Fragment implements AssessmentAdapter.ItemClickListener {
    private static final String TAG = "AssessmentFragment";
    private Context context;
    private RecyclerView recyclerView;
    private AssessmentAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private JSONController jsonController;
    private AssessmentController assessmentController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_assessment, container, false);
        context = root.getContext();

        recyclerView = root.findViewById(R.id.assessment_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        jsonController = Factory.getJSONController();
        assessmentController = Factory.getAssessmentController();

        List<Assessment> assessments = assessmentController.getAssessmentList();
        mAdapter = new AssessmentAdapter(root.getContext(), assessments);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        AlgorithmFragment algorithmFragment = new AlgorithmFragment();
        Assessment assessment = mAdapter.getItem(position);

        Bundle args = new Bundle();
        args.putSerializable("assessment", assessment);
        algorithmFragment.setArguments(args);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, algorithmFragment, ALGORITHM_FRAGMENT)
                .addToBackStack(ALGORITHM_FRAGMENT)
                .commit();
    }
}
