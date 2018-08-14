package info.guardianproject.chime.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.util.Date;
import java.util.List;

import info.guardianproject.chime.model.Chime;
import info.guardianproject.chime.model.ChimeEvent;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WifiJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        new JobTask(this).execute(jobParameters);

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(getApplicationContext(),WifiReceiver.WifiActiveService.class));
            }
            else
            {
                startService(new Intent(getApplicationContext(),WifiReceiver.WifiActiveService.class));
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }

    }

    public static final int MY_BACKGROUND_JOB = 0;

    public static void initJob(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            JobInfo job = new JobInfo.Builder(
                    MY_BACKGROUND_JOB,
                    new ComponentName(context, WifiJobService.class))
                    .setPersisted(true)
                    .setPeriodic(MIN_INTERVAL)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setRequiresCharging(false)
                    .build();
            js.schedule(job);
        }
    }

    private final static int MIN_INTERVAL = 15 * 60000; //15 minutes

}

