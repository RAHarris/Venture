package descriptio.net.venture.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.io.RefreshAstuListListenerInterface;

/**
 * Created by rahar on 5/25/2016.
 */
public class ManageAstuListDialogFragment extends DialogFragment {

    private RefreshAstuListListenerInterface mListener;

    private final String LOGCAT_TAG = "ManageAstuListDialogFragment";

    public ManageAstuListDialogFragment() {
        // Required empty public constructor
    }

    public static ManageAstuListDialogFragment newInstance() {
        ManageAstuListDialogFragment fragment = new ManageAstuListDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final PeriegesisDbHelper dbHelper = new PeriegesisDbHelper(getContext());
        final String[] paths = dbHelper.getAsteaPathnames().toArray(new String[0]);
        final List<String> selected = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Check items to remove")
                .setMultiChoiceItems(paths, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selected.add(paths[which]);
                        } else if (selected.contains(paths[which])){
                            selected.remove(paths[which]);
                        }
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (String path: selected){
                            dbHelper.removeAstuForPath(path);
                        }
                        mListener.refreshAstea();
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
    }
}
