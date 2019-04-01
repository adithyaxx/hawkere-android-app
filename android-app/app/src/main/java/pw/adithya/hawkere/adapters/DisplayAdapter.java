package pw.adithya.hawkere.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import pw.adithya.hawkere.objects.Detail;
import pw.adithya.hawkere.R;

public class DisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Activity activity;
    private ArrayList<Detail> filteredArrayList;
    private ArrayList<Detail> originalArrayList;
    private ItemFilter mFilter = new ItemFilter();

    public DisplayAdapter(ArrayList<Detail> arrayList, Activity activity) {
        this.activity = activity;
        this.originalArrayList = arrayList;
        this.filteredArrayList = arrayList;
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

        vh.nameTextView.setText(filteredArrayList.get(position).getName());
        vh.addressTextView.setText(filteredArrayList.get(position).getShortAddr());

        if (filteredArrayList.get(position).getDistance() < 1000)
            vh.distanceTextView.setText(filteredArrayList.get(position).getDistance() + "m");
        else
            vh.distanceTextView.setText(String.format("%.1fkm", filteredArrayList.get(position).getDistance() / 1000));

        final Transformation transformation = new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.LEFT);

        if(!filteredArrayList.get(position).getPhotoURL().equalsIgnoreCase(""))
            Picasso.get().load(filteredArrayList.get(position).getPhotoURL()).transform(transformation).resize(200,200).centerCrop().into(vh.imageView);

    }

    @Override
    public int getItemCount() {
        if (filteredArrayList == null)
            return 0;

        return filteredArrayList.size();
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

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            ArrayList<Detail> matches = new ArrayList<>();

            if (filterString.equalsIgnoreCase(""))
            {
                results.values = originalArrayList;
                results.count = originalArrayList.size();
            }
            else
            {
                for (int i = 0; i < originalArrayList.size(); i++) {
                    String name, address;

                    if (originalArrayList.get(i).getName() == null)
                        name = "";
                    else
                        name = originalArrayList.get(i).getName().toLowerCase();

                    if (originalArrayList.get(i).getShortAddr() == null)
                        address = "";
                    else
                        address = originalArrayList.get(i).getShortAddr().toLowerCase();

                    if (name.contains(filterString) || address.contains(filterString)) {
                        matches.add(originalArrayList.get(i));
                    }
                }

                results.values = matches;
                results.count = matches.size();

                Log.e("Size", results.count + "");
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredArrayList = (ArrayList<Detail>) results.values;
            notifyDataSetChanged();
        }

    }
}
