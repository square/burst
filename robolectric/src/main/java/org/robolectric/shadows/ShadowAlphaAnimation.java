package org.robolectric.shadows;

import android.view.animation.AlphaAnimation;
import org.robolectric.annotation.Implements;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(AlphaAnimation.class)
public class ShadowAlphaAnimation extends ShadowAnimation {
  private float fromAlpha;
  private float toAlpha;

  public void __constructor__(float fromAlpha, float toAlpha) {
    this.fromAlpha = fromAlpha;
    this.toAlpha = toAlpha;
  }

  public float getFromAlpha() {
    return fromAlpha;
  }

  public float getToAlpha() {
    return toAlpha;
  }
}
