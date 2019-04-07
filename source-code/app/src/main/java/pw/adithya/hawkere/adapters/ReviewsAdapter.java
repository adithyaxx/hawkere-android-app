package pw.adithya.hawkere.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.R;
import pw.adithya.hawkere.objects.Rating;

public class ReviewsAdapter extends  RecyclerView.Adapter<ReviewsAdapter.PhotosViewHolder>{
    private ArrayList<Rating> reviews;
    Context context;

    public ReviewsAdapter(ArrayList<Rating> reviews, Context context){
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public PhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_recycler_view,parent,false);
        return new PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotosViewHolder holder, final int position) {
        Glide.with(context)
                .load(reviews.get(position).getAuthorPic())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.user)
                .into(holder.profileImageView);

        holder.nameTextView.setText(reviews.get(position).getAuthorName());
        holder.review.setText(reviews.get(position).getReview());
        holder.materialRatingBar.setRating((float) reviews.get(position).getTotalRating());

        PrettyTime prettyTime = new PrettyTime();
        holder.timestamp.setText(prettyTime.format(new Date(reviews.get(position).getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class PhotosViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImageView;
        TextView nameTextView, timestamp, review;
        MaterialRatingBar materialRatingBar;

        public PhotosViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imageview_profile_pic);
            nameTextView = itemView.findViewById(R.id.textview_name);
            timestamp = itemView.findViewById(R.id.textview_duration);
            review = itemView.findViewById(R.id.textview_review);
            materialRatingBar = itemView.findViewById(R.id.review_rating_bar);
        }
    }
}
