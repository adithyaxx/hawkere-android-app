package pw.adithya.hawkere.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import pw.adithya.hawkere.R;

public class PhotosAdapter extends  RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>{
    private ArrayList<String> images;
    Context context;

    public PhotosAdapter(ArrayList<String> images, Context context){
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
                .load(images.get(position))
                .centerInside()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(holder.photoImageView);
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
