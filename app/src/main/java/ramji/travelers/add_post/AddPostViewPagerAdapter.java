package ramji.travelers.add_post;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import ramji.travelers.R;

class AddPostViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "AddPostViewPagerAdapter";

    private Context context;

    public AddPostViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.i(TAG, "fm: " + fm);
        this.context= context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "position: " + position);
        Log.i(TAG, "AddPostViewPagerAdapter: getItem");
        return AddPostTabFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "AddPostViewPagerAdapter: getCount");
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        Log.i(TAG, "AddPostViewPagerAdapter: getPageTitle");
        switch (position){
            case 0:
                return context.getString(R.string.gallery);
            case 1:
                return context.getString(R.string.camera);
            case 2:
                return context.getString(R.string.video);
        }
        return null;
    }
}
