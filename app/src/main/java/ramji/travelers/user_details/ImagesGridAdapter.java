package ramji.travelers.user_details;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import ramji.travelers.posts.ImagesStaggeredAdapter;

public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter
        .ImagesViewHolder> {

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

    public ImagesGridAdapter(Context mContext,
                             ImageClickListener imageClickListener,
                             ArrayList<String> imageUrl,
                             ArrayList<String> location,
                             ArrayList<String> caption,
                             ArrayList<String> photo_id) {
        this.mContext = mContext;
        this.imageClickListener = imageClickListener;
        this.imageUrl = imageUrl;
        this.location = location;
        this.caption = caption;
        this.photo_id = photo_id;
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

    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"getItemCount: "+ imageUrl.size());
        return imageUrl.size();
    }


    public class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.user_Image)
        ImageView userImage;


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
                    photo_id.get(getAdapterPosition()));

        }
    }
}
