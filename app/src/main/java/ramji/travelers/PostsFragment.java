package ramji.travelers;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostsFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "PostsFragment";

    @BindView(R.id.rv_image_holder)
    RecyclerView imageHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_post,container,false);
        ButterKnife.bind(this,view);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        imageHolder.setLayoutManager(layoutManager);

        ImagesStaggeredAdapter adapter = new ImagesStaggeredAdapter(getContext());
        imageHolder.setAdapter(adapter);
        return view;
    }
}
