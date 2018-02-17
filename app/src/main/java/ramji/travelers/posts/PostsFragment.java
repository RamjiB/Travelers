package ramji.travelers.posts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.R;
import ramji.travelers.image_details.ImageDetailsView;

public class PostsFragment extends android.support.v4.app.Fragment implements
        ImagesStaggeredAdapter.ImageClickListener{

    private static final String TAG = "PostsFragment";
    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String PHOTO_ID = "photo_id";

    @BindView(R.id.rv_image_holder)
    RecyclerView imageHolder;

    private ImagesStaggeredAdapter.ImageClickListener imageClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_post,container,false);
        ButterKnife.bind(this,view);

        imageClickListener = this;
        imageHolder.setHasFixedSize(true);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        imageHolder.setLayoutManager(layoutManager);

        getImageUrlsAndLocation();
        return view;
    }

    private void getImageUrlsAndLocation() {
        final ArrayList<String> imageUrls = new ArrayList<>();
        final ArrayList<String> location = new ArrayList<>();
        final ArrayList<String> caption = new ArrayList<>();
        final ArrayList<String> photo_id = new ArrayList<>();
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

                }

                ImagesStaggeredAdapter adapter = new ImagesStaggeredAdapter(getContext(),
                        imageClickListener,
                        imageUrls,
                        location,
                        caption,photo_id);
                imageHolder.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void imageClick(String imageUrl, String postLocation, String description,String photo_id) {

        Intent intent = new Intent(getContext(), ImageDetailsView.class);
        intent.putExtra(IMAGE_URL,imageUrl);
        intent.putExtra(POST_LOCATION,postLocation);
        intent.putExtra(IMAGE_DESCRIPTION,description);
        intent.putExtra(PHOTO_ID,photo_id);
        startActivity(intent);

    }
}
