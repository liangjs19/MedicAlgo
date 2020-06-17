package com.mbaxajl3.medicalgo.ui.algorithm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mbaxajl3.medicalgo.ImageViewActvity;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.Assessment;

public class AlgorithmFragment extends Fragment implements AlgorithmAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private AlgorithmAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private Algorithm algorithm;
    private BottomNavigationView bottomNavigationView;
    private Bundle bundle;
    private Assessment assessment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_algorithm, container, false);
        bundle = getArguments();

        FloatingActionButton fab = root.findViewById(R.id.fab);

        recyclerView = root.findViewById(R.id.algorithm_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        algorithm = (Algorithm) bundle.getSerializable("algorithm");
        assessment = (Assessment) bundle.getSerializable("assessment");

        if (assessment == null) {
            algorithm = (Algorithm) bundle.getSerializable("algorithm");
            mAdapter = new AlgorithmAdapter(root.getContext(), layoutManager, algorithm);
        } else {
            algorithm = assessment.getAlgorithm();
            mAdapter = new AlgorithmAdapter(root.getContext(), layoutManager, assessment);
        }

        if (algorithm == null) {
            return root;
        }

        if (algorithm.getImage() == null) {
            fab.hide();
        } else {
            fab.show();
            fab.setOnClickListener(view -> {
                launchImage();
            });
        }

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }
        });

        return root;
    }

    public void launchImage() {
        if (algorithm == null) return;
        Intent intent = new Intent(getActivity(), ImageViewActvity.class);
        intent.putExtra("algorithm", algorithm);
        startActivity(intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public AlgorithmAdapter getAdapter() {
        return mAdapter;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public String getActionBarTitle() {
        if (algorithm == null) {
            return "";
        }
        return algorithm.getName();
    }
}
