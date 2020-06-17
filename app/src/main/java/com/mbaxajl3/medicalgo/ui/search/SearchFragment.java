package com.mbaxajl3.medicalgo.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.Algorithm;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;
import com.mbaxajl3.medicalgo.ui.algorithm.AlgorithmFragment;

import java.util.List;
import java.util.Objects;

import static com.mbaxajl3.medicalgo.Constants.ALGORITHM_FRAGMENT;

public class SearchFragment extends Fragment implements SearchAdapter.ItemClickListener {
    private static final String TAG = "SearchFragment";
    private RecyclerView recyclerView;
    private SearchAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<AlgorithmMetadata> algorithms;
    private Context context;
    private JSONController jsonController;
    private EditText editText;
    private String searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        context = root.getContext();

        recyclerView = root.findViewById(R.id.search_algorithms_view);

        jsonController = Factory.getJSONController();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        algorithms = jsonController.getListOfAllAlgorithmsMetadata();
        mAdapter = new SearchAdapter(root.getContext(), algorithms);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);

        if (getArguments() != null)
            searchText = (String) getArguments().getSerializable("searchText");

        editText = root.findViewById(R.id.search_edit_text);

        if (searchText != null) {
            editText.setText(searchText);
            mAdapter.getFilter().filter(searchText);
        } else {
            // focus on search
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                mAdapter.getFilter().filter(s);
            }
        });

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

    public SearchAdapter getAdapter() {
        return mAdapter;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setSearchText(String s) {
        editText.setText(s);
    }
}
