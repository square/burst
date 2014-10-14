package android.view.animation;


import org.robolectric.internal.DoNotInstrument;

@DoNotInstrument
public class ShadowAnimationBridge {

  private Animation realAnimation;

  public ShadowAnimationBridge(Animation realAnimation) {
    this.realAnimation = realAnimation;

  }


  public void applyTransformation(float interpolatedTime, Transformation t) {
    realAnimation.applyTransformation(interpolatedTime, t);
  }
}
