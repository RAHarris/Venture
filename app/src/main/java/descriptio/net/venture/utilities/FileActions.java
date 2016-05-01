package descriptio.net.venture.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rahar on 4/17/2016.
 */
public class FileActions {

    private static final String LOGCAT_TAG = "FileActions";

    public static Set<File> filesOfTypeInPath(File path, String type) {
        Set<File> files = new HashSet<>();
        Log.i("root node path", path.getAbsolutePath());
        files = writeFileTree(path, type, files);
        return files;
    }

    private static Set<File> writeFileTree(File file, String type, Set<File> set) {
        if (file.canRead()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    set = writeFileTree(f, type, set);
                }
            } else if (file.isFile() && file.getName().endsWith(".json")) {
                Log.i(LOGCAT_TAG, "adding file to set: " + file.getName());
                set.add(file);
            }
        }
        return set;
    }
}
