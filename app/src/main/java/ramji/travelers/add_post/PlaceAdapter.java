package ramji.travelers.add_post;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.R;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>{

    private static final String TAG = "PlaceAdapter";

    private ArrayList<String> placesPrimary = new ArrayList<>();
    private ArrayList<String> placesSec = new ArrayList<>();
    private final onItemClickListener mOnItemClickListener;

    interface onItemClickListener{
        void onItemClick(int position);
    }

    public PlaceAdapter(ArrayList<String> placesPrimary,
                        ArrayList<String> placesSec,
                        onItemClickListener mOnItemClickListener) {
        this.placesPrimary = placesPrimary;
        this.placesSec = placesSec;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_place_adapter,
                parent,false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceViewHolder holder, int position) {

        Log.i(TAG,"onBindViewHolder");
        if (placesPrimary != null){
            holder.primaryText.setText(placesPrimary.get(position));
            holder.secondaryText.setText(placesSec.get(position));
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"getItemCount: " + placesPrimary.size());
        return placesPrimary.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        @BindView(R.id.location_primary_text)
        TextView primaryText;

        @BindView(R.id.location_secondary_text)
        TextView secondaryText;


        public PlaceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
