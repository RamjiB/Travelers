package ramji.travelers.add_post;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import ramji.travelers.user_details.TabFragment;

public class AddPostViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "AddPostViewPagerAdapter";

    private String title[] = {"GALLERY","CAMERA","VIDEO"};

    public AddPostViewPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.i(TAG,"fm: "+ fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG,"AddPostViewPagerAdapter: getItem");
        return AddPostTabFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        Log.i(TAG,"AddPostViewPagerAdapter: getCount");
        return title.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        Log.i(TAG,"AddPostViewPagerAdapter: getPageTitle");
        return title[position];
    }
}
