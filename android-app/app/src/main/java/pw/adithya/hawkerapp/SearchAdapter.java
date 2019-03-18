package pw.adithya.hawkerapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pw.adithya.hawkerapp.Objects.Details;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private ArrayList<Details> details = new ArrayList<>();
    private Context mContext;

    SearchAdapter(Context context, ArrayList<Details> details) {
        this.mContext = context;
        this.details = details;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recycler_view, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        SearchViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
        }

        public void bindDetails (Details details) {
        }
    }
}
