package descriptio.net.venture;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import descriptio.net.venture.dialogs.AddHostedAstuDialogFragment;
import descriptio.net.venture.dialogs.ManageAstuListDialogFragment;
import descriptio.net.venture.io.RefreshAstuListListenerInterface;
import descriptio.net.venture.models.Astu;
import descriptio.net.venture.models.Thauma;
import descriptio.net.venture.views.AstuListFragment;
import descriptio.net.venture.views.ThaumaListFragment;
import descriptio.net.venture.views.ThaumaManager;

import descriptio.net.venture.dialogs.AddLocalAstuDialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class VentureActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            AstuListFragment.OnListFragmentInteractionListener,
            ThaumaListFragment.OnThaumaFragmentInteractionListener,
            ThaumaManager.OnThaumaManagerInteractionListener,
            RefreshAstuListListenerInterface {

    private String LIST_FRAGMENT_NAME = "LIST_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setContentView(R.layout.view_astu_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabLocal = (FloatingActionButton) findViewById(R.id.fab_local);
        fabLocal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment localFragment = new AddHostedAstuDialogFragment();
                localFragment.show(getSupportFragmentManager(), "addhosted");
            }
        });

        FloatingActionButton fabCloud = (FloatingActionButton) findViewById(R.id.fab_cloud);
        fabCloud.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment localFragment = new AddLocalAstuDialogFragment();
                localFragment.show(getSupportFragmentManager(), "addlocal");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (intent != null &&
                intent.getLongExtra(ThaumaManager.ARG_ASTU_ID, -1) != -1 &&
                intent.getIntExtra(ThaumaManager.ARG_THAUMA_UID, -1) != -1) {
            ThaumaManager detail = new ThaumaManager();
            Bundle args = new Bundle();
            args.putLong(ThaumaManager.ARG_ASTU_ID, intent.getLongExtra(ThaumaManager.ARG_ASTU_ID, -1));
            args.putInt(ThaumaManager.ARG_THAUMA_UID, intent.getIntExtra(ThaumaManager.ARG_THAUMA_UID, -1));
            detail.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_list_container, detail);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            if (findViewById(R.id.frag_list_container) != null) {
                if (savedInstanceState != null) {
                    return;
                }
                AstuListFragment listFragment = new AstuListFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.frag_list_container, listFragment, LIST_FRAGMENT_NAME).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.astu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_delete) {
            DialogFragment manageListFragment = new ManageAstuListDialogFragment();
            manageListFragment.show(getSupportFragmentManager(), "manage");
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListItemClicked(Astu item) {
        Log.i("clicked on item: ", item.toString());
        ThaumaListFragment detail = new ThaumaListFragment();
        Bundle args = new Bundle();
        args.putLong(ThaumaListFragment.ARG_ASTU_ID, item.id);
        detail.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_list_container, detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onThaumaFragmentInteraction(Astu astu, Thauma thauma) {
        Log.i("clicked on item: ", thauma.toString());
        ThaumaManager detail = new ThaumaManager();
        Bundle args = new Bundle();
        args.putLong(ThaumaManager.ARG_ASTU_ID, astu.id);
        args.putInt(ThaumaManager.ARG_THAUMA_UID, thauma.getUid());
        detail.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_list_container, detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onThaumaManagerInteraction(Uri uri) {

    }

    public void refreshAstea() {
        AstuListFragment fragment = (AstuListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_NAME);
        fragment.refreshAstea();
    }
}
