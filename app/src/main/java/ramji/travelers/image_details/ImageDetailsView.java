package ramji.travelers.image_details;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.FavouritePlaceUpdatedWidget;
import ramji.travelers.FirebaseMethods;
import ramji.travelers.GlideApp;
import ramji.travelers.HomeActivity;
import ramji.travelers.R;


public class ImageDetailsView extends AppCompatActivity {

    private static final String TAG = "ImageDetailsView";

    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String PHOTO_ID = "photo_id";
    private static final String FILE_TYPE = "file_type";

    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.postLocation)
    TextView postLocation;

    @BindView(R.id.imageDescriptionTV)
    TextView imageDescription;

    @BindView(R.id.notFav)
    ImageView notFav;

    @BindView(R.id.fav)
    ImageView fav;

    @BindView(R.id.crossImage)
    ImageView crossImage;

    @BindView(R.id.videoView)
    SimpleExoPlayerView videoView;

    private SimpleExoPlayer player;

    private String getImageUrl;
    private String getPostLocation;
    private String getImageDescription;
    private String getPhotoId;
    private boolean favourite;
    private String getFileType;
    private String userId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail_view);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        if (intent.hasExtra(IMAGE_URL) && intent.hasExtra(POST_LOCATION) &&
                intent.hasExtra(IMAGE_DESCRIPTION) && intent.hasExtra(PHOTO_ID)
                && intent.hasExtra(FILE_TYPE)){
            getImageUrl = intent.getStringExtra(IMAGE_URL);
            getPostLocation = intent.getStringExtra(POST_LOCATION);
            getImageDescription = intent.getStringExtra(IMAGE_DESCRIPTION);
            getPhotoId = intent.getStringExtra(PHOTO_ID);
            getFileType = intent.getStringExtra(FILE_TYPE);
            Log.i(TAG,"imageUrl: "+ getImageUrl);

        }else{
            getPostLocation = "";
            getImageDescription ="";
        }

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

    public void setVideoPlayer() {

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
    protected void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.release();
    }
}
