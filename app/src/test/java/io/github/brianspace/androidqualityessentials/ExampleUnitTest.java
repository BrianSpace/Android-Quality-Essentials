package io.github.brianspace.androidqualityessentials;

import static org.junit.Assert.assertEquals;

import org.junit.ComparisonFailure;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void additionIsCorrect() throws ComparisonFailure {
        assertEquals("Not equal!", 4, 2 + 2);
    }
}