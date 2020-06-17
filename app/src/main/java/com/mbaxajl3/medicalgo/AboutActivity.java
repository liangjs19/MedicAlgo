package com.mbaxajl3.medicalgo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AboutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("About");
        }

        recyclerView = (RecyclerView) findViewById(R.id.icon_credits_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] items = getResources().getStringArray(R.array.icon_credits);
        // specify an adapter (see also next example)
        mAdapter = new AboutAdapter(this, items);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

}

class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    private String[] mDataset;
    private LayoutInflater mInflater;

    public AboutAdapter(AboutActivity context, String[] myDataset) {
        this.mInflater = LayoutInflater.from(context);
        mDataset = myDataset;
    }

    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View view = mInflater.inflate(R.layout.credits_row, parent, false);
        return new AboutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.icon_credits_text);
        }
    }
}
