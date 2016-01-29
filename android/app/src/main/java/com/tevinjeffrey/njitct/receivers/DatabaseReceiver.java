package com.tevinjeffrey.njitct.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tevinjeffrey.njitct.NjitCTApp;
import com.tevinjeffrey.njitct.database.DatabaseHandler;
import com.tevinjeffrey.njitct.model.SectionModel;

import com.tevinjeffrey.njitct.ui.courseinfo.CourseInfoView;

import javax.inject.Inject;

public class DatabaseReceiver extends BroadcastReceiver {

    @Inject
    DatabaseHandler mDatabaseHandler;

    public DatabaseReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NjitCTApp.getObjectGraph(context).inject(this);

        SectionModel section = intent.getParcelableExtra(CourseInfoView.SELECTED_SECTION);
        if (section != null) {
            //Removes section from databse
            mDatabaseHandler.removeSectionFromDb(section);
            //Notify user with a toast.
            notifyUser(context, section);
            //Remove notification from notification panel
            removeNotification(context, section);
        }
    }

    private void removeNotification(Context context, SectionModel sectionModel) {
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(Integer.valueOf(sectionModel.getIndexNumber()));
    }

    private void notifyUser(Context context, SectionModel section) {
        Toast.makeText(context, String.format("Stopped tracking %s",
                section.getCourse().getName()), Toast.LENGTH_SHORT).show();
    }
}
