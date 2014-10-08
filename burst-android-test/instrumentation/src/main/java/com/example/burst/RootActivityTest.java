package com.example.burst;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import java.util.ArrayList;

import static android.view.View.FIND_VIEWS_WITH_TEXT;

public class RootActivityTest extends ActivityInstrumentationTestCase2<RootActivity> {
  private final Drink drink;

  public RootActivityTest() {
    // Required for Burst!
    this(null);
  }

  public RootActivityTest(Drink drink) {
    super(RootActivity.class);
    this.drink = drink;
  }

  @Override
  protected void setUp() {
    getActivity();
  }

  public void testDrinkIsDisplayed() {
    View decorView = getActivity().getWindow().getDecorView();
    ArrayList<View> viewsWithMatchingDrinkName = new ArrayList<>();
    decorView.findViewsWithText(viewsWithMatchingDrinkName, drink.name(), FIND_VIEWS_WITH_TEXT);
    assertEquals(1, viewsWithMatchingDrinkName.size());
  }
}
