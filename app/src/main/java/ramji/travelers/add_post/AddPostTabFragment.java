package ramji.travelers.add_post;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.Utils.FilePaths;
import ramji.travelers.Utils.FileSearch;
import ramji.travelers.R;

import static android.app.Activity.RESULT_OK;


public class AddPostTabFragment extends android.support.v4.app.Fragment implements
        GalleryImagesGridAdapter.ImageClickListener {

    private static final String TAG = "AddPostTabFragment";

    private static final int REQUEST_CODE = 100;

    private static final String IMAGE_URL = "imageUrl";
    private static final String POST_LOCATION = "postLocation";
    private static final String IMAGE_DESCRIPTION = "description";

    private GalleryImagesGridAdapter.ImageClickListener imageClickListener;

    private int position;
    private static final int GALLERY = 0;
    private static final int CAMERA = 1;
    private static final int VIDEO = 2;

    //vars
    private ArrayList<String> directories = new ArrayList<>();
    private ArrayList<String> Dcim = new ArrayList<>();
    private String mAppend = "file:/";
    private String mSelectedImage;

    private boolean storagePermission;
    private boolean cameraPermission;

    @BindView(R.id.user_images_rv)
    RecyclerView imagesRecyclerView;

    @BindView(R.id.spinnerDirectory)
    Spinner spinnerDirectory;

    @BindView(R.id.add_post_next)
    TextView next;

    @BindView(R.id.crossImage)
    ImageView crossImage;

    public static AddPostTabFragment getInstance(int position){

        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        AddPostTabFragment tabFragment = new AddPostTabFragment();
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
        View view = inflater.inflate(R.layout.add_post_gal_rv,container,false);
        ButterKnife.bind(this,view);
        imageClickListener = this;

        Log.i(TAG,"Tab Fragment: onCreateView");

        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: closing the gallery fragment");
                getActivity().finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImage != null) {
                    Intent intent = new Intent(getContext(), NextActivity.class);
                    intent.putExtra(IMAGE_URL, mSelectedImage);
                    startActivity(intent);
                }
            }
        });



        switch (position){
            case GALLERY:

                askForPermission();
                if (storagePermission)
                    galleryView();

                break;
            case CAMERA:

                askForPermission();
                if (cameraPermission)
                    cameraView();
                break;
            case VIDEO:
                break;


        }

        return view;
    }

    private void cameraView() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,REQUEST_CODE);
    }

    private void galleryView() {

        FilePaths filePaths = new FilePaths();

        //check for other directories
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        if (FileSearch.getDirectoryPaths(filePaths.DCIM) != null)
            Dcim = FileSearch.getDirectoryPaths(filePaths.DCIM);

        directories.addAll(Dcim);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size();i++){
            int index = directories.get(i).lastIndexOf("/")+1;
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDirectory.setAdapter(adapter);

        spinnerDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                imagesRecyclerView.setLayoutManager(gridLayoutManager);

                ArrayList<String> imageUrls = FileSearch.getFilePaths(directories.get(position));

                GalleryImagesGridAdapter adapter = new GalleryImagesGridAdapter(getContext(),
                        imageClickListener
                        ,imageUrls);
                imagesRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void askForPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA},REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, REQUEST_CODE);
            }
        }else{
            storagePermission = true;
            cameraPermission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0])
                == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG,"permissionGranted");
                galleryView();
            }
        }else if (ActivityCompat.checkSelfPermission(getActivity(),permissions[1]) ==
                PackageManager.PERMISSION_GRANTED){
            if (grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                cameraView();
            }
        }
    }

    @Override
    public void imageClick(String imageUrl) {

        mSelectedImage = imageUrl;
        Log.i(TAG,"mSelectedImage: "+ mSelectedImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),photo,
                    "CI"+ System.currentTimeMillis()+".jpeg",null);
            Cursor cursor = getActivity().getContentResolver()
                    .query(Uri.parse(path),null,null,null,null);

            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            File file = new File(cursor.getString(idx));

            Log.i(TAG,"file: "+ file);

        }
    }
}
