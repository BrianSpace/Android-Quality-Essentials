package io.github.brianspace.androidqualityessentials;

import android.app.Application;
import android.os.StrictMode;
import com.squareup.leakcanary.LeakCanary;

/**
 * Application class.
 */
public class AndroidQualityEssentialsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()   // If violations are in Android itself or 3rd-party libs, use penaltyLog.
                    .build());

            // Avoid the process dedicated to LeakCanary for heap analysis.
            if (!LeakCanary.isInAnalyzerProcess(this)) {
                LeakCanary.install(this);
            }
        }
    }
}
