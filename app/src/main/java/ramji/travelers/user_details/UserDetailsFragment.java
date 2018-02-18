package ramji.travelers.user_details;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
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
import ramji.travelers.EditProfileActivity;
import ramji.travelers.GlideApp;
import ramji.travelers.R;

public class UserDetailsFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "UserDetailsFragment";
    private static final String IMAGE_URL = "image_url";
    private static final String USERNAME = "profile_name";
    private static final String USER_CITY = "city";
    private static final String ABOUT_ME = "about_me";

    @BindView(R.id.profile_image)
    ImageView profile_image;

    @BindView(R.id.profile_name)
    TextView profile_name;

    @BindView(R.id.city)
    TextView city;

    @BindView(R.id.aboutMe)
    TextView aboutMe;

    @BindView(R.id.edit_profile)
    TextView editProfile;

    private String username;
    private String userCity;
    private String aboutUser;
    private String imageUrl = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details,container,false);
        ButterKnife.bind(this,view);

        Log.i(TAG,"UserDetailsFragment");
        setProfileImage();
        setOtherDetails();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra(IMAGE_URL,imageUrl);
                intent.putExtra(USERNAME,username);
                intent.putExtra(USER_CITY,userCity);
                intent.putExtra(ABOUT_ME,aboutUser);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setOtherDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(getString(R.string.dbname_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (Objects.equals(userId, singleSnapshot.getKey())){
                        username = singleSnapshot
                                .child(getString(R.string.username))
                                .getValue().toString();

                        userCity = singleSnapshot
                                .child(getString(R.string.city))
                                .getValue().toString();

                        aboutUser = singleSnapshot
                                .child(getString(R.string.about_me))
                                .getValue().toString();

                    }
                }

                profile_name.setText(username);
                city.setText(userCity);
                aboutMe.setText(aboutUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileImage() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child(getString(R.string.dbname_users));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (Objects.equals(userId, singleSnapshot.getKey())){
                          imageUrl = singleSnapshot
                                .child(getString(R.string.profile_image_path))
                                .getValue().toString();

                        GlideApp
                                .with(getActivity())
                                .load(imageUrl)
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.ic_default_profile_image)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
