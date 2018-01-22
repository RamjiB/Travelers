package ramji.travelers;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesStaggeredAdapter extends RecyclerView.Adapter<ImagesStaggeredAdapter
        .MasonryViewHolder> {

    private static final String TAG = "ImagesStaggeredAdapter";

    private Context mContext;

    public ImagesStaggeredAdapter(Context mContext) {
        this.mContext = mContext;
    }
    private int[] size = {3,4,5,6,7,8,9,10};
    private int mPosition = 0;

    private String[] imageUrl = {
            "https://s.hswstatic.com/gif/adventure-travel-6.jpg",
            "https://s-i.huffpost.com/gen/2593452/images/o-TRAVEL-MOUNT-facebook.jpg",
            "https://www.adventure-journal.com/wp-content/uploads/2015/06/o-the-glory-of-it-all.jpg",
            "http://cdn.coresites.factorymedia.com/twc/wp-content/uploads/2014/06/header.jpg",
            "https://i.kinja-img.com/gawker-media/image/upload/t_original/951820009082492817.jpg",
            "http://www.quotesfrenzy.com/wp-content/uploads/2017/08/Adventure-Quotes-And-Sayings-1.jpg",
            "https://www.outsideonline.com/sites/default/files/styles/img_948x622/public/2016/11/18/big-wave-surfing-paige-alms_h_0.jpg?itok=eAllwvCx",
            "http://www.lifeadventures.us/wp-content/uploads/2014/05/109-800x400.jpg",
            "http://images.indianexpress.com/2016/07/adventure.jpg",
            "http://paulgalloro.com/wp-content/uploads/2014/03/New-Adventure.jpg",
            "https://i.pinimg.com/736x/f4/24/59/f42459b08d70e1c79ce4c09dde8d91df--adventure-photography-friends-perspective-photography.jpg"
    };

    private String[] location = {"1","2","3","4","5","6","7","8","9","10","11"};


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
                .load(imageUrl[position])
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.postedImage);

        holder.postLocation.setText(location[position]);
    }

    @Override
    public int getItemCount() {
        return imageUrl.length;
    }


    public class MasonryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.postedImage)
        ImageView postedImage;

        @BindView(R.id.postLocation)
        TextView postLocation;

        public MasonryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
