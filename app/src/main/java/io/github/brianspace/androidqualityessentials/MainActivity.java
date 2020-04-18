package io.github.brianspace.androidqualityessentials;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

/**
 * Main activity for the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Log tag.
     */
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
    }
}
