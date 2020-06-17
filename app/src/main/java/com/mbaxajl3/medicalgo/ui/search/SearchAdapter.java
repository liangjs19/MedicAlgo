package com.mbaxajl3.medicalgo.ui.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mbaxajl3.medicalgo.Factory;
import com.mbaxajl3.medicalgo.R;
import com.mbaxajl3.medicalgo.controllers.FavouritesController;
import com.mbaxajl3.medicalgo.controllers.JSONController;
import com.mbaxajl3.medicalgo.models.AlgorithmMetadata;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "SearchAdapter";
    private final JSONController jsonController;
    private List<AlgorithmMetadata> mData;
    private List<AlgorithmMetadata> mDataFiltered;
    private List<AlgorithmMetadata> favouritesList;
    private LayoutInflater mInflater;
    private SearchAdapter.ItemClickListener mClickListener;
    private Context context;
    private FavouritesController favouritesController;

    SearchAdapter(Context context, List<AlgorithmMetadata> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataFiltered = data;
        this.jsonController = Factory.getJSONController();
        this.favouritesController = Factory.getFavouritesController();
        favouritesList = favouritesController.getFavourites();
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.algorithms_row, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        if (mDataFiltered.isEmpty()) {
            return;
        }

        AlgorithmMetadata algorithm = mDataFiltered.get(position);
        holder.tvAlgorithm.setText(algorithm.getAlgorithmName());
        holder.subtitleTvAlgorithm.setText(jsonController.getCategoryById(algorithm.getCategoryId()).getName());

        if (favouritesController.isAlgorithmInFavourites(algorithm)) {
            holder.likeButton.setLiked(true);
        } else {
            holder.likeButton.setLiked(false);
        }

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Log.v(TAG, "liked " + position);
                favouritesController.like(algorithm);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.v(TAG, "unliked " + position);
                favouritesController.unlike(algorithm);
            }
        });

        if (!algorithm.getImagePath().equals(""))
            Glide.with(context)
                    .load(algorithm.getImagePath())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivAlgorithm);
        else
            holder.ivAlgorithm.setVisibility(View.GONE);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<AlgorithmMetadata> filteredList = new ArrayList<>();
                    for (AlgorithmMetadata row : mData) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAlgorithmName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mDataFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (ArrayList<AlgorithmMetadata>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // convenience method for getting data at click position
    AlgorithmMetadata getItem(int id) {
        return mDataFiltered.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(SearchAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvAlgorithm;
        LikeButton likeButton;
        ImageView ivAlgorithm;
        TextView subtitleTvAlgorithm;

        ViewHolder(View itemView) {
            super(itemView);
            tvAlgorithm = itemView.findViewById(R.id.tvAlgorithm);
            likeButton = itemView.findViewById(R.id.favourite_button);
            ivAlgorithm = itemView.findViewById(R.id.ivAlgorithm);
            subtitleTvAlgorithm = itemView.findViewById(R.id.subtitleTvAlgorithm);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
