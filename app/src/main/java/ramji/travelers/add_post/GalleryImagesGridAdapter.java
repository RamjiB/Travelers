package ramji.travelers.add_post;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

public class GalleryImagesGridAdapter extends RecyclerView.Adapter<GalleryImagesGridAdapter
        .ImagesViewHolder> {

    private static final String TAG = "PostImagesAdapter";

    private Context mContext;
    private ImageClickListener imageClickListener;
    private ArrayList<String> imageUrl = new ArrayList<>();


    interface ImageClickListener{
        void imageClick(String imageUrl);
    }

    public GalleryImagesGridAdapter(Context mContext
            ,ImageClickListener imageClickListener,ArrayList<String> imageUrl) {

        this.mContext = mContext;
        this.imageClickListener = imageClickListener;
        this.imageUrl = imageUrl;
    }


    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_gallery_images,parent,
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

        @BindView(R.id.selectedMark)
        ImageButton tickMark;


        public ImagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            String imageURL;

            if (tickMark.getVisibility() == View.INVISIBLE){
                tickMark.setVisibility(View.VISIBLE);
                imageURL = imageUrl.get(getAdapterPosition());
                imageClickListener.imageClick(imageURL);
            }else
                tickMark.setVisibility(View.INVISIBLE);

        }
    }
}
