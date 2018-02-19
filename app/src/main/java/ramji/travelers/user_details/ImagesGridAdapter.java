package ramji.travelers.user_details;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter
        .ImagesViewHolder> {

    private static final String TAG = "PostImagesAdapter";

    private Context mContext;
    private ImageClickListener imageClickListener;
    private ArrayList<String> imageUrl = new ArrayList<>();
    private ArrayList<String> location = new ArrayList<>();
    private ArrayList<String> caption = new ArrayList<>();
    private ArrayList<String> photo_id = new ArrayList<>();
    private ArrayList<String> fileType = new ArrayList<>();

    interface ImageClickListener{
        void imageClick(String imageUrl,String postLocation,String description,String photo_id,
                        String fileType);
    }

    public ImagesGridAdapter(Context mContext,
                             ImageClickListener imageClickListener,
                             ArrayList<String> imageUrl,
                             ArrayList<String> location,
                             ArrayList<String> caption,
                             ArrayList<String> photo_id,
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
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_images_user_profile,parent,
                false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {

        GlideApp
                .with(mContext)
                .load(imageUrl.get(position))
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.userImage);

        if (!Objects.equals(fileType.get(position), "image/jpeg")){
            holder.video_icon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"getItemCount: "+ imageUrl.size());
        return imageUrl.size();
    }


    public class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.user_Image)
        ImageView userImage;

        @BindView(R.id.video_icon)
        ImageView video_icon;


        public ImagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            imageClickListener.imageClick(imageUrl.get(getAdapterPosition()),
                    location.get(getAdapterPosition()),
                    caption.get(getAdapterPosition()),
                    photo_id.get(getAdapterPosition()),
                    fileType.get(getAdapterPosition()));
        }
    }
}
