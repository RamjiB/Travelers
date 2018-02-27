package ramji.travelers.Utils;


import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        Log.i(TAG,"file: "+ file);
        File[] listFiles = file.listFiles();
        Log.i(TAG,"listFiles: "+ listFiles.length);
        for (File listFile : listFiles) {
            if (listFile.isDirectory()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */

    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isFile()) {
                pathArray.add(listFile.getAbsolutePath());
            }
        }
        return pathArray;
    }
}
