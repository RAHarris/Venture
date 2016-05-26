package descriptio.net.venture.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import descriptio.net.venture.R;
import descriptio.net.venture.VentureActivity;
import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.models.Astu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Astu items.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AstuListFragment extends Fragment {

    private static final String LOGCAT_TAG = "AstuListFragment";

    private RequestQueue requestQueue;
    private OnListFragmentInteractionListener mListener;
    private MyAstuListRecyclerViewAdapter adapter;
    List<Astu> astea;
    AssetManager manager;

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

        astea = getAstea();
        adapter = new MyAstuListRecyclerViewAdapter(getContext(), astea, mListener);
        if (rView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) rView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
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

    public List<Astu> getAstea() {
        PeriegesisDbHelper dbHelper = new PeriegesisDbHelper(getContext());
        List<Astu> asteaList = new ArrayList<>();
        manager = getActivity().getAssets();
        List<String[]> asteaFiles = dbHelper.getAsteaDetails();
        for (String[] curFile : asteaFiles){
            Astu item = readAstu(curFile);
            if (item != null) {
                asteaList.add(item);
            }
        }
        return asteaList;
    }

    public void refreshAstea() {
        astea = getAstea();
        adapter.swap(astea);
    }

    private Astu readAstu(String[] paths) {
        Astu result = null;
        final long index = Long.parseLong(paths[0]);
        String path = paths[1];
        int locType = Integer.parseInt(paths[2]);
        Log.i(LOGCAT_TAG, "index: " + index + ", path: " + path + ", locType: " + locType);
        InputStream stream;
        try {
            if (locType == AstuStateContract.LocTypes.asset.ordinal()) {
                stream = manager.open(path);
                result = new Astu(stream, index);
            } else if (locType == AstuStateContract.LocTypes.external.ordinal()) {
                stream = new FileInputStream(path);
                result = new Astu(stream, index);
            } else if (locType == AstuStateContract.LocTypes.cloud.ordinal()) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOGCAT_TAG, "successful response???");
                        astea.add(new Astu(response, index));
                        adapter.swap(astea);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOGCAT_TAG, "some problem with the response", error);
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 3));
                requestQueue.add(request);
                return null;
            } else {
                Log.e(LOGCAT_TAG, "didn't recognize locType " + locType);
                stream = null;
            }
        } catch (Exception e) {
            Log.e(LOGCAT_TAG, "problem with file located at " + path, e);
        }
        return result;
    }
}
