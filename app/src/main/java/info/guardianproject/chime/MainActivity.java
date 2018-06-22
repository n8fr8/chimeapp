package info.guardianproject.chime;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import info.guardianproject.chime.db.ChimeAdapter;
import info.guardianproject.chime.db.ChimeEventAdapter;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;

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

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasPermissions())
                    showHomeList();

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

    }

    ChimeAdapter chimeAdapter;
    ChimeEventAdapter chimeEventAdapter;

    RecyclerView recyclerView;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chimeAdapter.onDestroy();
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

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    chimeList.remove(viewHolder.getAdapterPosition());
                    chimeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
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
        chimeEventAdapter = new ChimeEventAdapter(this, chimeEventList, R.layout.layout_card_small);

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

}
