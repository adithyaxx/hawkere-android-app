package pw.adithya.hawkerapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private ArrayList<Detail> details;

    public SearchAdapter(ArrayList<Detail> details, Activity activity) {
        this.activity = activity;
        this.details = details;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_recycler_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder vh = (MyViewHolder) holder;

        vh.nameTextView.setText(details.get(position).getName());
        vh.addressTextView.setText(details.get(position).getShortAddr());

        if (details.get(position).getDistance() < 1000)
            vh.distanceTextView.setText(details.get(position).getDistance() + "m");
        else
            vh.distanceTextView.setText(String.format("%.1fkm", details.get(position).getDistance() / 1000));

        if(!details.get(position).getPhotoURL().equalsIgnoreCase(""))
            Picasso.get().load(details.get(position).getPhotoURL()).into(vh.imageView);
    }

    @Override
    public int getItemCount() {
        if (details == null)
            return 0;

        return details.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, addressTextView, distanceTextView;
        private MaterialRatingBar materialRatingBar;
        private ImageView imageView;

        private MyViewHolder(View view) {
            super(view);

            nameTextView = view.findViewById(R.id.textview_name);
            distanceTextView = view.findViewById(R.id.textview_distance);
            addressTextView = view.findViewById(R.id.textview_address);
            materialRatingBar = view.findViewById(R.id.material_rating_bar);
            imageView = view.findViewById(R.id.imageview_pic);
        }
    }
}
