package info.guardianproject.chime;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import info.guardianproject.chime.db.ChimeAdapter;
import info.guardianproject.chime.db.ChimeEventAdapter;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;

public class MainActivity extends AppCompatActivity {

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

        List<Chime> chimeList = Chime.listAll(Chime.class);

        if (chimeList.size() == 0)
        {
            startActivity(new Intent(this,OnboardingActivity.class));
        }
        else
            showHomeList ();
    }

    ChimeAdapter chimeAdapter;
    ChimeEventAdapter chimeEventAdapter;

    RecyclerView recyclerView;

    private void showHomeList ()
    {
        List<Chime> chimeList = Chime.listAll(Chime.class);
        chimeAdapter = new ChimeAdapter(this, chimeList, R.layout.layout_card_large);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chimeAdapter);

      //  chimeAdapter.notifyDataSetChanged();
    }

    private void showDashboardList ()
    {
        List<Chime> chimeList = Chime.listAll(Chime.class);
        chimeAdapter = new ChimeAdapter(this, chimeList, R.layout.layout_card_mixed);

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chimeAdapter);

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
