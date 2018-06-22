package info.guardianproject.pinwheel;

import android.Manifest;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.util.ArrayList;

import info.guardianproject.pinwheel.db.ChimeAdapter;
import info.guardianproject.pinwheel.model.Chime;

import static com.mapzen.android.lost.api.Geofence.NEVER_EXPIRE;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    showHomeList();
                    return true;
                case R.id.navigation_dashboard:
  //                  mTextMessage.setText(R.string.title_dashboard);
                    showDashboardList();
                    return true;
                case R.id.navigation_notifications:
    //                mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        showHomeList ();
    }

    ArrayList<Chime> chimeList;
    ChimeAdapter chimeAdapter;
    RecyclerView recyclerView;

    private void showHomeList ()
    {
        chimeList = new ArrayList<>();
        chimeAdapter = new ChimeAdapter(this, chimeList, R.layout.layout_card_large);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chimeAdapter);

        Chime chime = new Chime();
        chime.name = "This is a RECENT chime";

        chimeList.add(chime);
        chimeAdapter.notifyDataSetChanged();
    }

    private void showDashboardList ()
    {
        chimeList = new ArrayList<>();
        chimeAdapter = new ChimeAdapter(this, chimeList, R.layout.layout_card_mixed);

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chimeAdapter);

        for (int i = 0; i < 30; i++) {
            Chime chime = new Chime();
            chime.name = "This is a chime " + i;
            chimeList.add(chime);
        }

        chimeAdapter.notifyDataSetChanged();
    }

    /**
    public void onWindowFocusChanged(boolean hasFocus) {
        AnimationDrawable loadingAnimation = (AnimationDrawable)
                findViewById(R.id.img_loading).getBackground();
        if (hasFocus) {
            loadingAnimation.start();
        }
        else {
            loadingAnimation.stop();
        }
    }**/
}
