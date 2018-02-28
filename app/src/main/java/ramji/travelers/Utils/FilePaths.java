package ramji.travelers.Utils;


import android.os.Environment;

public class FilePaths {

    //"storage/emulated/0"
    private final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

    public final String PICTURES = ROOT_DIR + "/Pictures";
    public final String DCIM = ROOT_DIR + "/DCIM";


    public final String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
