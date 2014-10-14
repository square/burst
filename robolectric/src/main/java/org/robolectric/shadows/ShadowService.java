package org.robolectric.shadows;

import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.ServiceConnection;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import static org.robolectric.Robolectric.shadowOf;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(Service.class)
public class ShadowService extends ShadowContextWrapper {
  @RealObject Service realService;

  private Notification lastForegroundNotification;
  private boolean selfStopped = false;
  private boolean unbindServiceShouldThrowIllegalArgument = false;
  private boolean foregroundStopped;
  private boolean notificationShouldRemoved;

  @Implementation
  public final Application getApplication() {
    return Robolectric.application;
  }

  @Implementation @Override
  public Context getApplicationContext() {
    return Robolectric.application;
  }

  @Implementation
  public void onDestroy() {
    assertNoBroadcastListenersRegistered();
  }

  @Override @Implementation
  public void unbindService(ServiceConnection conn) {
    if (unbindServiceShouldThrowIllegalArgument) {
      throw new IllegalArgumentException();
    }
  }

  @Implementation
  public void stopSelf() {
    selfStopped = true;
  }

  @Implementation
  public void stopSelf(int id) {
    selfStopped = true;
  }

  public void setUnbindServiceShouldThrowIllegalArgument(boolean flag) {
    unbindServiceShouldThrowIllegalArgument = flag;
  }

  @Implementation
  public final void startForeground(int id, Notification notification) {
    lastForegroundNotification = notification;
  }

  @Implementation
  public void stopForeground(boolean removeNotification) {
    foregroundStopped = true;
    notificationShouldRemoved = removeNotification;
    if (removeNotification) {
      lastForegroundNotification = null;
    }
  }

  public Notification getLastForegroundNotification() {
    return lastForegroundNotification;
  }

  /**
   * Utility method that throws a {@code RuntimeException} if any {@code BroadcastListener}s are still registered.
   */
  public void assertNoBroadcastListenersRegistered() {
    ((ShadowApplication) shadowOf(getApplicationContext())).assertNoBroadcastListenersRegistered(realService, "Service");
  }

  /**
   * Non-Android accessor, to use in assertions.
   * @return
   */
  public boolean isStoppedBySelf() {
    return selfStopped;
  }

  public boolean isForegroundStopped() {
    return foregroundStopped;
  }

  public boolean getNotificationShouldRemoved() {
    return notificationShouldRemoved;
  }
}