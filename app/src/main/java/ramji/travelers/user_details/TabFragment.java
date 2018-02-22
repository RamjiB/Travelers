package ramji.travelers.user_details;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.HomeActivity;
import ramji.travelers.R;
import ramji.travelers.image_details.ImageDetailsView;

public class TabFragment extends android.support.v4.app.Fragment implements
        ImagesGridAdapter.ImageClickListener{

    private static final String TAG = "AddPostTabFragment";

    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String PHOTO_ID = "photo_id";
    private static final String FILE_TYPE = "file_type";

    private int position;
    private ImagesGridAdapter.ImageClickListener imageClickListener;
    private GridLayoutManager gridLayoutManager;

    @BindView(R.id.user_images_rv)
    RecyclerView imagesRecyclerView;

    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;

    public static TabFragment getInstance(int position){

        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"Tab Fragment: onCreate");
        if (getArguments() != null){

            position = getArguments().getInt("position");
            Log.i(TAG,"position: "+ position);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_rv_images,container,false);
        ButterKnife.bind(this,view);
        imageClickListener = this;

        Log.i(TAG,"Tab Fragment: onCreateView");

        mProgressBar.setVisibility(View.VISIBLE);
        if (getResources().getBoolean(R.bool.is_phone))
            gridLayoutManager = new GridLayoutManager(getContext(),3);
        else
            gridLayoutManager = new GridLayoutManager(getContext(),5);

        imagesRecyclerView.setLayoutManager(gridLayoutManager);

        switch(position){
            case 0:
                getImageUrlsAndLocation(getString(R.string.dbname_user_photos));
                break;
            case 1:
                getImageUrlsAndLocation(getString(R.string.dbname_saved_photos));
                break;
        }

        return view;
    }

    private void getImageUrlsAndLocation(String photoType) {

        final ArrayList<String> imageUrls = new ArrayList<>();
        final ArrayList<String> location = new ArrayList<>();
        final ArrayList<String> caption = new ArrayList<>();
        final ArrayList<String> photo_id = new ArrayList<>();
        final ArrayList<String> fileType = new ArrayList<>();
        Query query = null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (Objects.equals(photoType, getString(R.string.dbname_saved_photos))) {
            query = databaseReference.child(getString(R.string.dbname_saved_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }else{
            query = databaseReference.child(getString(R.string.dbname_user_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        if (query != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                        imageUrls.add(singleSnapshot.child(getString(R.string.db_image_path))
                                .getValue().toString());
                        Log.i(TAG,"imageUrls: "+ imageUrls);
                        location.add(singleSnapshot.child(getString(R.string.db_location))
                                .getValue().toString());
                        caption.add(singleSnapshot.child(getString(R.string.db_caption))
                                .getValue().toString());
                        photo_id.add(singleSnapshot.child(getString(R.string.db_photo_id))
                                .getValue().toString());
                        fileType.add(singleSnapshot.child(getString(R.string.db_fileType))
                                .getValue().toString());

                    }

                    ImagesGridAdapter adapter = new ImagesGridAdapter(getContext(),
                            imageClickListener,
                            imageUrls,
                            location,
                            caption,photo_id,fileType);
                    imagesRecyclerView.setAdapter(adapter);
                    mProgressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void imageClick(String imageUrl, String postLocation, String description,
                           String photo_id,String fileType) {

        if (getResources().getBoolean(R.bool.is_phone)) {

            Intent intent = new Intent(getContext(), ImageDetailsView.class);
            intent.putExtra(IMAGE_URL, imageUrl);
            intent.putExtra(POST_LOCATION, postLocation);
            intent.putExtra(IMAGE_DESCRIPTION, description);
            intent.putExtra(PHOTO_ID, photo_id);
            intent.putExtra(FILE_TYPE, fileType);
            startActivity(intent);
        }else{

            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.putExtra(IMAGE_URL, imageUrl);
            intent.putExtra(POST_LOCATION, postLocation);
            intent.putExtra(IMAGE_DESCRIPTION, description);
            intent.putExtra(PHOTO_ID, photo_id);
            intent.putExtra(FILE_TYPE, fileType);
            startActivity(intent);
        }

    }
}
