package ramji.travelers.posts;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

public class ImagesStaggeredAdapter extends RecyclerView.Adapter<ImagesStaggeredAdapter
        .MasonryViewHolder> {

    private static final String TAG = "ImagesStaggeredAdapter";

    private Context mContext;
    private ImageClickListener imageClickListener;
    private ArrayList<String> imageUrl = new ArrayList<>();
    private ArrayList<String> location = new ArrayList<>();
    private ArrayList<String> caption = new ArrayList<>();
    private ArrayList<String> photo_id = new ArrayList<>();

    interface ImageClickListener{
        void imageClick(String imageUrl,String postLocation,String description,String photo_id);
    }

    public ImagesStaggeredAdapter(Context mContext, ImageClickListener imageClickListener,
                                  ArrayList<String> imageUrl, ArrayList<String> location,
                                  ArrayList<String> caption,ArrayList<String> photo_id) {
        this.mContext = mContext;
        this.imageClickListener = imageClickListener;
        this.imageUrl = imageUrl;
        this.location = location;
        this.caption = caption;
        this.photo_id = photo_id;
    }

    @Override
    public MasonryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_images,parent,
                false);
        return new MasonryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MasonryViewHolder holder, int position) {

        GlideApp
                .with(mContext)
                .load(imageUrl.get(position))
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.postedImage);

        holder.postLocation.setText(location.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrl.size();
    }


    public class MasonryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.postedImage)
        ImageView postedImage;

        @BindView(R.id.postLocation)
        TextView postLocation;

        public MasonryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            imageClickListener.imageClick(imageUrl.get(getAdapterPosition()),
                    location.get(getAdapterPosition()),caption.get(getAdapterPosition()),
                    photo_id.get(getAdapterPosition()));
        }
    }
}
