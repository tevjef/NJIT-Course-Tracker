package com.tevinjeffrey.njitct;

import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.tevinjeffrey.njitct.database.DatabaseHandler;
import com.tevinjeffrey.njitct.database.DatabaseHandlerImpl;
import com.tevinjeffrey.njitct.model.TrackingApiModel;
import com.tevinjeffrey.njitct.njitapi.RetroNjit;
import com.tevinjeffrey.njitct.njitapi.RetroNjitService;
import com.tevinjeffrey.njitct.receivers.BootReceiver;
import com.tevinjeffrey.njitct.receivers.DatabaseReceiver;
import com.tevinjeffrey.njitct.services.Alarm;
import com.tevinjeffrey.njitct.services.RequestService;
import com.tevinjeffrey.njitct.ui.MainActivity;
import com.tevinjeffrey.njitct.ui.chooser.ChooserPresenterImpl;
import com.tevinjeffrey.njitct.ui.course.CoursePresenterImpl;
import com.tevinjeffrey.njitct.ui.sectioninfo.SectionInfoPresenterImpl;
import com.tevinjeffrey.njitct.ui.settings.SettingsActivity.SettingsFragment;
import com.tevinjeffrey.njitct.ui.subject.SubjectPresenterImpl;
import com.tevinjeffrey.njitct.ui.trackedsections.TrackedSectionsPresenterImpl;
import com.tevinjeffrey.njitct.utils.AndroidMainThread;
import com.tevinjeffrey.njitct.utils.BackgroundThread;
import com.tevinjeffrey.njitct.utils.PreferenceUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module(
        injects = {
        Alarm.class,
        RequestService.class,
        SettingsFragment.class,
        MainActivity.class,
        BootReceiver.class,
        DatabaseReceiver.class,
        SectionInfoPresenterImpl.class,
        TrackedSectionsPresenterImpl.class,
        ChooserPresenterImpl.class,
        SubjectPresenterImpl.class,
        CoursePresenterImpl.class,
        }, library = true)

public class NjitCTModule {

    private static final long CONNECT_TIMEOUT_MILLIS = 15000;
    private static final long READ_TIMEOUT_MILLIS = 20000;

    private final Context applicationContext;

    public NjitCTModule(Context context) {
        this.applicationContext = context;
    }

    @Provides
    @Singleton
    @AndroidMainThread
    public Scheduler provideAndroidMainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @BackgroundThread
    public Scheduler provideBackgroundThread() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return applicationContext;
    }

    @Provides
    @Singleton
    public DatabaseHandler providesDatabaseHandler(Bus bus) {
        return new DatabaseHandlerImpl(bus);
    }

    @Provides
    @Singleton
    public Bus providesEventBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    public TrackingApiModel providesTrackingApi() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return new RetroNjit(new RestAdapter.Builder()
                .setEndpoint("http://107.170.110.237/")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build().create(RetroNjitService.class));
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    @Provides
    @Singleton
    public PreferenceUtils providesPreferenceUtils(Context context) {
        return new PreferenceUtils(context);
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        client.networkInterceptors().add(new StethoInterceptor());

        File httpCacheDir = new File(context.getCacheDir(), context.getString(R.string.application_name));
        long httpCacheSize = 50 * 1024 * 1024; // 50 MiB
        Cache cache = new Cache(httpCacheDir, httpCacheSize);
        client.setCache(cache);
        if (BuildConfig.DEBUG) {
            try {
                cache.evictAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
