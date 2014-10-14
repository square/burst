package org.robolectric.shadows;

import android.view.animation.LayoutAnimationController;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

@Implements(LayoutAnimationController.class)
public class ShadowLayoutAnimationController {
  @RealObject
  private LayoutAnimationController realAnimation;

  private int loadedFromResourceId = -1;

  public void setLoadedFromResourceId(int loadedFromResourceId) {
    this.loadedFromResourceId = loadedFromResourceId;
  }

  public int getLoadedFromResourceId() {
    if (loadedFromResourceId == -1) {
      throw new IllegalStateException("not loaded from a resource");
    }
    return loadedFromResourceId;
  }
}
