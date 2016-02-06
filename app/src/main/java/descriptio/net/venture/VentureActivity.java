package descriptio.net.venture;

import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import descriptio.net.venture.models.Astu;
import descriptio.net.venture.models.Thauma;

public class VentureActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            AstuListFragment.OnListFragmentInteractionListener,
            AddAstuFragment.OnAddAstuInteractionListener,
            ThaumaListFragment.OnThaumaFragmentInteractionListener,
            ThaumaManager.OnThaumaManagerInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_astu_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAstuFragment newView = new AddAstuFragment();
                Bundle args = new Bundle();
                newView.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_list_container, newView);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.frag_list_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            AstuListFragment listFragment = new AstuListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frag_list_container, listFragment).commit();
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

        } else if (id == R.id.nav_manage) {

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
        args.putString(ThaumaListFragment.ARG_ASTU_FILENAME, item.filename);
        detail.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_list_container, detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onThaumaFragmentInteraction(Thauma thauma) {
        Log.i("clicked on item: ", thauma.toString());
        ThaumaManager detail = new ThaumaManager();
        Bundle args = new Bundle();
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

    public void onAddAstuInteraction(Uri uri) {
    }
}
