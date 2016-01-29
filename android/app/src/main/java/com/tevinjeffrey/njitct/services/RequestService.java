package com.tevinjeffrey.njitct.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.tevinjeffrey.njitct.R;
import com.tevinjeffrey.njitct.NjitCTApp;
import com.tevinjeffrey.njitct.database.DatabaseHandler;
import com.tevinjeffrey.njitct.model.SectionModel;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.receivers.DatabaseReceiver;
import com.tevinjeffrey.njitct.ui.courseinfo.CourseInfoView;
import com.tevinjeffrey.njitct.ui.trackedsections.TrackedSectionsView;
import com.tevinjeffrey.njitct.utils.PreferenceUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class RequestService extends Service {

    private static final String SECTION_NOTIFICATION_GROUP = "SECTION_NOTIFICATION_GROUP";

    @Inject
    TrackingApiModel mApi;
    @Inject
    PreferenceUtils mPreferenceUtils;
    @Inject
    DatabaseHandler mDatabaseHandler;
    @Inject
    Context mContext;

    public RequestService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NjitCTApp.getObjectGraph(this).inject(this);

        Timber.i("Request Service started at %s", System.currentTimeMillis());

        mDatabaseHandler.getObservableSections()
                .flatMap(new Func1<List<SectionModel>, Observable<? extends SectionModel>>() {
                    @Override
                    public Observable<? extends SectionModel> call(List<SectionModel> requests) {
                        return mApi.getTrackedSections(requests);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SectionModel>() {
                    @Override
                    public void onCompleted() {
                        stopSelf();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Timber.e(t, "Crash while attempting to complete request");
                        stopSelf();
                    }
                    @Override
                    public void onNext(SectionModel section) {
                        if (section.isOpen())
                            makeNotification(section);
                    }
                });
        return START_NOT_STICKY;
    }

    //Creates a notfication of the Android system.
    private void makeNotification(SectionModel section) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("app_notification", true)) {
            String courseTitle = section.getCourse().getName();

            String sectionNumber = section.getSectionNumber();

            //Builds a notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Section " + sectionNumber + " of " + courseTitle
                                            + " has opened")
                                    .setBigContentTitle(section.getCourse().getTerm() + " - " + courseTitle))
                            .setSmallIcon(R.drawable.ic_notification)
                            .setWhen(System.currentTimeMillis())
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setColor(ContextCompat.getColor(mContext, R.color.green))
                            .setAutoCancel(true)
                            .setGroup(SECTION_NOTIFICATION_GROUP)
                            .setSound(getSound())
                            .setContentTitle("A section has opened!")
                            .setContentText("Section " + sectionNumber + " of " + courseTitle
                                    + " has opened");

            //Intent to start web browser
            Intent openInBrowser = new Intent(Intent.ACTION_VIEW);
            openInBrowser.setData(Uri.parse("http://my.njit.edu/"));
            PendingIntent pOpenInBrowser = PendingIntent.getActivity(RequestService.this, 0, openInBrowser, 0);
            mBuilder.addAction(R.drawable.ic_open_in_browser_white_24dp, "Webreg", pOpenInBrowser);

            //Intent open the app.
            Intent openTracked = new Intent(RequestService.this, DatabaseReceiver.class);
            openTracked.putExtra(TrackedSectionsView.REQUEST, section);
            openTracked.putExtra(CourseInfoView.SELECTED_SECTION, section);
            //The intent that will be when the user clicks stop tracking in the notification bar.
            PendingIntent pOpenTracked = PendingIntent.getBroadcast(RequestService.this,
                    Integer.valueOf(section.getIndexNumber()), openTracked, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_close_white_24dp, "Stop Tracking", pOpenTracked);

            //When you click on the notification itself.
            mBuilder.setContentIntent(pOpenInBrowser);

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //Builds the intent and sends it to notification manager with a unique ID.
            // The id is the index number of the section since those are also unique.
            // It also allows me to easily update the notication in the future.
            Notification n = mBuilder.build();
            mNotifyMgr.notify(Integer.valueOf(section.getIndexNumber()), n);
        }
    }

    private Uri getSound() {
        if (mPreferenceUtils.getCanPlaySound()) {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            return null;
        }
    }


    //Androd boilerplate code
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Timber.i("Request Service ended at %s", System.currentTimeMillis());
        super.onDestroy();
    }

    @Override
    public String toString() {
        return "RequestService";
    }
}
