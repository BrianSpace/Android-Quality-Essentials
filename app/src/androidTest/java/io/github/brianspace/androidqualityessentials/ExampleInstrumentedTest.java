package io.github.brianspace.androidqualityessentials;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * Sample test case.
     */
    @Test
    public void useAppContext() throws ComparisonFailure {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("Package name does not match.",
                "io.github.brianspace.androidqualityessentials", appContext.getPackageName());
    }
}
