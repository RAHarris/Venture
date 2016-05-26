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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import descriptio.net.venture.R;
import descriptio.net.venture.VentureActivity;
import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.io.RefreshAstuListListenerInterface;
import descriptio.net.venture.utilities.FileActions;


public class AddHostedAstuDialogFragment extends DialogFragment {

    private RefreshAstuListListenerInterface mListener;

    private final String LOGCAT_TAG = "AddHostedAstuDialog";

    public AddHostedAstuDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddHostedAstuFragment.
     */
    public static AddHostedAstuDialogFragment newInstance() {
        AddHostedAstuDialogFragment fragment = new AddHostedAstuDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a file from a web location")
                .setView(input)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String filepath = input.getText().toString();
                        try {
                            URL url = new URL(filepath);
                            PeriegesisDbHelper helper = new PeriegesisDbHelper(getContext());
                            helper.addAstu(filepath, AstuStateContract.LocTypes.cloud);
                        } catch (MalformedURLException e) {
                            Log.e(LOGCAT_TAG, "That URL is invalid and will not be added");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RefreshAstuListListenerInterface) {
            mListener = (RefreshAstuListListenerInterface) context;
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
}
