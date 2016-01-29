package com.tevinjeffrey.rmp.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.tevinjeffrey.rmp.client.ClientService;
import com.tevinjeffrey.rmp.client.RMPClient;
import com.tevinjeffrey.rmp.client.module.ClientModule;
import com.tevinjeffrey.rmp.scraper.RMPScraper;
import com.tevinjeffrey.rmp.scraper.module.ScraperModule;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.junit.Assert.*;

@Module(
        injects = {
                RMPTest.class
        },
        includes = {ClientModule.class, ScraperModule.class},
        complete = false,
        library = true )

public class RMPTestModule {

    @Singleton
    @Provides
    public RMP providesRMP(RMPClient rmpClient, RMPScraper rmpScraper) {
        return new RMP(rmpClient, rmpScraper);
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

}