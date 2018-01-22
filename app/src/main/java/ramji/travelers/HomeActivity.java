package ramji.travelers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.postButton)
    Button posts;

    @BindView(R.id.profileButton)
    Button profile;
    @BindView(R.id.fragmentPart)
    FrameLayout fragmentPart;

    private ProfileFragment profileFragment;
    private PostsFragment postsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        postsFragment = new PostsFragment();
        profileFragment = new ProfileFragment();

        final FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragmentPart,postsFragment).commit();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setBackgroundColor(getColor(R.color.colorPrimary));
                posts.setBackgroundColor(getColor(R.color.white));
                fm.beginTransaction()
                        .replace(R.id.fragmentPart,profileFragment).commit();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setBackgroundColor(getColor(R.color.white));
                posts.setBackgroundColor(getColor(R.color.colorPrimary));
                fm.beginTransaction().replace(R.id.fragmentPart,postsFragment).commit();
            }
        });

    }
}
