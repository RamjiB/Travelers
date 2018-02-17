package ramji.travelers.user_details;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "AddPostViewPagerAdapter";

    private String title[] = {"Shared","Saved"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.i(TAG,"fm: "+ fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG,"AddPostViewPagerAdapter: getItem");
        return TabFragment.getInstance(position);
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
