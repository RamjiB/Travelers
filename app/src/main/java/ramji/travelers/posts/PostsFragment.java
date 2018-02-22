package ramji.travelers.posts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.HomeActivity;
import ramji.travelers.R;
import ramji.travelers.image_details.ImageDetailsView;

public class PostsFragment extends android.support.v4.app.Fragment implements
        PostImagesAdapter.ImageClickListener{

    private static final String TAG = "PostsFragment";
    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String PHOTO_ID = "photo_id";
    private static final String FILE_TYPE = "file_type";

    @BindView(R.id.rv_image_holder)
    RecyclerView imageHolder;

    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;

    private PostImagesAdapter.ImageClickListener imageClickListener;
    private GridLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post,container,false);
        ButterKnife.bind(this,view);

        imageClickListener = this;
        imageHolder.setHasFixedSize(true);

        mProgressBar.setVisibility(View.VISIBLE);
        if (getResources().getBoolean(R.bool.is_phone)) {
             layoutManager = new GridLayoutManager(getContext(), 2);
        }else{
            layoutManager = new GridLayoutManager(getContext(),4);
        }
        imageHolder.setLayoutManager(layoutManager);

        getImageUrlsAndLocation();
        return view;
    }

    private void getImageUrlsAndLocation() {

        Log.i(TAG,"getImageUrlsAndLocation");

        final ArrayList<String> imageUrls = new ArrayList<>();
        final ArrayList<String> location = new ArrayList<>();
        final ArrayList<String> caption = new ArrayList<>();
        final ArrayList<String> photo_id = new ArrayList<>();
        final ArrayList<String> fileType = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(getString(R.string.dbname_photos));
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

                PostImagesAdapter adapter = new PostImagesAdapter(getContext(),
                        imageClickListener,
                        imageUrls,
                        location,
                        caption,photo_id,fileType);
                imageHolder.setAdapter(adapter);
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void imageClick(String imageUrl, String postLocation,
                           String description,String photo_id,String fileType) {

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
