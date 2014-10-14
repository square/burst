package org.robolectric.shadows;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

@Implements(TouchDelegate.class)
public class ShadowTouchDelegate {

  @RealObject
  TouchDelegate realObject;

  private Rect bounds;
  private View delegateView;

  public void __constructor__( Rect bounds, View delegateView ){
    this.bounds = bounds;
    this.delegateView = delegateView;
  }

  public Rect getBounds() {
    return this.bounds;
  }

  public View getDelegateView() {
    return this.delegateView;
  }

}
