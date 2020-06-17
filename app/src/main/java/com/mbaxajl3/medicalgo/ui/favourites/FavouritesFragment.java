package com.mbaxajl3.medicalgo.ui.favourites;

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
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;

public class FavouritesFragment extends Fragment implements FavouritesAdapter.ItemClickListener {
    private static final String TAG = "FavouritesFragment";
    private Context context;
    private RecyclerView recyclerView;
    private FavouritesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<AlgorithmMetadata> algorithms = new ArrayList<>();
    private JSONController jsonController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);
        context = root.getContext();

        jsonController = Factory.getJSONController();
        recyclerView = root.findViewById(R.id.favourites_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FavouritesAdapter(root.getContext());
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        AlgorithmFragment algorithmFragment = new AlgorithmFragment();
        Algorithm algorithm = jsonController.getAlgorithm(mAdapter.getItem(position));
        Bundle args = new Bundle();
        args.putSerializable("algorithm", algorithm);
        algorithmFragment.setArguments(args);
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, algorithmFragment, ALGORITHM_FRAGMENT)
                .addToBackStack(ALGORITHM_FRAGMENT)
                .commit();
    }

    public FavouritesAdapter getAdapter() {
        return mAdapter;
    }
}
