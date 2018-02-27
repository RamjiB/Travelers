package ramji.travelers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.add_post.AddPost;
import ramji.travelers.login.ProfileLoginFragment;
import ramji.travelers.posts.PostsFragment;

@SuppressWarnings("ALL")
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String PHOTO_ID = "photo_id";
    private static final String FILE_TYPE = "file_type";

    @BindView(R.id.postButton)
    Button posts;

    @BindView(R.id.profileButton)
    Button profile;

    @BindView(R.id.fragmentPart)
    FrameLayout fragmentPart;

    @BindView(R.id.toolBar)
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.add_new_FAB)
    FloatingActionButton addPostFAB;

    @Nullable
    @BindView(R.id.image)
    ImageView image;

    @Nullable
    @BindView(R.id.postLocation)
    TextView postLocation;

    @Nullable
    @BindView(R.id.imageDescriptionTV)
    TextView imageDescription;

    @Nullable
    @BindView(R.id.notFav)
    ImageView notFav;

    @Nullable
    @BindView(R.id.fav)
    ImageView fav;

    @Nullable
    @BindView(R.id.crossImage)
    ImageView crossImage;

    @Nullable
    @BindView(R.id.videoView)
    SimpleExoPlayerView videoView;

    @Nullable
    @BindView(R.id.bts_layout)
    LinearLayout bt_layout;

    @Nullable
    @BindView(R.id.user_details_layout)
    CardView userDetailsLayout;

    private ProfileLoginFragment profileLoginFragment;
    private PostsFragment postsFragment;
    private boolean fromSignUpActivity;
    private android.support.v4.app.FragmentManager fm;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private SimpleExoPlayer player;

    private String getImageUrl;
    private String getPostLocation;
    private String getImageDescription;
    private String getPhotoId;
    private boolean favourite;
    private String getFileType;
    private String userId = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);


        fm = getSupportFragmentManager();

        setupFirebaseAuth();

        if (getIntent().hasExtra("fromSignUpActivity")) {
            fromSignUpActivity = getIntent().hasExtra("fromSignUpActivity");
        }

        Log.i(TAG, "fromSignUpActivity: " + fromSignUpActivity);

        if (savedInstanceState == null) {

            if (!fromSignUpActivity) {
                postsFragment = new PostsFragment();
                fm.beginTransaction()
                        .add(R.id.fragmentPart, postsFragment).commit();
            } else {

                fm.beginTransaction().replace(R.id.fragmentPart, profileLoginFragment).commit();
                profile.setBackgroundColor(getColor(R.color.colorPrimary));
                posts.setBackgroundColor(getColor(R.color.white));

            }
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profile.setBackgroundColor(getColor(R.color.colorPrimary));
                posts.setBackgroundColor(getColor(R.color.white));
                profile.setElevation(
                        getResources().getDimension(R.dimen.raised_button_pressed_elevation));
                posts.setElevation(
                        getResources().getDimension(R.dimen.raised_button_resting_elevation));
                Log.i(TAG,"postFragment not added");
                profileLoginFragment = new ProfileLoginFragment();
                fm.beginTransaction()
                        .replace(R.id.fragmentPart, profileLoginFragment).commit();

            }

        });


        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsFragment = new PostsFragment();
                profile.setBackgroundColor(getColor(R.color.white));
                posts.setBackgroundColor(getColor(R.color.colorPrimary));
                profile.setElevation(
                        getResources().getDimension(R.dimen.raised_button_resting_elevation));
                fm.beginTransaction().replace(R.id.fragmentPart, postsFragment).commit();
            }
        });

        if (!getResources().getBoolean(R.bool.is_phone)) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i(TAG,"tabletView: "+ getResources().getBoolean(R.bool.is_phone));
            addPostFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(HomeActivity.this, AddPost.class);
                        startActivity(intent);
                    } else {
                        profileLoginFragment = new ProfileLoginFragment();
                        profile.setBackgroundColor(getColor(R.color.colorPrimary));
                        posts.setBackgroundColor(getColor(R.color.white));
                        fm.beginTransaction()
                                .replace(R.id.fragmentPart, profileLoginFragment).commit();
                    }
                }
            });

            Intent intent = getIntent();

            if (intent.hasExtra(IMAGE_URL) && intent.hasExtra(POST_LOCATION) &&
                    intent.hasExtra(IMAGE_DESCRIPTION) && intent.hasExtra(PHOTO_ID)
                    && intent.hasExtra(FILE_TYPE)) {
                Log.i(TAG, "intentHasExtra: " + intent);
                getImageUrl = intent.getStringExtra(IMAGE_URL);
                getPostLocation = intent.getStringExtra(POST_LOCATION);
                getImageDescription = intent.getStringExtra(IMAGE_DESCRIPTION);
                getPhotoId = intent.getStringExtra(PHOTO_ID);
                getFileType = intent.getStringExtra(FILE_TYPE);
                Log.i(TAG, "imageUrl: " + getImageUrl);
                userDetailsLayout.setVisibility(View.VISIBLE);
                bt_layout.setVisibility(View.VISIBLE);
                imageDetailsForTabView();
            }

        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    private void imageDetailsForTabView() {
        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!Objects.equals(getFileType, "image/jpeg")){
            image.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            setVideoPlayer();

        }else{

            GlideApp
                    .with(this)
                    .load(getImageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(image);
            postLocation.setText(getPostLocation);
            imageDescription.setText(getImageDescription);
        }

        try {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.i(TAG, "userId: " + userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!Objects.equals(userId, "")) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child(getString(R.string.dbname_saved_photos))
                    .child(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        if (Objects.equals(getPhotoId, singleSnapshot.getKey())) {
                            favourite = Boolean.parseBoolean(singleSnapshot
                                    .child(getString(R.string.favourite)).getValue().toString());

                            Log.i(TAG, "favourite: " + favourite);

                            if (favourite) {
                                notFav.setVisibility(View.INVISIBLE);
                                fav.setVisibility(View.VISIBLE);
                            } else {
                                notFav.setVisibility(View.VISIBLE);
                                fav.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        notFav.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Objects.equals(userId, "")) {

                    Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                    intent.putExtra("fromSignUpActivity",true);
                    startActivity(intent);

                } else {
                    notFav.setVisibility(View.INVISIBLE);
                    fav.setVisibility(View.VISIBLE);
                    favourite = true;

                    FirebaseMethods firebaseMethods = new FirebaseMethods(getBaseContext());
                    firebaseMethods.addSavedPhotos(getImageDescription, getPostLocation
                            , getImageUrl, favourite, getPhotoId, getFileType);

                    FavouritePlaceUpdatedWidget.startActionUpdateFavWidgets(getBaseContext());
                }
            }
        });


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notFav.setVisibility(View.VISIBLE);
                fav.setVisibility(View.INVISIBLE);
                favourite = false;

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child(getString(R.string.dbname_saved_photos))
                        .child(userId);
                Log.i(TAG, "query: " + query);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(getPhotoId, singleSnapshot.getKey())) {
                                favourite = Boolean.parseBoolean(singleSnapshot
                                        .child(getString(R.string.favourite))
                                        .getValue().toString());

                                Log.i(TAG, "favourite: " + favourite);

                                if (favourite) {
                                    singleSnapshot.getRef().removeValue();
                                    FavouritePlaceUpdatedWidget
                                            .startActionUpdateFavWidgets(getBaseContext());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    private void setVideoPlayer() {

        //Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        //Create player
        player = ExoPlayerFactory.newSimpleInstance(this,trackSelector,loadControl);

        //Set media controller
        videoView.setUseController(true);
        videoView.requestFocus();

        // Bind the player to the view.
        videoView.setResizeMode(3);
        videoView.setPlayer(player);

        //Video Source
        Uri videoUri = Uri.parse(getImageUrl);

        //Measures bandwidth uring playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();

        //Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ramji.travelers"), bandwidthMeterA);

        //Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //This is the MediaSource representing the media to be played:
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        // Prepare the player with the source.
        player.prepare(loopingSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        player.setPlayWhenReady(true); //run file/link when ready to play.

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        MenuItem menuItem = menu.findItem(R.id.menu_add);
        if (!getResources().getBoolean(R.bool.is_phone)){
            menuItem.setVisible(false);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.release();
    }

}
