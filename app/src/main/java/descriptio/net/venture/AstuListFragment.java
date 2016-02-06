package descriptio.net.venture;

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
        View view = inflater.inflate(R.layout.fragment_astulist_list, container, false);
        View rView = view.findViewById(R.id.astulist_recyclerview);

        astea = new ArrayList<Astu>();
        AssetManager manager = getActivity().getAssets();
        String[] asteaFiles = new String[0];
        try {
            asteaFiles = manager.list("astea");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream stream;
        for (String filename : asteaFiles){
            try {
                stream = manager.open("astea/" + filename);
            } catch (Exception e) {
                stream = null;
                Log.e("astea open", "there was a failure opening astu-seattle-0-1.json");
            }
            try {
                Astu item = new Astu(stream);
                item.filename = "astea/" + filename;
                astea.add(item);
            } catch (Exception e) {
                Log.e("astea parse failure", "there was a failure parsing the stream");
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
