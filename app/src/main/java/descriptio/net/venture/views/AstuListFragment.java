package descriptio.net.venture.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import descriptio.net.venture.R;
import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.models.Astu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Astu items.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AstuListFragment extends Fragment {

    private static final String LOGCAT_TAG = "AstuListFragment";

    private OnListFragmentInteractionListener mListener;
    List<Astu> astea;

    public AstuListFragment() {
    }

    public static AstuListFragment newInstance() {
        AstuListFragment fragment = new AstuListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PeriegesisDbHelper dbHelper = new PeriegesisDbHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_astulist_list, container, false);
        View rView = view.findViewById(R.id.astulist_recyclerview);

        astea = new ArrayList<>();
        AssetManager manager = getActivity().getAssets();
        List<String[]> asteaFiles = dbHelper.getAsteaPaths();
        InputStream stream;
        for (String[] curFile : asteaFiles){
            try {
                stream = manager.open(curFile[1]);
            } catch (Exception e) {
                stream = null;
                Log.e(LOGCAT_TAG, "there was a failure opening " + curFile[1]);
            }
            try {
                Astu item = new Astu(stream, Long.parseLong(curFile[0]));
                astea.add(item);
            } catch (Exception e) {
                Log.e(LOGCAT_TAG, "there was a failure parsing the stream");
            }
        }

        if (rView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) rView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyAstuListRecyclerViewAdapter(getContext(), astea, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListItemClicked(Astu item);
    }
}
