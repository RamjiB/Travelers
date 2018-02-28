package ramji.travelers.add_post;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import ramji.travelers.R;

public class AddPost extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);

        if (getResources().getBoolean(R.bool.is_phone))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        FragmentManager fm = getSupportFragmentManager();
        AddPostFragment addPostFragment = new AddPostFragment();
        fm.beginTransaction().add(R.id.add_post_fragment, addPostFragment).commit();

    }
}
