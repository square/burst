package org.robolectric.shadows;

import android.animation.Animator;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.ArrayList;
import java.util.List;

import static android.animation.Animator.AnimatorListener;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(Animator.class)
public class ShadowAnimator {
  @RealObject
  private Animator realObject;
  protected long duration;
  private final List<AnimatorListener> listeners = new ArrayList<AnimatorListener>();

  protected void notifyStart() {
    for (AnimatorListener listener : listeners) {
      listener.onAnimationStart(realObject);
    }
  }

  protected void notifyEnd() {
    for (AnimatorListener listener : listeners) {
      listener.onAnimationEnd(realObject);
    }
  }

  @Implementation
  public void addListener(AnimatorListener listener) {
    listeners.add(listener);
  }

  @Implementation
  public void start () {
    notifyStart();
    notifyEnd();
  }

  // Tested via ObjectAnimatorTest for now
  @Implementation
  public long getDuration() {
    return duration;
  }
}
