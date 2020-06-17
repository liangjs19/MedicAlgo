package com.mbaxajl3.medicalgo.ui.categories;

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
import com.mbaxajl3.medicalgo.models.Category;
import com.mbaxajl3.medicalgo.ui.algorithms.AlgorithmsFragment;

import java.util.List;
import java.util.Objects;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHMS_FRAGMENT;

public class CategoriesFragment extends Fragment implements CategoriesAdapter.ItemClickListener {

    private final String TAG = "CategoriesFragment";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private List<Category> categories;
    private CategoriesFragment categoriesFragment = this;
    private JSONController jsonController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        context = root.getContext();
        jsonController = Factory.getJSONController();

        recyclerView = root.findViewById(R.id.categories_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        categories = jsonController.getCategories();
        mAdapter = new CategoriesAdapter(root.getContext(), categories);
        ((CategoriesAdapter) mAdapter).setClickListener(categoriesFragment);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (categories.get(position).getNoOfAlgos() == 0) {
            return;
        }
        Category category = categories.get(position);

        AlgorithmsFragment algorithmsFragment = new AlgorithmsFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        algorithmsFragment.setArguments(args);
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, algorithmsFragment, ALGORITHMS_FRAGMENT)
                .addToBackStack(ALGORITHMS_FRAGMENT)
                .commit();
    }
}
