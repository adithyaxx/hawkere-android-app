package pw.adithya.hawkere.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import pw.adithya.hawkere.R;
import pw.adithya.hawkere.objects.Photo;

public class PhotosAdapter extends  RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>{
    private ArrayList<Photo> images;
    Context context;

    public PhotosAdapter(ArrayList<Photo> images, Context context){
        this.images = images;
        this.context = context;
    }

    @Override
    public PhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_recycler_view,parent,false);
        return new PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotosViewHolder holder, final int position) {
        Glide.with(context)
                .load(images.get(position).getUrl())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .placeholder(R.drawable.user)
                .into(holder.photoImageView);

        Glide.with(context)
                .load(images.get(position).getAuthorPic())
                .centerCrop()
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.user)
                .into(holder.profileImageView);

        holder.nameTextView.setText(images.get(position).getAuthorName());
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class PhotosViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImageView, photoImageView;
        TextView nameTextView;

        public PhotosViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imageview_profile_pic);
            photoImageView = itemView.findViewById(R.id.imageview_photo2);
            nameTextView = itemView.findViewById(R.id.textview_name);
        }
    }
}
