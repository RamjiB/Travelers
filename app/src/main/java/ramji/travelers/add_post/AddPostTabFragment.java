package ramji.travelers.add_post;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.GlideApp;
import ramji.travelers.Utils.FilePaths;
import ramji.travelers.Utils.FileSearch;
import ramji.travelers.R;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class AddPostTabFragment extends android.support.v4.app.Fragment implements
        GalleryImagesGridAdapter.ImageClickListener {

    private static final String TAG = "AddPostTabFragment";

    private static final int REQUEST_CODE = 100;
    private static final int IMAGE_REQUEST_CODE = 101;
    private static final int VIDEO_REQUEST_CODE = 102;

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

    private boolean readStoragePermission;
    private boolean writeStoragePermission;
    private boolean cameraPermission;

    //Video vars
    private Uri videofileUri;

    @BindView(R.id.user_images_rv)
    RecyclerView imagesRecyclerView;

    @BindView(R.id.spinnerDirectory)
    Spinner spinnerDirectory;

    @BindView(R.id.add_post_next)
    TextView next;

    @BindView(R.id.crossImage)
    ImageView crossImage;

    @BindView(R.id.cameraImage)
    ImageView cameraImage;

    @BindView(R.id.openCameraBt)
    Button openCamera;

    @BindView(R.id.videoView)
    VideoView videoView;

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
                if (readStoragePermission)
                    galleryView();

                break;

            case CAMERA:

                spinnerDirectory.setVisibility(View.INVISIBLE);
                imagesRecyclerView.setVisibility(View.INVISIBLE);
                openCamera.setVisibility(View.VISIBLE);
                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askForPermission();
                        Log.i(TAG,"writeStoragePermission: "+ writeStoragePermission);
                        Log.i(TAG,"cameraPermission: "+ cameraPermission);
                        if (cameraPermission && writeStoragePermission)
                            cameraView();
                    }
                });

                break;
            case VIDEO:

                spinnerDirectory.setVisibility(View.INVISIBLE);
                imagesRecyclerView.setVisibility(View.INVISIBLE);
                openCamera.setText(R.string.open_video);
                openCamera.setVisibility(View.VISIBLE);
                openCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askForPermission();
                        Log.i(TAG,"writeStoragePermission: "+ writeStoragePermission);
                        Log.i(TAG,"cameraPermission: "+ cameraPermission);
                        if (cameraPermission && writeStoragePermission){
                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivityForResult(videoIntent,VIDEO_REQUEST_CODE);
                        }
                    }
                });
                break;
        }

        return view;
    }

    private void cameraView() {
        Log.i(TAG,"cameraView");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,IMAGE_REQUEST_CODE);
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

        Log.i(TAG,"askForPermission");

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.RECORD_AUDIO) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)  &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CAMERA) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.RECORD_AUDIO)) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_CODE);
            }
            return;
        }else{
            readStoragePermission = true;
            cameraPermission = true;
            writeStoragePermission =true;
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
                readStoragePermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(),permissions[1]) ==
                PackageManager.PERMISSION_GRANTED){
            if (grantResults.length>0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"write storage Permission granted");
                writeStoragePermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(),permissions[2]) ==
                PackageManager.PERMISSION_GRANTED){
            if (grantResults.length>0 && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"camera Permission granted");
                cameraPermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(),permissions[3]) ==
                PackageManager.PERMISSION_GRANTED){
            if (grantResults.length>0 && grantResults[3] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"audio Permission granted");
                cameraPermission = true;
            }
        }

        Log.i(TAG,"readStoragePermission: "+ readStoragePermission);
        Log.i(TAG,"writeStoragePermission: "+ writeStoragePermission);
        Log.i(TAG,"cameraPermission: "+ cameraPermission);
    }

    @Override
    public void imageClick(String imageUrl) {

        mSelectedImage = imageUrl;
        Log.i(TAG,"mSelectedImage: "+ mSelectedImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG,"onActivityResult: ");

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),photo,
                    "CI"+ System.currentTimeMillis()+".jpeg",null);
            Cursor cursor = getActivity().getContentResolver()
                    .query(Uri.parse(path),null,null,null,null);

            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            File file = new File(cursor.getString(idx));
            cursor.close();

            Log.i(TAG,"file: "+ file);

            openCamera.setVisibility(View.INVISIBLE);
            cameraImage.setVisibility(View.VISIBLE);

            GlideApp
                    .with(getContext())
                    .load(file)
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(cameraImage);

            mSelectedImage = String.valueOf(file);

        }

        if (requestCode == VIDEO_REQUEST_CODE && resultCode == RESULT_OK){

            Log.i(TAG,"videoCaptured");

            Log.i(TAG,"videoFile Path: "+ data.getData());


            openCamera.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(data.getData());

            MediaController mc = new MediaController(getContext());
            videoView.setMediaController(mc);
            videoView.start();
            mSelectedImage = data.getData().toString();

        }
    }
}
