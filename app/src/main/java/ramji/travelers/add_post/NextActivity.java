package ramji.travelers.add_post;


import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.FirebaseMethods;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

@SuppressWarnings("ALL")
public class NextActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,PlaceAdapter.onItemClickListener{

    private static final String TAG = "NextActivity";
    private static final String IMAGE_URL = "imageUrl";

    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @BindView(R.id.crossImage)
    ImageView crossImage;

    @BindView(R.id.shareImageView)
    ImageView selectedImage;

    @BindView(R.id.imageDescriptionTV)
    EditText mCaption;

    @BindView(R.id.location_search)
    AutoCompleteTextView mLocation;

    @BindView(R.id.currentLocation)
    TextView currentLocation;

    @BindView(R.id.add_post_share)
    TextView share;

    @BindView(R.id.location_adapter)
    RecyclerView locationAdapter;

    @BindView(R.id.layoutView)
    RelativeLayout layout;

    @BindView(R.id.mProgressBar)
    ProgressBar progressBar;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;

    private int imageCount = 0;
    private String imageUrl;
    private Context mContext;

    //Google places vars
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private AutocompletePredictionBufferResponse prediction;
    private PlaceAdapter placeAdapter;
    private ArrayList<String> placesPrimary;
    private ArrayList<String> placesSecondary;
    private PlaceAdapter.onItemClickListener onItemClickListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ButterKnife.bind(this);

        hideSoftKeyboard();
        if (getResources().getBoolean(R.bool.is_phone))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mFirebaseMethods = new FirebaseMethods(this);
        setupFirebaseAuth();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });

        mContext = this;
        onItemClickListener = this;

        locationAdapter.setLayoutManager(new LinearLayoutManager(this));

        mGeoDataClient = Places.getGeoDataClient(this,null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this,null);

        buildGoogleApiClient();
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: closing the gallery fragment");
                finish();
            }
        });

        if (getIntent().hasExtra(IMAGE_URL)){

            imageUrl = getIntent().getStringExtra(IMAGE_URL);

            Log.i(TAG,"imageUrl: "+ imageUrl);

            GlideApp
                    .with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(selectedImage);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String caption = mCaption.getText().toString();
                    String location = mLocation.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,
                            imageCount,imageUrl,location,progressBar);
                }
            });

        }

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            NextActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                    NextActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(NextActivity.this, new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, REQUEST_CODE);
                    }
                    return;
                }

                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient
                        .getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        CharSequence address = "";
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            address =  placeLikelihood.getPlace().getAddress();
                            Log.i(TAG,"address: "+ address);
                        }
                        likelyPlaces.release();

                        mLocation.setText(address);
                    }
                });
            }
        });

        //mLocation text changed
        mLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                retrievePlaces(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void retrievePlaces(CharSequence s){
        Log.i(TAG,"retrievePlaces");

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();

        Task<AutocompletePredictionBufferResponse> results = mGeoDataClient
                .getAutocompletePredictions(s.toString(),null,filter);

        Log.i(TAG,"result:" + results);

        results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                if (task.isSuccessful()){

                    Log.i(TAG,"result Successful");
                    prediction = task.getResult();
                    Log.i(TAG,"prediction: "+ prediction.getCount());
                    placesPrimary = new ArrayList<>(prediction.getCount());
                    placesSecondary = new ArrayList<>(prediction.getCount());
                    for (int i = 0; i < prediction.getCount(); i++) {
                        placesPrimary.add(prediction.get(i).getPrimaryText(null).toString());
                        placesSecondary.add(prediction.get(i).getSecondaryText(null).toString());
                    }

                    Log.i(TAG, "places: " + placesPrimary);

                    placeAdapter = new PlaceAdapter(placesPrimary,placesSecondary,onItemClickListener);
                    locationAdapter.setAdapter(placeAdapter);

                }else
                    Log.e(TAG,"task failed: "+ task.getException());
            }
        });
    }

    private void buildGoogleApiClient() {
        Log.i(TAG, "Lifecycle: buildGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void askForGPS() {

        Log.i(TAG, "Lifecycle: askForGPS");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(NextActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            break;
                        }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "Lifecycle: onRequestPermissionsResult");
        if (ActivityCompat.checkSelfPermission(mContext, permissions[0])
                == PackageManager.PERMISSION_GRANTED) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                askForGPS();
                Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient
                        .getCurrentPlace(null);
                placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                        CharSequence address = "";
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            address =  placeLikelihood.getPlace().getAddress();
                            Log.i(TAG,"address: "+ address);
                        }
                        likelyPlaces.release();

                        mLocation.setText(address);
                    }
                });
            }

        }if (ActivityCompat.checkSelfPermission(mContext,permissions[1]) ==
                PackageManager.PERMISSION_GRANTED){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"permission granted");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Lifecycle: onStart");
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int position) {

        locationAdapter.setVisibility(View.INVISIBLE);
        mLocation.setText(placesPrimary.get(position));
        hideSoftKeyboard();

    }

    private void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    this.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Setup the firebase auth object
     */

    private void setupFirebaseAuth(){
        Log.d(TAG,"setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();
        Log.d(TAG,"onDataChange: image count: "+ imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG,"onDataChange: image count: "+ imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
