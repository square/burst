package org.robolectric.shadows;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Implements(ResultReceiver.class)
public class ShadowResultReceiver {
  // TODO: Use handler to make asynchronous

  @RealObject private ResultReceiver realResultReceiver;

  @Implementation
  public void send(int resultCode, android.os.Bundle resultData) {
    try {
      Method onReceiveResult = ResultReceiver.class.getDeclaredMethod("onReceiveResult", Integer.TYPE, Bundle.class);
      onReceiveResult.setAccessible(true);
      onReceiveResult.invoke(realResultReceiver, resultCode, resultData);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
