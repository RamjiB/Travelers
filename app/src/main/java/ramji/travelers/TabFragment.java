package ramji.travelers;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class TabFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TabFragment";

    private int position;
    private RecyclerView imagesRecyclerView;

    public static TabFragment getInstance(int position){

        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_rv_images,container,false);
        ButterKnife.bind(this,view);

        return view;
    }
}
