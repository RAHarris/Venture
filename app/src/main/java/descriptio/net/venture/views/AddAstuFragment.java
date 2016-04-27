package descriptio.net.venture.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

import descriptio.net.venture.R;
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
        ListView fileView = (ListView) view.findViewById(R.id.file_list);
        File[] files = FileActions.filesInPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        Log.i(LOGCAT_TAG, "there are " + files.length + " files in downloads");
        for (File file : files) {
            try {
              Log.i("possible file", file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return view;
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
}
