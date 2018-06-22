package info.guardianproject.chime.db;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;
import java.util.Random;

import info.guardianproject.chime.geo.GeoManager;
import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.R;
import info.guardianproject.chime.util.DistanceFormatter;

public class ChimeAdapter extends RecyclerView.Adapter<ChimeAdapter.MyViewHolder> {

    private Context mContext;
    private List<Chime> chimeList;
    private int chimeLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, location, wifi_ssid, service_uri;
        public ImageView ivShare, ivMap, ivConnect;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            location = (TextView) view.findViewById(R.id.card_location);
            wifi_ssid = (TextView) view.findViewById(R.id.card_wifi_ssid);
            service_uri = (TextView) view.findViewById(R.id.card_service_uri);
            ivShare = view.findViewById(R.id.action_share);
            ivMap = view.findViewById(R.id.action_map);
            ivConnect = view.findViewById(R.id.action_connect);


        }
    }


    public ChimeAdapter(Context context, List<Chime> chimeList, int chimeLayout) {
        this.mContext = context;
        this.chimeList = chimeList;
        this.chimeLayout = chimeLayout;

        GeoManager.initLocation(context);
        getWifiInfo(context);
    }

    public void onDestroy() {
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(chimeLayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Chime chime = chimeList.get(position);
        holder.title.setText(chime.name);

        if (holder.location != null) {

            if (chime.latitude > 0 || chime.longitude > 0) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(chime.latitude + "," + chime.longitude);
                chime.isNearby = isNearby(chime);

                if (chime.isNearby) {
                    //show nearby location icon
                    chime.lastSeen = new Date();
                }

                float distanceInMeters = getDistanceInMeters(chime);
                if (distanceInMeters > 0) {
                    holder.location.setText(DistanceFormatter.format((int)distanceInMeters) + ' ' + mContext.getString(R.string.distance_away));
                }
                else
                {
                    holder.location.setText(mContext.getString(R.string.status_here));
                }

            } else {

                holder.location.setText("");
                holder.location.setVisibility(View.GONE);
                chime.isNearby = false;
            }
        }

        if (!TextUtils.isEmpty(chime.ssid)) {
            if (holder.wifi_ssid != null) {
                holder.wifi_ssid.setText(chime.ssid);

                holder.ivConnect.setVisibility(View.VISIBLE);

                if (lastWifiInfo != null && chime.ssid.equals(lastWifiInfo.getSSID())) {
                    //show wifi connected icon
                    chime.isNearby = true;
                    chime.lastSeen = new Date();

                    holder.ivConnect.setColorFilter(R.color.colorActive);

                }
                else
                {
                    holder.ivConnect.clearColorFilter();
                }

            }
        }
        else
        {
            if (holder.ivConnect != null)
                holder.ivConnect.setVisibility(View.GONE);

        }

        if (holder.service_uri != null)
            if (!TextUtils.isEmpty(chime.serviceUri))
                holder.service_uri.setText(chime.serviceUri);

        chime.save();

        if (chimeLayout == R.layout.layout_card_mixed)
        {
            // Set a random height for TextView
            holder.title.getLayoutParams().height = getRandomIntInRange(300, 100);

        }

        if (holder.ivShare != null)
        {
            holder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String ShareSub = mContext.getString(R.string.label_share_chime);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, chime.getUri());
                    mContext.startActivity(Intent.createChooser(sharingIntent, ShareSub));
                }
            });
        }

        if (holder.ivMap != null)
        {
            holder.ivMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = "geo:" + chime.latitude + ","
                            +chime.longitude + "?q=" + chime.latitude
                            + "," + chime.longitude;
                    mContext.startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }
            });
        }

        if (holder.ivConnect != null)
        {
            holder.ivConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
        }

        // loading album cover using Glide library
        //  Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        /**
         holder.overflow.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        showPopupMenu(holder.overflow);
        }
        });**/
    }

    private Random mRandom = new Random();

    // Custom method to get a random number between a range
    protected int getRandomIntInRange(int max, int min) {
        return mRandom.nextInt((max - min) + min) + min;
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        /**
         PopupMenu popup = new PopupMenu(mContext, view);
         MenuInflater inflater = popup.getMenuInflater();
         inflater.inflate(R.menu.menu_album, popup.getMenu());
         popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
         popup.show();
         **/
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            /**
             switch (menuItem.getItemId()) {
             case R.id.action_add_favourite:
             Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
             return true;
             case R.id.action_play_next:
             Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
             return true;
             default:
             }**/
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return chimeList.size();
    }


    private boolean isNearby (Chime chime)
    {
        Location lastLocation = GeoManager.getLastLocation();

        if (lastLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), chime.latitude, chime.longitude, results);
            float distanceInMeters = results[0];
            boolean isNearby = distanceInMeters < 10;
            return isNearby;
        }

        return false;
    }

    private float getDistanceInMeters (Chime chime)
    {
        Location lastLocation = GeoManager.getLastLocation();

        if (lastLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), chime.latitude, chime.longitude, results);
            float distanceInMeters = results[0];
            return distanceInMeters;
        }

        return -1;
    }

    static WifiInfo lastWifiInfo;

    private static void getWifiInfo(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
            lastWifiInfo = wifiManager.getConnectionInfo();

    }
}