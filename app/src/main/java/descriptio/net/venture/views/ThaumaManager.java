package descriptio.net.venture.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.Manifest.permission;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import descriptio.net.venture.GeofenceTransitionsIntentService;
import descriptio.net.venture.R;
import descriptio.net.venture.io.AstuStateContract;
import descriptio.net.venture.io.PeriegesisDbHelper;
import descriptio.net.venture.models.Astu;
import descriptio.net.venture.models.Thauma;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThaumaManager.OnThaumaManagerInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThaumaManager#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ThaumaManager extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    public static final String ARG_ASTU_ID = "astu_id";
    public static final String ARG_THAUMA_UID = "thauma_uid";
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 92;   // can be any magic number < 127

    public MapView mapView;
    public GoogleMap mMap;
    public Circle mCircle;

    private Astu mAstu;
    private Thauma mThauma;
    private GoogleApiClient mApiClient;

    private boolean geofenceRequested;
    private boolean permissionAvailable;
    private boolean apiAvailable;

    private RequestQueue requestQueue;
    private OnThaumaManagerInteractionListener mListener;
    private final String LOGCAT_TAG = "ThaumaManager";

    //TODO: this class does too much, think about splitting up
    public ThaumaManager() {
        // Required empty public constructor
    }

    /**
     * @param thauma_uid Unique identifier of the thauma selected.
     * @return A new instance of fragment ThaumaManager.
     */
    public static ThaumaManager newInstance(String thauma_uid) {
        ThaumaManager fragment = new ThaumaManager();
        Bundle args = new Bundle();
        args.putString(ARG_THAUMA_UID, thauma_uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            final long astuId = getArguments().getLong(ARG_ASTU_ID);
            final int thaumaId = getArguments().getInt(ARG_THAUMA_UID);
            PeriegesisDbHelper dbHelper = new PeriegesisDbHelper(getContext());
            String path = dbHelper.getAstuDetails(astuId)[0];
            int locType = Integer.parseInt(dbHelper.getAstuDetails(astuId)[1]);
            InputStream stream;
            // TODO: refactor this logic into a utilities class
            try {
                if (locType == AstuStateContract.LocTypes.asset.ordinal()) {
                    AssetManager manager = getActivity().getAssets();
                    stream = manager.open(path);
                    mAstu = new Astu(stream, astuId);
                } else if (locType == AstuStateContract.LocTypes.external.ordinal()) {
                    stream = new FileInputStream(path);
                    mAstu = new Astu(stream, astuId);
                } else if (locType == AstuStateContract.LocTypes.cloud.ordinal()) {
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(LOGCAT_TAG, "successful response???");
                            mAstu = new Astu(response, astuId);
                            for (Thauma item : mAstu.getThaumata()) {
                                if (item.getUid() == thaumaId) {
                                    mThauma = item;
                                    updateView();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(LOGCAT_TAG, "some problem with the response", error);
                        }
                    });
                    request.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 3));
                    requestQueue.add(request);
                } else {
                    Log.e(LOGCAT_TAG, "didn't recognize locType " + locType);
                    stream = null;
                }
            } catch (Exception e) {
                Log.e(LOGCAT_TAG, "problem with file located at " + path, e);
            }
            if (mAstu != null) {
                for (Thauma item : mAstu.getThaumata()) {
                    if (item.getUid() == thaumaId) {
                        mThauma = item;
                    }
                }
            }
        }
        if (mThauma == null) {
            Log.i(LOGCAT_TAG, "failed to load a thuama from arguments");
        } else {
            Log.i(LOGCAT_TAG, "successfully loaded thauma " + mThauma.getName());
            mApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mApiClient.connect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thauma_manager, container, false);
        mapView = (MapView) view.findViewById(R.id.thauma_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        updateView();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        if (context instanceof OnThaumaManagerInteractionListener) {
            mListener = (OnThaumaManagerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnThaumaManagerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) throws SecurityException {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAvailable = true;
                    mMap.setMyLocationEnabled(true);
                    setMapOptions();
                }
                else {
                    // do nothing; the user has said we're not allowed to know where they are
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            permissionAvailable = true;
            mMap.setMyLocationEnabled(true);
            setMapOptions();
        }
    }

    public void setMapOptions() {
        Log.i("current coordinates", "now at: " + mThauma.getCoords()[0] + ", " + mThauma.getCoords()[1]);
        LatLng currentLoc = new LatLng(mThauma.getCoords()[0], mThauma.getCoords()[1]);
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(currentLoc, 16);
        mMap.moveCamera(center);
        CircleOptions options = new CircleOptions().center(currentLoc).radius(100);
        mCircle = mMap.addCircle(options);
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        apiAvailable = true;
        updateGeofence(mThauma.getCoords(), "123456");
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiAvailable = false;
        Log.i(LOGCAT_TAG, "waiting for reconnect");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        apiAvailable = false;
        Log.e(LOGCAT_TAG, "connection to GoogleServices failed");
    }

    @Override
    public void onResult(Status status) {
        Log.i(LOGCAT_TAG, status.toString());
    }

    public interface OnThaumaManagerInteractionListener {
        // TODO: Is this necessary? What am I communicating back?
        void onThaumaManagerInteraction(Uri uri);
    }

    private void updateGeofence(double[] location, String id) throws SecurityException {
        Log.i(LOGCAT_TAG, "fenceRequested: " + geofenceRequested + ", permissionAvailable: " + permissionAvailable + ", apiAvailable: " + apiAvailable);
        if (geofenceRequested && permissionAvailable && apiAvailable) {
            Log.i(LOGCAT_TAG, "setting geofence");
            Geofence.Builder builder = new Geofence.Builder();
            builder.setCircularRegion(location[0], location[1], 100);
            builder.setLoiteringDelay(5000);
            builder.setNotificationResponsiveness(180000);
            builder.setRequestId(id);
            builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
            builder.setExpirationDuration(6000000);
            Geofence fence = builder.build();
            GeofencingRequest request = new GeofencingRequest.Builder().addGeofence(fence).build();
            Intent intent = new Intent(this.getContext(), GeofenceTransitionsIntentService.class);
            intent.putExtra(ARG_ASTU_ID, mAstu.id);
            intent.putExtra(ARG_THAUMA_UID, mThauma.getUid());
            LocationServices.GeofencingApi
                    .addGeofences(mApiClient, request, PendingIntent.getService(
                            this.getContext(), 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setResultCallback(this);
        }
    }

    private void updateView() {
        View view = getView();
        if (mThauma != null) {
            TextView nameView = (TextView) view.findViewById(R.id.manager_name);
            TextView descriptionView = (TextView) view.findViewById(R.id.manager_description);
            nameView.setText(mThauma.getName());
            descriptionView.setText(mThauma.getDescription());

            ToggleButton toggle = (ToggleButton) view.findViewById(R.id.geofence_toggle);
            toggle.setChecked(false);
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mCircle != null) {
                        if (isChecked) {
                            Log.i(LOGCAT_TAG, "isChecked: true");
                            geofenceRequested = true;
                            mCircle.setFillColor(R.color.primary);
                            updateGeofence(mThauma.getCoords(), "123456");

                        } else {
                            Log.i(LOGCAT_TAG, "isChecked: false");
                            mCircle.setFillColor(0);
                        }
                    }
                }
            });
        }
    }
}
