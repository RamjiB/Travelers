package ramji.travelers.add_post;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.R;


public class AddPostFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "AddPostFragment";

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post,container,false);
        ButterKnife.bind(this,view);
        Log.i(TAG,"AddPostFragment");

        AddPostViewPagerAdapter adapter = new AddPostViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
