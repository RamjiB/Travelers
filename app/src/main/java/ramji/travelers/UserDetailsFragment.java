package ramji.travelers;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class UserDetailsFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "UserDetailsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details,container,false);
        ButterKnife.bind(this,view);

        return view;
    }
}
