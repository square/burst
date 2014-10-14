package com.squareup.burst;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.example.burst.RootActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static android.view.View.FIND_VIEWS_WITH_TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Launches {@link com.example.burst.RootActivity} and interacts with some views therein.
 */
@RunWith(BurstRobolectric.class)
@Config(manifest = "./burst-android-test/app/AndroidManifest.xml", emulateSdk = 18)
public class RoboActivityTest {
  private final RoboSoda soda;

  public RoboActivityTest(RoboSoda soda) {
    this.soda = soda;
  }

  @Test public void none() {
    assertTrue(true);
  }


  @Test
  public void testDrinkIsDisplayed() {
    Activity activity = Robolectric.buildActivity(RootActivity.class).create().get();
    View decorView = activity.getWindow().getDecorView();
    ArrayList<View> viewsWithMatchingDrinkName = new ArrayList<>();
    decorView.findViewsWithText(viewsWithMatchingDrinkName, soda.name(), FIND_VIEWS_WITH_TEXT);
    assertEquals(1, viewsWithMatchingDrinkName.size());
  }
}
