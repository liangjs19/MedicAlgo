package com.mbaxajl3.medicalgo.ui.algorithms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.models.Category;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;

import java.util.ArrayList;
import java.util.List;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;

public class AlgorithmsFragment extends Fragment implements AlgorithmsAdapter.ItemClickListener {
    private static final String TAG = "AlgorithmsFragment";
    private RecyclerView recyclerView;
    private AlgorithmsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<AlgorithmMetadata> algorithms;
    private AlgorithmsFragment algorithmFragment = this;
    private Category category;
    private Context context;
    private JSONController jsonController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_algorithms, container, false);

        category = (Category) getArguments().getSerializable("category");
        context = root.getContext();

        recyclerView = root.findViewById(R.id.algorithms_view);

        jsonController = Factory.getJSONController();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        algorithms = new ArrayList<>();
        algorithms = category.getAlgorithms();

        mAdapter = new AlgorithmsAdapter(root.getContext(), algorithms);
        mAdapter.setClickListener(algorithmFragment);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.v(TAG, "Algorithms fragment");

        AlgorithmFragment algorithmFragment = new AlgorithmFragment();
        Algorithm algorithm = jsonController.getAlgorithm(mAdapter.getItem(position));
        Bundle args = new Bundle();
        args.putSerializable("algorithm", algorithm);
        algorithmFragment.setArguments(args);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, algorithmFragment, ALGORITHM_FRAGMENT)
                .addToBackStack(ALGORITHM_FRAGMENT)
                .commit();
    }

    public String getActionBarTitle() {
        return category.getName();
    }

    public AlgorithmsAdapter getAdapter() {
        return mAdapter;
    }
}
