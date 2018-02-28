package ramji.travelers.user_details;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import ramji.travelers.R;

class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "AddPostViewPagerAdapter";

    private Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.i(TAG, "fm: " + fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "AddPostViewPagerAdapter: getItem");
        return TabFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "AddPostViewPagerAdapter: getCount");
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        Log.i(TAG, "AddPostViewPagerAdapter: getPageTitle");
        switch (position) {
            case 0:
                return context.getString(R.string.shared);
            case 1:
                return context.getString(R.string.saved);
        }
        return null;
    }
}
