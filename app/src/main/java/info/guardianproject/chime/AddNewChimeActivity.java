package info.guardianproject.chime;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

import java.util.Date;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import info.guardianproject.chime.model.Chime;

public class AddNewChimeActivity extends AppCompatActivity implements VerticalStepperForm {

    private VerticalStepperFormLayout verticalStepperForm;

    EditText etLocation;
    EditText etName;
    EditText etNetwork;
    EditText etService;

    Location lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_chime);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] mySteps = {getString(R.string.form_label_name),
                getString(R.string.form_label_location),getString(R.string.form_label_network),getString(R.string.form_label_service)};

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();

        initLocation();
        getWifiInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createLocationStep();
                break;
            case 2:
                view = createNetworkStep();
                break;
            case 3:
                view = createServiceStep();
                break;
        }
        return view;
    }

    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        etName = new EditText(this);
        etName.setSingleLine(true);
        etName.setHint(R.string.form_hint_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkName();
            }
        });
        return etName;
    }


    private View createLocationStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        etLocation = new EditText(this);
        etLocation.setSingleLine(true);
        etLocation.setHint(R.string.form_hint_location);

        return etLocation;
    }

    private View createNetworkStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        etNetwork = new EditText(this);
        etNetwork.setSingleLine(true);
        etNetwork.setHint(R.string.form_hint_network);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkNetwork();
            }
        });
        return etNetwork;
    }

    private View createServiceStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layout
        etService = new EditText(this);
        etService.setSingleLine(true);
        etService.setHint(R.string.form_hint_service);
        etService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkService();
            }
        });
        return etService;
    }

    /**
    private View createEmailStep() {
        // In this case we generate the view by inflating a XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout emailLayoutContent = (LinearLayout) inflater.inflate(R.layout.email_step_layout, null, false);
        email = (EditText) emailLayoutContent.findViewById(R.id.email);

        return emailLayoutContent;
    }

    private View createPhoneNumberStep() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout phoneLayoutContent = (LinearLayout) inflater.inflate(R.layout.phone_step_layout, null, false);
        return phoneLayoutContent;
    }**/

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkName();
                break;
            case 1:
                checkLocation();
                break;
            case 2:
                checkNetwork();
                break;
            case 3:
                checkService();
                break;
        }
    }

    @Override
    public void sendData() {

        Chime chime = new Chime();
        chime.name = etName.getText().toString();

        if (etLocation.length() > 0) {
            String[] locSplit = etLocation.getText().toString().split(",");
            chime.latitude = Double.parseDouble(locSplit[0]);
            chime.longitude = Double.parseDouble(locSplit[1]);
        }

        if (etNetwork.length() > 0) {
            chime.ssid = etNetwork.getText().toString();

            if (lastWifiInfo != null)
             if (chime.ssid.equals(lastWifiInfo.getSSID()))
             {
                 chime.bssid = lastWifiInfo.getBSSID();
             }
        }

        if (etService.length() > 0) {
            chime.serviceUri = etService.getText().toString();

            if (chime.serviceUri.contains("fdroid")) {
                chime.serviceType = "fdroid";
                chime.servicePackage = "org.fdroid.fdroid";
            }
            else if (chime.serviceUri.contains("librarybox")) {
                chime.serviceType = "www";
            }
            else if (chime.serviceUri.contains("piratebox")) {
                chime.serviceType = "www";
            }
            else if (chime.serviceUri.contains("upload")) {
                chime.serviceType = "report";
                chime.servicePackage = "net.opendasharchive.openarchive";

            }

        }

        chime.lastSeen = new Date();
        chime.isNearby = true;
        chime.save();

        finish();
    }

    private void checkName() {
        if(etName.length() >= 3 && etName.length() <= 40) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = getString(R.string.warn_name_length);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkNetwork() {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void checkService() {
        verticalStepperForm.setActiveStepAsCompleted();
    }

    private void checkLocation() {
        if(etLocation.length() > 0) {

            try {
                String[] latlong = etLocation.getText().toString().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                Location loc = new Location("");
                loc.setLatitude(latitude);
                loc.setLongitude(longitude);

                verticalStepperForm.setActiveStepAsCompleted();

            }
            catch (Exception e) {
                // This error message is optional (use null if you don't want to display an error message)
                String errorMessage = getString(R.string.form_error_location);
                verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
            }

        } else {

            verticalStepperForm.setActiveStepAsCompleted();
        }

    }

    LostApiClient lostApiClient;

    public void initLocation() {
        lostApiClient = new LostApiClient.Builder(this).addConnectionCallbacks(new LostApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected() {
                getCurrentLocation();
            }

            @Override
            public void onConnectionSuspended() {

            }
        }).build();
        lostApiClient.connect();

    }

    private void getCurrentLocation() {

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(5000)
                .setSmallestDisplacement(10);

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Do stuff

                lastLocation = location;
                String locString = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                etLocation.setText(locString);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(lostApiClient, request, listener);
    }

    WifiInfo lastWifiInfo;

    private void getWifiInfo() {
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            lastWifiInfo = wifiManager.getConnectionInfo();

            if (lastWifiInfo != null) {
                String mac = lastWifiInfo.getMacAddress();
                String ssid = lastWifiInfo.getSSID();
                String bssid = lastWifiInfo.getBSSID();

                etNetwork.setText(ssid);
            }
        }
    }


}
