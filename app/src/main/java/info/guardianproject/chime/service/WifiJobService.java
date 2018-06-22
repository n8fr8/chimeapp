package info.guardianproject.chime.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WifiJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        new JobTask(this).execute(jobParameters);

      //  startService(new Intent(this, PublishService.class));


        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;

        public JobTask(JobService jobService) {
            this.jobService = jobService;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {

            getWifiInfo();

            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }

    }


    private void getWifiInfo() {
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifiManager.getConnectionInfo();

        String mac = info.getMacAddress();
        String ssid = info.getSSID();
        String bssid = info.getBSSID();
    }
}

