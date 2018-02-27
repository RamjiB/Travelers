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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter
        .MasonryViewHolder> {

    private final Context mContext;
    private final ImageClickListener imageClickListener;
    private ArrayList<String> imageUrl = new ArrayList<>();
    private ArrayList<String> location = new ArrayList<>();
    private ArrayList<String> caption = new ArrayList<>();
    private ArrayList<String> photo_id = new ArrayList<>();
    private ArrayList<String> fileType = new ArrayList<>();

    interface ImageClickListener{
        void imageClick(String imageUrl,String postLocation,String description,
                        String photo_id,String fileType);
    }

    public PostImagesAdapter(Context mContext, ImageClickListener imageClickListener,
                             ArrayList<String> imageUrl, ArrayList<String> location,
                             ArrayList<String> caption, ArrayList<String> photo_id,
                             ArrayList<String> fileType) {
        this.mContext = mContext;
        this.imageClickListener = imageClickListener;
        this.imageUrl = imageUrl;
        this.location = location;
        this.caption = caption;
        this.photo_id = photo_id;
        this.fileType = fileType;
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
        if (!Objects.equals(fileType.get(position), "image/jpeg")){
            holder.video_icon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return imageUrl.size();
    }


    public class MasonryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.postedImage)
        ImageView postedImage;

        @BindView(R.id.video_icon)
        ImageView video_icon;

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
                    photo_id.get(getAdapterPosition()),
                    fileType.get(getAdapterPosition()));
        }
    }
}
