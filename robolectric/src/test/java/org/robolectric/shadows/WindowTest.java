package org.robolectric.shadows;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(TestRunners.WithDefaults.class)
public class WindowTest {
  @Test
  public void testGetFlag() throws Exception {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    Window window = activity.getWindow();

    assertThat(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_FULLSCREEN)).isFalse();
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    assertThat(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_FULLSCREEN)).isTrue();
    window.setFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON, WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    assertThat(shadowOf(window).getFlag(WindowManager.LayoutParams.FLAG_FULLSCREEN)).isTrue();
  }

  @Test
  public void testGetTitle() throws Exception {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    Window window = activity.getWindow();
    window.setTitle("My Window Title");
    assertThat(shadowOf(window).getTitle()).isEqualTo("My Window Title");
  }

  @Test
  public void getHomeIcon_getsTheIconThatWasSetWithTheActionBar() throws Exception {
    TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
    Window window = activity.getWindow();
    ShadowWindow shadowWindow = shadowOf(window);

    ImageView homeIcon = shadowWindow.getHomeIcon();

    assertThat(homeIcon.getDrawable()).isNotNull();
    int createdFromResId = shadowOf(homeIcon.getDrawable()).getCreatedFromResId();
    assertThat(createdFromResId).isEqualTo(R.drawable.ic_lock_power_off);
  }

  @Test
  public void getBackgroundDrawable_returnsSetDrawable() throws Exception {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    Window window = activity.getWindow();
    ShadowWindow shadowWindow = shadowOf(window);

    assertThat(shadowWindow.getBackgroundDrawable()).isNull();

    window.setBackgroundDrawableResource(R.drawable.btn_star);
    assertThat(shadowOf(shadowWindow.getBackgroundDrawable()).createdFromResId).isEqualTo(R.drawable.btn_star);
  }

  @Test
  public void getSoftInputMode_returnsSoftInputMode() throws Exception {
    TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
    Window window = activity.getWindow();
    ShadowWindow shadowWindow = shadowOf(window);

    window.setSoftInputMode(7);

    assertThat(shadowWindow.getSoftInputMode()).isEqualTo(7);
  }

  @Test
  public void getProgressBar_returnsTheProgressBar() {
    Activity activity = Robolectric.buildActivity(TestActivity.class).create().get();

    ProgressBar progress = shadowOf(activity.getWindow()).getProgressBar();

    assertThat(progress.getVisibility()).isEqualTo(View.INVISIBLE);
    activity.setProgressBarVisibility(true);
    assertThat(progress.getVisibility()).isEqualTo(View.VISIBLE);
    activity.setProgressBarVisibility(false);
    assertThat(progress.getVisibility()).isEqualTo(View.GONE);
  }

  @Test
  public void getIndeterminateProgressBar_returnsTheIndeterminateProgressBar() {
    TestActivity.requestFeature = Window.FEATURE_INDETERMINATE_PROGRESS;
    Activity activity = Robolectric.buildActivity(TestActivity.class).create().get();

    ProgressBar indeterminate = shadowOf(activity.getWindow()).getIndeterminateProgressBar();

    assertThat(indeterminate.getVisibility()).isEqualTo(View.INVISIBLE);
    activity.setProgressBarIndeterminateVisibility(true);
    assertThat(indeterminate.getVisibility()).isEqualTo(View.VISIBLE);
    activity.setProgressBarIndeterminateVisibility(false);
    assertThat(indeterminate.getVisibility()).isEqualTo(View.GONE);
  }

  public static class TestActivity extends Activity {
    public static int requestFeature = Window.FEATURE_PROGRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTheme(R.style.Theme_Holo_Light);
      getWindow().requestFeature(requestFeature);
      setContentView(new LinearLayout(this));
      getActionBar().setIcon(R.drawable.ic_lock_power_off);
    }
  }
}
