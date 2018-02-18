package ramji.travelers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.add_post.AddPost;
import ramji.travelers.login.ProfileLoginFragment;
import ramji.travelers.posts.PostsFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.postButton)
    Button posts;

    @BindView(R.id.profileButton)
    Button profile;

    @BindView(R.id.fragmentPart)
    FrameLayout fragmentPart;

    @BindView(R.id.toolBar)
    Toolbar toolbar;

    private ProfileLoginFragment profileLoginFragment;
    private PostsFragment postsFragment;
    private boolean fromSignUpActivity;
    private android.support.v4.app.FragmentManager fm;

    //Firebase
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        postsFragment = new PostsFragment();
        fm = getSupportFragmentManager();

        profileLoginFragment = new ProfileLoginFragment();

        setupFirebaseAuth();

        if (getIntent().hasExtra("fromSignUpActivity")){
            fromSignUpActivity = getIntent().hasExtra("fromSignUpActivity");
        }

        Log.i(TAG,"fromSignUpActivity: "+fromSignUpActivity);

        if (!fromSignUpActivity) {

            fm.beginTransaction()
                    .add(R.id.fragmentPart, postsFragment).commit();

        }else {


            fm.beginTransaction().replace(R.id.fragmentPart, profileLoginFragment).commit();
            profile.setBackgroundColor(getColor(R.color.colorPrimary));
            posts.setBackgroundColor(getColor(R.color.white));

        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setBackgroundColor(getColor(R.color.colorPrimary));
                posts.setBackgroundColor(getColor(R.color.white));
                fm.beginTransaction()
                        .replace(R.id.fragmentPart, profileLoginFragment).commit();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setBackgroundColor(getColor(R.color.white));
                posts.setBackgroundColor(getColor(R.color.colorPrimary));
                fm.beginTransaction().replace(R.id.fragmentPart, postsFragment).commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.menu_add:
                if (mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(HomeActivity.this, AddPost.class);
                    startActivity(intent);
                }else{
                    profileLoginFragment = new ProfileLoginFragment();
                    profile.setBackgroundColor(getColor(R.color.colorPrimary));
                    posts.setBackgroundColor(getColor(R.color.white));
                    fm.beginTransaction()
                            .replace(R.id.fragmentPart, profileLoginFragment).commit();
                }
                return true;

            case R.id.menu_signOut:
                if (mAuth.getCurrentUser() != null){
                    mAuth.signOut();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupFirebaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                    Log.d(TAG,"onAuthStateChanged: signed_in "+ user.getUid());
                else
                    Log.d(TAG,"onAuthStateChanged: signed_out");
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
