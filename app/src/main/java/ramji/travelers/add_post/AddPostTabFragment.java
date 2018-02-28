package ramji.travelers.add_post;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ramji.travelers.Utils.FilePaths;
import ramji.travelers.Utils.FileSearch;
import ramji.travelers.R;

import static android.app.Activity.RESULT_OK;


@SuppressWarnings("ALL")
public class AddPostTabFragment extends android.support.v4.app.Fragment implements
        GalleryImagesGridAdapter.ImageClickListener, SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "AddPostTabFragment";

    private static final int REQUEST_CODE = 100;
    private static final int IMAGE_REQUEST_CODE = 101;

    private static final String IMAGE_URL = "imageUrl";

    private GalleryImagesGridAdapter.ImageClickListener imageClickListener;

    private int position;
    private static final int GALLERY = 0;
    private static final int CAMERA = 1;
    private static final int VIDEO = 2;

    //vars
    private ArrayList<String> directories = new ArrayList<>();
    private ArrayList<String> Dcim = new ArrayList<>();
    private String mSelectedImage;

    private boolean readStoragePermission;
    private boolean writeStoragePermission;
    private boolean cameraPermission;

    private String capturedPhotoPath;
    private android.hardware.Camera mCamera;

    //Video vars
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private SurfaceHolder holder;
    private boolean recording = false;

    @BindView(R.id.user_images_rv)
    RecyclerView imagesRecyclerView;

    @BindView(R.id.spinnerDirectory)
    Spinner spinnerDirectory;

    @BindView(R.id.add_post_next)
    TextView next;

    @BindView(R.id.crossImage)
    ImageView crossImage;

    @BindView(R.id.capture_video)
    ImageView videoCaptureIcon;

    @BindView(R.id.openCameraBt)
    Button openCamera;

    @BindView(R.id.videoView)
    SurfaceView videoView;

    public static AddPostTabFragment getInstance(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        AddPostTabFragment tabFragment = new AddPostTabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Tab Fragment: onCreate");
        if (getArguments() != null) {

            position = getArguments().getInt("position");
            Log.i(TAG, "position: " + position);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_post_gal_rv, container, false);
        ButterKnife.bind(this, view);
        imageClickListener = this;

        Log.i(TAG, "Tab Fragment: onCreateView");

        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment");
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

        switch (position) {

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
                        Log.i(TAG, "writeStoragePermission: " + writeStoragePermission);
                        Log.i(TAG, "cameraPermission: " + cameraPermission);
                        if (cameraPermission && writeStoragePermission)
                            cameraView();
                    }
                });

                break;

            case VIDEO:

                spinnerDirectory.setVisibility(View.INVISIBLE);
                imagesRecyclerView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoCaptureIcon.setVisibility(View.VISIBLE);
                videoCaptureIcon.setClickable(true);
                videoCaptureIcon.setOnClickListener(this);
                break;
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startVideoRecording() throws IOException {
        mCamera = getCameraInstance();
        Log.i(TAG, "startVideoRecording");

        mediaRecorder = new MediaRecorder();
        holder = videoView.getHolder();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(holder);
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private void cameraView() {
        Log.i(TAG, "cameraView");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(getContext(),
                        getString(R.string.authority),
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST_CODE);
            }
        }
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
        for (int i = 0; i < directories.size(); i++) {
            int index = directories.get(i).lastIndexOf("/") + 1;
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
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
                        , imageUrls);
                imagesRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void askForPermission() {

        Log.i(TAG, "askForPermission");

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
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
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
        } else {
            readStoragePermission = true;
            cameraPermission = true;
            writeStoragePermission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0])
                == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "permissionGranted");
                galleryView();
                readStoragePermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[1]) ==
                PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "write storage Permission granted");
                writeStoragePermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[2]) ==
                PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "camera Permission granted");
                cameraPermission = true;
            }
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[3]) ==
                PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "audio Permission granted");
                cameraPermission = true;
            }
        }

        Log.i(TAG, "readStoragePermission: " + readStoragePermission);
        Log.i(TAG, "writeStoragePermission: " + writeStoragePermission);
        Log.i(TAG, "cameraPermission: " + cameraPermission);
    }

    @Override
    public void imageClick(String imageUrl) {

        mSelectedImage = imageUrl;
        Log.i(TAG, "mSelectedImage: " + mSelectedImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult: ");

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(capturedPhotoPath);
            Uri uri = Uri.fromFile(file);
            mediaIntent.setData(uri);
            getActivity().sendBroadcast(mediaIntent);

            Log.i(TAG, "file: " + file);
            mSelectedImage = String.valueOf(file);
            if (mSelectedImage != null) {
                Intent intent = new Intent(getContext(), NextActivity.class);
                intent.putExtra(IMAGE_URL, mSelectedImage);
                startActivity(intent);
            }

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(getString(R.string.date_format), Locale.US).format(new Date());
        String imageFileName = getString(R.string.image_name) + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                getString(R.string.image_format),
                storageDir
        );

        capturedPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mediaRecorder != null)
            prepareRecorder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.i(TAG, "surfaceDestroyed");
        if (recording) {
            mediaRecorder.stop();
            recording = false;
            mCamera.stopPreview();
            mCamera.release();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {

        Log.i(TAG, "VideoView: onClick");
        if (recording) {
            videoCaptureIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_capture));
            mediaRecorder.stop();
            recording = false;
            mediaRecorder.release();
            mediaRecorder = null;
            mCamera.stopPreview();
            mCamera.release();
        } else {
            recording = true;
            try {
                startVideoRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
            videoCaptureIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_bt));
            initRecorder();
            String file = new FilePaths().PICTURES +
                    getString(R.string.video_name) + System.currentTimeMillis() + getString(R.string.video_format);
            mediaRecorder.setOutputFile(file);
            mSelectedImage = file;
            Log.i(TAG, "mSelectedImage: " + mSelectedImage);
            prepareRecorder();
            mediaRecorder.start();
        }
    }

    private void prepareRecorder() {
        Log.i(TAG, "prepareRecorder");
        mediaRecorder.setPreviewDisplay(holder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initRecorder() {

        if (mediaRecorder != null) {

            Log.i(TAG, "initRecorder");
            mCamera.unlock();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setPreviewDisplay(holder.getSurface());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

            CamcorderProfile camcorderProfile = null;
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                camcorderProfile = CamcorderProfile
                        .get(CamcorderProfile.QUALITY_HIGH);
            }
            mediaRecorder.setProfile(camcorderProfile);
            mediaRecorder.setOrientationHint(90);
            mediaRecorder.setMaxDuration(40000); // 40 sec
            mediaRecorder.setMaxFileSize(50000000); //50 MB
        }

    }

    private static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera c = null;
        try {
            c = android.hardware.Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
