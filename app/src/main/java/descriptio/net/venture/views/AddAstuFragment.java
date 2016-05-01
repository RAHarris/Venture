package descriptio.net.venture.views;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddAstuFragment.OnAstuAddedListener} interface
 * to handle interaction events.
 * Use the {@link AddAstuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAstuFragment extends Fragment {

    private OnAstuAddedListener mListener;
    private boolean permissionAvailable = false;
    private List<File> files;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FILES = 14;
    private final String LOGCAT_TAG = "AddAstuFragment";

    public AddAstuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddAstuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAstuFragment newInstance() {
        AddAstuFragment fragment = new AddAstuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_astu, container, false);
        files = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_FILES);
        } else {
            permissionAvailable = true;
        }
        getFiles();
        ListView fileView = (ListView) view.findViewById(R.id.file_list);
        fileView.setAdapter(new ArrayAdapter<File>(getContext(), R.layout.add_astu_file_item, files));
        fileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = files.get(position);
                PeriegesisDbHelper helper = new PeriegesisDbHelper(getContext());
                if (!helper.getAsteaPathnames().contains(file.getPath())) {
                    helper.addAstu(file.getPath(), AstuStateContract.LocTypes.external);
                }
            }
        });
        return view;
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
}
