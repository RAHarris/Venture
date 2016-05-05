package descriptio.net.venture.dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import descriptio.net.venture.R;
import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.utilities.FileActions;


public class AddLocalAstuDialogFragment extends DialogFragment {

    private OnAstuAddedListener mListener;
    private boolean permissionAvailable = false;
    private List<File> files;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FILES = 14;
    private final String LOGCAT_TAG = "AddLocalAstuDialogFragment";

    public AddLocalAstuDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddAstuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddLocalAstuDialogFragment newInstance() {
        AddLocalAstuDialogFragment fragment = new AddLocalAstuDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        files = new ArrayList<>();
        final List<File> selectedItems = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_FILES);
        } else {
            permissionAvailable = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        getFiles();
        builder.setTitle("Choose a file from your device")
                .setMultiChoiceItems(getFilesArray(), null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(files.get(which));
                        } else if (selectedItems.contains(files.get(which))){
                            selectedItems.remove(files.get(which));
                        }
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        PeriegesisDbHelper helper = new PeriegesisDbHelper(getContext());
                        for (File file : selectedItems) {
                            if (!helper.getAsteaPathnames().contains(file.getPath())) {
                                helper.addAstu(file.getPath(), AstuStateContract.LocTypes.external);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        return builder.create();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) throws SecurityException {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FILES:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permissionAvailable = true;
                    getFiles();
                } else {
                    // do nothing; the user has said we're not allowed to read their files
                }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAstuAdded(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAstuAddedListener) {
            mListener = (OnAstuAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAstuAddedListener {
        // TODO: Update argument type and name
        void onAstuAdded(Uri uri);
    }

    private void getFiles() {
        Log.i(LOGCAT_TAG, "calling getFiles with permissionAvailable set to: " + permissionAvailable);
        if (permissionAvailable) {
            files.addAll(FileActions.filesOfTypeInPath(Environment.getExternalStorageDirectory(), ".json"));
        }
    }

    private String[] getFilesArray() {
        String[] array = new String[files.size()];
        for (int i = 0; i < files.size(); i ++) {
            array[i] = files.get(i).getName();
        }
        return array;
    }
}
