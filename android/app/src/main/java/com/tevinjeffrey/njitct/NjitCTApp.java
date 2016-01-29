package com.tevinjeffrey.njitct;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.orm.SugarApp;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import jonathanfinerty.once.Once;
import timber.log.Timber;

public class NjitCTApp extends SugarApp {
    private static final String INSTALLATION = "INSTALLATION";
    private static String sID = null;
    private ObjectGraph objectGraph;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        NjitCTApp njitCTApp = (NjitCTApp) context.getApplicationContext();
        return njitCTApp.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //refWatcher = LeakCanary.install(this);

        Hawk.init(this)
                .setLogLevel(LogLevel.FULL)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSqliteStorage(this))
                .build();

        Once.initialise(this);

        objectGraph = ObjectGraph.create(new NjitCTModule(getApplicationContext()));

        //Initalize crash reporting apis
        Fabric.with(this, new Crashlytics());
        if (BuildConfig.DEBUG) {
            //When debugging logs will go through the Android logger
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        } else {

            //Mint.enableDebug();
            //Mint.initAndStartSession(this, "2974ff7f");
            //Gets a unique id for for every installation
            String s = getsID(getApplicationContext());

            //Set unique user id
            //Mint.setUserIdentifier(s);
            Crashlytics.setUserIdentifier(s);
            //Diverts logs through crash reporting APIs
            Timber.plant(new CrashReportingTree());
        }
    }

    public static ObjectGraph getObjectGraph(Context context) {
        return ((NjitCTApp)context.getApplicationContext()).objectGraph;
    }

    private synchronized static String getsID(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    // A tree which logs important information for crash reporting.
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t);
                }
            }
            Crashlytics.log(priority, tag, message);
        }
    }
}
