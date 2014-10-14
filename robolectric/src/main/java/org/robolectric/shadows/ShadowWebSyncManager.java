package org.robolectric.shadows;

import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(value = Robolectric.Anything.class, className = "android.webkit.WebSyncManager")
public class ShadowWebSyncManager {
  protected boolean synced = false;

  @Implementation
  public void sync() {
    synced = true;
  }

  public boolean synced() {
    return synced;
  }

  public void reset() {
    synced = false;
  }
}
