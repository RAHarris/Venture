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
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.models.Astu;
import descriptio.net.venture.models.Thauma;

import java.io.InputStream;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnThaumaFragmentInteractionListener}
 * interface.
 */
public class ThaumaListFragment extends Fragment {

    private final String LOGCAT_TAG = "ThaumaListFragment";
    public static final String ARG_ASTU_ID = "astu_id";

    private OnThaumaFragmentInteractionListener mListener;
    private Astu astu;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ThaumaListFragment() {
    }

    public static ThaumaListFragment newInstance(int columnCount) {
        ThaumaListFragment fragment = new ThaumaListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            PeriegesisDbHelper dbHelper = new PeriegesisDbHelper(getContext());
            long id = getArguments().getLong(ARG_ASTU_ID);
            String filename = dbHelper.getAstuPath(id);
            AssetManager manager = getActivity().getAssets();
            InputStream stream;
            try {
                stream = manager.open(filename);
            } catch (Exception e) {
                stream = null;
                Log.e(LOGCAT_TAG, "there was a failure opening " + filename);
            }
            try {
                astu = new Astu(stream, id);
            } catch (Exception e) {
                Log.e(LOGCAT_TAG, "there was a failure parsing the stream");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thaumalist_list, container, false);
        View rView = view.findViewById(R.id.thaumalist_recyclerview);

        // Set the adapter
        if (rView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) rView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyThaumaListRecyclerViewAdapter(astu, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnThaumaFragmentInteractionListener) {
            mListener = (OnThaumaFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnThaumaFragmentInteractionListener {
        // TODO: Update argument type and name
        void onThaumaFragmentInteraction(Astu astu, Thauma thauma);
    }
}
