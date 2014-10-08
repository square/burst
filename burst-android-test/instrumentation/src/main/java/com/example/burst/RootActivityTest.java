package com.example.burst;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import java.util.ArrayList;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

/**
 * Launches {@link RootActivity} and interacts with some views therein.
 * <p>
 * Extends {@link ActivityInstrumentationTestCase2} for convenience - it takes care of launching
 * and finishing the activity for us. Extending the more basic
 * {@link android.test.InstrumentationTestCase} would also work.
 */
public class RootActivityTest extends ActivityInstrumentationTestCase2<RootActivity> {
  private final Drink drink;

  public RootActivityTest() {
    // Required for JUnit 3.
    this(null);
  }

  public RootActivityTest(Drink drink) {
    super(RootActivity.class);
    this.drink = drink;
  }

  public void testDrinkIsDisplayed() {
    View decorView = getActivity().getWindow().getDecorView();
    ArrayList<View> viewsWithMatchingDrinkName = new ArrayList<>();
    decorView.findViewsWithText(viewsWithMatchingDrinkName, drink.name(), FIND_VIEWS_WITH_TEXT);
    assertEquals(1, viewsWithMatchingDrinkName.size());
  }
}
