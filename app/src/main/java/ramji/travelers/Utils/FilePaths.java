package ramji.travelers.Utils;


import android.os.Environment;

public class FilePaths {
    private static final String TAG = "FilePaths";

    //"storage/emulated/0"
    private String ROOT_DIR =Environment.getExternalStorageDirectory().getAbsolutePath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String DCIM = ROOT_DIR + "/DCIM";


    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
