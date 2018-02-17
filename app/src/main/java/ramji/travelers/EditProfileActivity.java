package ramji.travelers;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity{

    private static final String TAG = "EditProfileActivity";

    private static final int REQUEST_CODE = 100;

    private boolean storagePermission;
    private String filePath;

    @BindView(R.id.EP_crossImage)
    ImageView crossImage;

    @BindView(R.id.EP_Done)
    TextView EP_done;

    @BindView(R.id.EP_username)
    EditText userName;

    @BindView(R.id.EP_userCity)
    EditText city;

    @BindView(R.id.EP_aboutUser)
    EditText aboutMe;

    @BindView(R.id.EP_profile_image)
    ImageView profileImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        GlideApp
                .with(this)
                .load(filePath)
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.ic_default_profile_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(profileImage);

        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: closing the gallery fragment");
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askForPermission();
                if (storagePermission)
                    pickImage();

            }
        });

        EP_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString();
                String location = city.getText().toString();
                String about = aboutMe.getText().toString();

                Log.i(TAG,"name: "+ name);

                FirebaseMethods firebaseMethods = new FirebaseMethods(getBaseContext());
                firebaseMethods.uploadProfilePhoto(about,name,filePath,location,null);

            }
        });

    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        final int ACTIVITY_SELECT_IMAGE = 1234;
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();

                    Log.i(TAG,"filePath: "+ filePath);
                    GlideApp
                            .with(this)
                            .load(filePath)
                            .placeholder(R.drawable.loading_image)
                            .error(R.drawable.ic_default_profile_image)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profileImage);
                }
        }

    }

    private void askForPermission() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }else{
            storagePermission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, permissions[0])
                == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"permissionGranted");
                pickImage();
            }
        }
    }

}
