package info.guardianproject.chime;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.guardianproject.chime.db.ChimeAdapter;
import info.guardianproject.chime.db.ChimeEventAdapter;
import info.guardianproject.chime.geo.GeoManager;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;
import info.guardianproject.chime.service.ChimeNotifier;
import info.guardianproject.chime.service.WifiJobService;
import info.guardianproject.chime.service.WifiReceiver;

import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private SwipeRefreshLayout refreshLayout;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (hasPermissions()) {

                    listenForChimes();

                    if (navigation.getSelectedItemId() == R.id.navigation_home)
                        showHomeList();
                    else if (navigation.getSelectedItemId() == R.id.navigation_dashboard)
                        showDashboardList();
                    else
                        showChimeEventList();
                }

                refreshLayout.setRefreshing(false);
            }
        });

        List<Chime> chimeList = Chime.listAll(Chime.class);

        if (chimeList.size() == 0)
        {
            startActivity(new Intent(this,OnboardingActivity.class));
        }
        else {
            if (hasPermissions())
                showHomeList();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        showHomeList();

        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver,intentFilter);

        new ChimeNotifier(this).clearAll();
    }

    WifiReceiver wifiReceiver;

    ChimeAdapter chimeAdapter;
    ChimeEventAdapter chimeEventAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chimeAdapter.onDestroy();
        beaconManager.unbind(this);

        GeoManager.disconnect();

        unregisterReceiver(wifiReceiver);
    }

    private List<Chime> chimeList;

    private void showHomeList ()
    {

        String[] args = {"1"};
        chimeList =  Chime.find(Chime.class,"is_nearby = ?",args,null,"last_seen DESC",null);

        if (chimeList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);

            chimeAdapter = new ChimeAdapter(this, chimeList, R.layout.layout_card_large);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(chimeAdapter);
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
        }
    }

    List<Chime> chimeDashList;
    ChimeAdapter chimeDashAdapter;

    private void showDashboardList ()
    {
        chimeDashList = Chime.listAll(Chime.class);
        chimeDashList =  Chime.find(Chime.class,null,null,null,"last_seen DESC",null);

        if (chimeDashList.size() > 0) {

            recyclerView.setVisibility(View.VISIBLE);

            chimeDashAdapter = new ChimeAdapter(this, chimeDashList, R.layout.layout_card_mixed);

            RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(chimeDashAdapter);


        }
    }

    private void showChimeEventList ()
    {
        List<ChimeEvent> chimeEventList = ChimeEvent.listAll(ChimeEvent.class);
        chimeEventAdapter = new ChimeEventAdapter(this, chimeEventList, R.layout.layout_card_event_small);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chimeEventAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.menu_add_chime:
                addNewChime();
                return true;

            case R.id.menu_scan:
                listenForChimes();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewChime ()
    {
        if (hasPermissions()) {
            Intent sintent = new Intent(this, AddNewChimeActivity.class);
            startActivity(sintent);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showHomeList();
                    return true;
                case R.id.navigation_dashboard:
                    showDashboardList();
                    return true;
                case R.id.navigation_notifications:
                    showChimeEventList();
                    return true;
            }
            return false;
        }
    };

    private boolean hasPermissions ()
    {
        if (askForPermission("android.permission.ACCESS_FINE_LOCATION",1))
            return false;

        return true;
    }

    private boolean askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }

            return true;
        }

        return false;

    }

    private BeaconManager beaconManager;

    private void initBeaconSupport ()
    {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);


    }

    @Override
    public void onBeaconServiceConnect() {

        Snackbar snackbar = Snackbar.make(recyclerView, R.string.status_beacons,Snackbar.LENGTH_LONG);
        snackbar.show();

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
              //  Log.i(TAG, "I just saw an beacon for the first time!");

                String[] args = {region.getBluetoothAddress()};
                List<Chime> chimes = Chime.find(Chime.class,"ssid = ?",args);
                if (chimes.size() == 0)
                {
                    Chime chime = new Chime();

                    chime.name = region.getBluetoothAddress();
                    chime.lastSeen = new Date();
                    chime.isNearby = true;
                    chime.ssid = region.getBluetoothAddress();
                    chime.serviceType = "altbeacon";
                    chime.chimeId = region.getUniqueId();

                    if (region.getId1() != null)
                    {
                        chime.serviceUri = region.getId1().toString();
                    }

                    chime.save();

                    ChimeEvent event = new ChimeEvent();
                    event.type = ChimeEvent.TYPE_FOUND_NEW_CHIME;
                    event.happened = chime.lastSeen;
                    event.description = getString(R.string.status_beacon_found);
                    event.chimeId = chime.getId()+"";
                    event.save();

                    showHomeList();
                }
            }

            @Override
            public void didExitRegion(Region region) {
             //   Log.i(TAG, "I no longer see an beacon");
                String[] args = {region.getBluetoothAddress()};
                List<Chime> chimes = Chime.find(Chime.class,"ssid = ?",args);
                for (Chime chime : chimes)
                {
                    chime.isNearby = false;
                    chime.lastSeen = new Date();
                    chime.save();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
             //   Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("chime1", null, null, null));
        } catch (RemoteException e) {    }
    }

    private void listenForChimes ()
    {
        initBeaconSupport();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WifiJobService.initJob(this);
        }
    }
}
