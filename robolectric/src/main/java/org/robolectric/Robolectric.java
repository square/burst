package org.robolectric;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.CornerPathEffect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.widget.AbsListView;
import android.widget.AbsSeekBar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ResourceCursorAdapter;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewAnimator;
import android.widget.ZoomButtonsController;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.fest.reflect.method.Invoker;
import org.robolectric.bytecode.RobolectricInternals;
import org.robolectric.bytecode.ShadowWrangler;
import org.robolectric.res.ResourceLoader;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.HttpResponseGenerator;
import org.robolectric.shadows.ShadowAbsListView;
import org.robolectric.shadows.ShadowAbsSeekBar;
import org.robolectric.shadows.ShadowAccountManager;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowActivityGroup;
import org.robolectric.shadows.ShadowActivityManager;
import org.robolectric.shadows.ShadowAdapterView;
import org.robolectric.shadows.ShadowAddress;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowAlphaAnimation;
import org.robolectric.shadows.ShadowAnimation;
import org.robolectric.shadows.ShadowAnimationUtils;
import org.robolectric.shadows.ShadowAnimator;
import org.robolectric.shadows.ShadowAppWidgetHost;
import org.robolectric.shadows.ShadowAppWidgetHostView;
import org.robolectric.shadows.ShadowAppWidgetManager;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowArrayAdapter;
import org.robolectric.shadows.ShadowAssetManager;
import org.robolectric.shadows.ShadowAsyncTask;
import org.robolectric.shadows.ShadowAudioManager;
import org.robolectric.shadows.ShadowBaseAdapter;
import org.robolectric.shadows.ShadowBinder;
import org.robolectric.shadows.ShadowBitmap;
import org.robolectric.shadows.ShadowBitmapDrawable;
import org.robolectric.shadows.ShadowBitmapFactory;
import org.robolectric.shadows.ShadowBluetoothAdapter;
import org.robolectric.shadows.ShadowBluetoothDevice;
import org.robolectric.shadows.ShadowBundle;
import org.robolectric.shadows.ShadowCamera;
import org.robolectric.shadows.ShadowCanvas;
import org.robolectric.shadows.ShadowChoreographer;
import org.robolectric.shadows.ShadowClipboardManager;
import org.robolectric.shadows.ShadowColor;
import org.robolectric.shadows.ShadowColorMatrix;
import org.robolectric.shadows.ShadowConfiguration;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowContentObserver;
import org.robolectric.shadows.ShadowContentProviderClient;
import org.robolectric.shadows.ShadowContentProviderOperation;
import org.robolectric.shadows.ShadowContentProviderResult;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowContext;
import org.robolectric.shadows.ShadowContextWrapper;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowCookieSyncManager;
import org.robolectric.shadows.ShadowCornerPathEffect;
import org.robolectric.shadows.ShadowCountDownTimer;
import org.robolectric.shadows.ShadowCursorAdapter;
import org.robolectric.shadows.ShadowCursorLoader;
import org.robolectric.shadows.ShadowCursorWindow;
import org.robolectric.shadows.ShadowCursorWrapper;
import org.robolectric.shadows.ShadowDateFormat;
import org.robolectric.shadows.ShadowDatePickerDialog;
import org.robolectric.shadows.ShadowDefaultRequestDirector;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowDialogPreference;
import org.robolectric.shadows.ShadowDisplay;
import org.robolectric.shadows.ShadowDownloadManager;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.shadows.ShadowDrawerLayout;
import org.robolectric.shadows.ShadowExpandableListView;
import org.robolectric.shadows.ShadowFilter;
import org.robolectric.shadows.ShadowFrameLayout;
import org.robolectric.shadows.ShadowGeocoder;
import org.robolectric.shadows.ShadowGestureDetector;
import org.robolectric.shadows.ShadowGradientDrawable;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowHandlerThread;
import org.robolectric.shadows.ShadowHttpResponseCache;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.shadows.ShadowInputDevice;
import org.robolectric.shadows.ShadowInputMethodManager;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowIntentService;
import org.robolectric.shadows.ShadowJsPromptResult;
import org.robolectric.shadows.ShadowJsResult;
import org.robolectric.shadows.ShadowKeyEvent;
import org.robolectric.shadows.ShadowKeyguardManager;
import org.robolectric.shadows.ShadowLayoutAnimationController;
import org.robolectric.shadows.ShadowLinearGradient;
import org.robolectric.shadows.ShadowLinearLayout;
import org.robolectric.shadows.ShadowListPreference;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowMatrix;
import org.robolectric.shadows.ShadowMediaMetadataRetriever;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.ShadowMediaRecorder;
import org.robolectric.shadows.ShadowMediaStore;
import org.robolectric.shadows.ShadowMenuInflater;
import org.robolectric.shadows.ShadowMimeTypeMap;
import org.robolectric.shadows.ShadowMotionEvent;
import org.robolectric.shadows.ShadowNetworkInfo;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowNumberPicker;
import org.robolectric.shadows.ShadowObjectAnimator;
import org.robolectric.shadows.ShadowPaint;
import org.robolectric.shadows.ShadowParcel;
import org.robolectric.shadows.ShadowPath;
import org.robolectric.shadows.ShadowPendingIntent;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowPopupWindow;
import org.robolectric.shadows.ShadowPowerManager;
import org.robolectric.shadows.ShadowPreference;
import org.robolectric.shadows.ShadowPreferenceActivity;
import org.robolectric.shadows.ShadowPreferenceCategory;
import org.robolectric.shadows.ShadowPreferenceGroup;
import org.robolectric.shadows.ShadowPreferenceScreen;
import org.robolectric.shadows.ShadowProgressBar;
import org.robolectric.shadows.ShadowProgressDialog;
import org.robolectric.shadows.ShadowRemoteViews;
import org.robolectric.shadows.ShadowResolveInfo;
import org.robolectric.shadows.ShadowResourceCursorAdapter;
import org.robolectric.shadows.ShadowResources;
import org.robolectric.shadows.ShadowResultReceiver;
import org.robolectric.shadows.ShadowSQLiteConnection;
import org.robolectric.shadows.ShadowScaleGestureDetector;
import org.robolectric.shadows.ShadowScanResult;
import org.robolectric.shadows.ShadowScrollView;
import org.robolectric.shadows.ShadowScroller;
import org.robolectric.shadows.ShadowSeekBar;
import org.robolectric.shadows.ShadowSensorManager;
import org.robolectric.shadows.ShadowService;
import org.robolectric.shadows.ShadowSimpleCursorAdapter;
import org.robolectric.shadows.ShadowSmsManager;
import org.robolectric.shadows.ShadowSslErrorHandler;
import org.robolectric.shadows.ShadowStatFs;
import org.robolectric.shadows.ShadowStateListDrawable;
import org.robolectric.shadows.ShadowSurface;
import org.robolectric.shadows.ShadowTabHost;
import org.robolectric.shadows.ShadowTelephonyManager;
import org.robolectric.shadows.ShadowTextPaint;
import org.robolectric.shadows.ShadowTextToSpeech;
import org.robolectric.shadows.ShadowTextView;
import org.robolectric.shadows.ShadowTimePickerDialog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowTouchDelegate;
import org.robolectric.shadows.ShadowTranslateAnimation;
import org.robolectric.shadows.ShadowTypedArray;
import org.robolectric.shadows.ShadowTypeface;
import org.robolectric.shadows.ShadowVideoView;
import org.robolectric.shadows.ShadowView;
import org.robolectric.shadows.ShadowViewAnimator;
import org.robolectric.shadows.ShadowViewConfiguration;
import org.robolectric.shadows.ShadowViewGroup;
import org.robolectric.shadows.ShadowViewTreeObserver;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.shadows.ShadowWebViewDatabase;
import org.robolectric.shadows.ShadowWifiConfiguration;
import org.robolectric.shadows.ShadowWifiInfo;
import org.robolectric.shadows.ShadowWifiManager;
import org.robolectric.shadows.ShadowWindow;
import org.robolectric.shadows.ShadowWindowManager;
import org.robolectric.shadows.ShadowZoomButtonsController;
import org.robolectric.shadows.ShadowProcess;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.HttpRequestInfo;
import org.robolectric.tester.org.apache.http.RequestMatcher;
import org.robolectric.util.ActivityController;
import org.robolectric.util.Scheduler;
import org.robolectric.util.ServiceController;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.fest.reflect.core.Reflection.method;

public class Robolectric {
  public static final int DEFAULT_SDK_VERSION = 18;

  public static Application application;
  public static RobolectricPackageManager packageManager;
  public static Object activityThread;

  public static <T> T newInstanceOf(Class<T> clazz) {
    return RobolectricInternals.newInstanceOf(clazz);
  }

  public static Object newInstanceOf(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      if (clazz != null) {
        return newInstanceOf(clazz);
      }
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  public static <T> T newInstance(Class<T> clazz, Class[] parameterTypes, Object[] params) {
    return RobolectricInternals.newInstance(clazz, parameterTypes, params);
  }

  // todo: make private
  public static ShadowWrangler getShadowWrangler() {
    return ((ShadowWrangler) RobolectricInternals.getClassHandler());
  }

  public static List<Class<?>> getDefaultShadowClasses() {
    return RobolectricBase.DEFAULT_SHADOW_CLASSES;
  }

  public static <T> T directlyOn(T shadowedObject, Class<T> clazz) {
    return RobolectricInternals.directlyOn(shadowedObject, clazz);
  }

  public static <T> Invoker directlyOn(T shadowedObject, Class<T> clazz, String methodName, Class<?>... paramTypes) {
    String directMethodName = RobolectricInternals.directMethodName(clazz.getName(), methodName);
    return method(directMethodName).withReturnType(Object.class).withParameterTypes(paramTypes).in(
        shadowedObject);
  }

  public static <T> Invoker directlyOn(Class<T> clazz, String methodName, Class<?>... paramTypes) {
    String directMethodName = RobolectricInternals.directMethodName(clazz.getName(), methodName);
    return method(directMethodName).withReturnType(Object.class).withParameterTypes(paramTypes).in(clazz);
  }

  public static <T> Invoker directlyOn(Object shadowedObject, String clazzName, String methodName, Class<?>... paramTypes) {
    try {
      Class<Object> aClass = (Class<Object>) shadowedObject.getClass().getClassLoader().loadClass(clazzName);
      return directlyOn(shadowedObject, aClass, methodName, paramTypes);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static ShadowAbsListView shadowOf(AbsListView instance) {
    return (ShadowAbsListView) shadowOf_(instance);
  }

  public static ShadowAbsSeekBar shadowOf(AbsSeekBar instance) {
    return (ShadowAbsSeekBar) shadowOf_(instance);
  }

  public static ShadowAccountManager shadowOf(AccountManager instance) {
    return (ShadowAccountManager) shadowOf_(instance);
  }

  public static ShadowActivity shadowOf(Activity instance) {
    return (ShadowActivity) shadowOf_(instance);
  }

  public static ShadowActivityGroup shadowOf(ActivityGroup instance) {
    return (ShadowActivityGroup) shadowOf_(instance);
  }

  public static ShadowActivityManager shadowOf(ActivityManager instance) {
    return (ShadowActivityManager) shadowOf_(instance);
  }

  public static ShadowAdapterView shadowOf(AdapterView instance) {
    return (ShadowAdapterView) shadowOf_(instance);
  }

  public static ShadowAddress shadowOf(Address instance) {
    return (ShadowAddress) shadowOf_(instance);
  }

  public static ShadowAlarmManager shadowOf(AlarmManager instance) {
    return (ShadowAlarmManager) Robolectric.shadowOf_(instance);
  }

  public static ShadowAlertDialog shadowOf(AlertDialog instance) {
    return (ShadowAlertDialog) shadowOf_(instance);
  }

  public static ShadowAlphaAnimation shadowOf(AlphaAnimation instance) {
    return (ShadowAlphaAnimation) shadowOf_(instance);
  }

  public static ShadowAnimation shadowOf(Animation instance) {
    return (ShadowAnimation) shadowOf_(instance);
  }

  public static ShadowLayoutAnimationController shadowOf(LayoutAnimationController instance) {
    return (ShadowLayoutAnimationController) shadowOf_(instance);
  }

  public static ShadowAnimationUtils shadowOf(AnimationUtils instance) {
    return (ShadowAnimationUtils) shadowOf_(instance);
  }

  public static ShadowAnimator shadowOf(Animator instance) {
    return (ShadowAnimator) shadowOf_(instance);
  }

  public static ShadowApplication shadowOf(Application instance) {
    return (ShadowApplication) shadowOf_(instance);
  }

  public static ShadowAppWidgetHost shadowOf(AppWidgetHost instance) {
    return (ShadowAppWidgetHost) shadowOf_(instance);
  }

  public static ShadowAppWidgetHostView shadowOf(AppWidgetHostView instance) {
    return (ShadowAppWidgetHostView) shadowOf_(instance);
  }

  public static ShadowAppWidgetManager shadowOf(AppWidgetManager instance) {
    return (ShadowAppWidgetManager) shadowOf_(instance);
  }

  public static ShadowArrayAdapter shadowOf(ArrayAdapter instance) {
    return (ShadowArrayAdapter) shadowOf_(instance);
  }

  public static ShadowAssetManager shadowOf(AssetManager instance) {
    return (ShadowAssetManager) Robolectric.shadowOf_(instance);
  }

  @SuppressWarnings("rawtypes")
  public static ShadowAsyncTask shadowOf(AsyncTask instance) {
    return (ShadowAsyncTask) Robolectric.shadowOf_(instance);
  }

  public static ShadowAudioManager shadowOf(AudioManager instance) {
    return (ShadowAudioManager) shadowOf_(instance);
  }

  public static ShadowBaseAdapter shadowOf(BaseAdapter other) {
    return (ShadowBaseAdapter) Robolectric.shadowOf_(other);
  }

  public static ShadowBitmap shadowOf(Bitmap other) {
    return (ShadowBitmap) Robolectric.shadowOf_(other);
  }

  public static ShadowBitmapDrawable shadowOf(BitmapDrawable instance) {
    return (ShadowBitmapDrawable) shadowOf_(instance);
  }

  public static ShadowBluetoothAdapter shadowOf(BluetoothAdapter other) {
    return (ShadowBluetoothAdapter) Robolectric.shadowOf_(other);
  }

  public static ShadowBluetoothDevice shadowOf(BluetoothDevice other) {
    return (ShadowBluetoothDevice) Robolectric.shadowOf_(other);
  }

  public static ShadowBundle shadowOf(Bundle instance) {
    return (ShadowBundle) shadowOf_(instance);
  }

  public static ShadowCamera shadowOf(Camera instance) {
    return (ShadowCamera) shadowOf_(instance);
  }

  public static ShadowCamera.ShadowParameters shadowOf(Camera.Parameters instance) {
    return (ShadowCamera.ShadowParameters) shadowOf_(instance);
  }

  public static ShadowCamera.ShadowSize shadowOf(Camera.Size instance) {
    return (ShadowCamera.ShadowSize) shadowOf_(instance);
  }

  public static ShadowCanvas shadowOf(Canvas instance) {
    return (ShadowCanvas) shadowOf_(instance);
  }

  public static ShadowClipboardManager shadowOf(ClipboardManager instance) {
    return (ShadowClipboardManager) shadowOf_(instance);
  }

  public static ShadowColor shadowOf(Color instance) {
    return (ShadowColor) shadowOf_(instance);
  }

  public static ShadowColorMatrix shadowOf(ColorMatrix instance) {
    return (ShadowColorMatrix) shadowOf_(instance);
  }

  public static ShadowConfiguration shadowOf(Configuration instance) {
    return (ShadowConfiguration) Robolectric.shadowOf_(instance);
  }

  public static ShadowConnectivityManager shadowOf(ConnectivityManager instance) {
    return (ShadowConnectivityManager) shadowOf_(instance);
  }

  public static ShadowCookieManager shadowOf(CookieManager instance) {
    return (ShadowCookieManager) shadowOf_(instance);
  }

  public static ShadowContentObserver shadowOf(ContentObserver instance) {
    return (ShadowContentObserver) shadowOf_(instance);
  }

  public static ShadowContentResolver shadowOf(ContentResolver instance) {
    return (ShadowContentResolver) shadowOf_(instance);
  }

  public static ShadowContentProviderClient shadowOf(ContentProviderClient client) {
    return (ShadowContentProviderClient) shadowOf_(client);
  }

  public static ShadowContentProviderOperation shadowOf(ContentProviderOperation instance) {
    return (ShadowContentProviderOperation) shadowOf_(instance);
  }

  public static ShadowContentProviderResult shadowOf(ContentProviderResult instance) {
    return (ShadowContentProviderResult) shadowOf_(instance);
  }

  public static ShadowCookieSyncManager shadowOf(CookieSyncManager instance) {
    return (ShadowCookieSyncManager) shadowOf_(instance);
  }

  public static ShadowContext shadowOf(Context instance) {
    return (ShadowContext) shadowOf_(instance);
  }

  public static ShadowContextWrapper shadowOf(ContextWrapper instance) {
    return (ShadowContextWrapper) shadowOf_(instance);
  }

  public static ShadowCornerPathEffect shadowOf(CornerPathEffect instance) {
    return (ShadowCornerPathEffect) Robolectric.shadowOf_(instance);
  }

  public static ShadowCountDownTimer shadowOf(CountDownTimer instance) {
    return (ShadowCountDownTimer) Robolectric.shadowOf_(instance);
  }

  public static ShadowCursorAdapter shadowOf(CursorAdapter instance) {
    return (ShadowCursorAdapter) shadowOf_(instance);
  }

  public static ShadowCursorLoader shadowOf(CursorLoader instance) {
    return (ShadowCursorLoader) shadowOf_(instance);
  }

  public static ShadowCursorWrapper shadowOf(CursorWrapper instance) {
    return (ShadowCursorWrapper) shadowOf_(instance);
  }

  public static ShadowDateFormat shadowOf(DateFormat instance) {
    return (ShadowDateFormat) shadowOf_(instance);
  }

  public static ShadowDatePickerDialog shadowOf(DatePickerDialog instance) {
    return (ShadowDatePickerDialog) shadowOf_(instance);
  }

  public static ShadowDefaultRequestDirector shadowOf(DefaultRequestDirector instance) {
    return (ShadowDefaultRequestDirector) shadowOf_(instance);
  }

  public static ShadowDialog shadowOf(Dialog instance) {
    return (ShadowDialog) shadowOf_(instance);
  }

  public static ShadowDialogPreference shadowOf(DialogPreference instance) {
    return (ShadowDialogPreference) shadowOf_(instance);
  }

  public static ShadowDrawable shadowOf(Drawable instance) {
    return (ShadowDrawable) shadowOf_(instance);
  }

  public static ShadowDisplay shadowOf(Display instance) {
    return (ShadowDisplay) shadowOf_(instance);
  }

  public static ShadowExpandableListView shadowOf(ExpandableListView instance) {
    return (ShadowExpandableListView) shadowOf_(instance);
  }

  public static ShadowLocation shadowOf(Location instance) {
    return (ShadowLocation) shadowOf_(instance);
  }

  public static ShadowFilter shadowOf(Filter instance) {
    return (ShadowFilter) shadowOf_(instance);
  }

  public static ShadowFrameLayout shadowOf(FrameLayout instance) {
    return (ShadowFrameLayout) shadowOf_(instance);
  }

  public static ShadowGeocoder shadowOf(Geocoder instance) {
    return (ShadowGeocoder) shadowOf_(instance);
  }

  public static ShadowGestureDetector shadowOf(GestureDetector instance) {
    return (ShadowGestureDetector) shadowOf_(instance);
  }

  public static ShadowGradientDrawable shadowOf(GradientDrawable instance) {
    return (ShadowGradientDrawable) shadowOf_(instance);
  }

  public static ShadowHandler shadowOf(Handler instance) {
    return (ShadowHandler) shadowOf_(instance);
  }

  public static ShadowHandlerThread shadowOf(HandlerThread instance) {
    return (ShadowHandlerThread) shadowOf_(instance);
  }

  public static ShadowHttpResponseCache shadowOf(HttpResponseCache instance) {
    return (ShadowHttpResponseCache) shadowOf_(instance);
  }

  public static ShadowImageView shadowOf(ImageView instance) {
    return (ShadowImageView) shadowOf_(instance);
  }

  public static ShadowInputMethodManager shadowOf(InputMethodManager instance) {
    return (ShadowInputMethodManager) shadowOf_(instance);
  }

  public static ShadowInputDevice shadowOf(InputDevice instance) {
    return (ShadowInputDevice) shadowOf_(instance);
  }

  public static ShadowIntent shadowOf(Intent instance) {
    return (ShadowIntent) shadowOf_(instance);
  }

  public static ShadowIntentService shadowOf(IntentService instance) {
    return (ShadowIntentService) shadowOf_(instance);
  }

  public static ShadowJsPromptResult shadowOf(JsPromptResult instance) {
    return (ShadowJsPromptResult) shadowOf_(instance);
  }

  public static ShadowJsResult shadowOf(JsResult instance) {
    return (ShadowJsResult) shadowOf_(instance);
  }

  public static ShadowKeyEvent shadowOf(KeyEvent instance) {
    return (ShadowKeyEvent) shadowOf_(instance);
  }

  public static ShadowKeyguardManager shadowOf(KeyguardManager instance) {
    return (ShadowKeyguardManager) shadowOf_(instance);
  }

  public static ShadowKeyguardManager.ShadowKeyguardLock shadowOf(KeyguardManager.KeyguardLock instance) {
    return (ShadowKeyguardManager.ShadowKeyguardLock) shadowOf_(instance);
  }

  public static ShadowLinearLayout shadowOf(LinearLayout instance) {
    return (ShadowLinearLayout) shadowOf_(instance);
  }

  public static ShadowLinearGradient shadowOf(LinearGradient instance) {
    return (ShadowLinearGradient) shadowOf_(instance);
  }

  public static ShadowListPreference shadowOf(ListPreference instance) {
    return (ShadowListPreference) shadowOf_(instance);
  }

  public static ShadowListView shadowOf(ListView instance) {
    return (ShadowListView) shadowOf_(instance);
  }

  public static ShadowLocationManager shadowOf(LocationManager instance) {
    return (ShadowLocationManager) shadowOf_(instance);
  }

  public static ShadowLooper shadowOf(Looper instance) {
    return (ShadowLooper) shadowOf_(instance);
  }

  public static ShadowMatrix shadowOf(Matrix other) {
    return (ShadowMatrix) Robolectric.shadowOf_(other);
  }

  public static ShadowMediaPlayer shadowOf(MediaPlayer instance) {
    return (ShadowMediaPlayer) shadowOf_(instance);
  }

  public static ShadowMediaRecorder shadowOf(MediaRecorder instance) {
    return (ShadowMediaRecorder) shadowOf_(instance);
  }

  public static ShadowMenuInflater shadowOf(MenuInflater instance) {
    return (ShadowMenuInflater) shadowOf_(instance);
  }

  public static ShadowMimeTypeMap shadowOf(MimeTypeMap instance) {
    return (ShadowMimeTypeMap) shadowOf_(instance);
  }

  public static ShadowMotionEvent shadowOf(MotionEvent other) {
    return (ShadowMotionEvent) Robolectric.shadowOf_(other);
  }

  public static ShadowNetworkInfo shadowOf(NetworkInfo instance) {
    return (ShadowNetworkInfo) shadowOf_(instance);
  }

  public static ShadowNotification shadowOf(Notification other) {
    return (ShadowNotification) Robolectric.shadowOf_(other);
  }

  public static ShadowNotificationManager shadowOf(NotificationManager other) {
    return (ShadowNotificationManager) Robolectric.shadowOf_(other);
  }

  public static ShadowNumberPicker shadowOf(NumberPicker other) {
    return (ShadowNumberPicker) Robolectric.shadowOf_(other);
  }

  public static ShadowObjectAnimator shadowOf(ObjectAnimator instance) {
    return (ShadowObjectAnimator) shadowOf_(instance);
  }

  public static ShadowPaint shadowOf(Paint instance) {
    return (ShadowPaint) shadowOf_(instance);
  }

  public static ShadowParcel shadowOf(Parcel instance) {
    return (ShadowParcel) shadowOf_(instance);
  }

  public static ShadowPath shadowOf(Path instance) {
    return (ShadowPath) shadowOf_(instance);
  }

  public static ShadowPendingIntent shadowOf(PendingIntent instance) {
    return (ShadowPendingIntent) shadowOf_(instance);
  }

  public static ShadowPopupWindow shadowOf(PopupWindow instance) {
    return (ShadowPopupWindow) shadowOf_(instance);
  }

  public static ShadowPowerManager shadowOf(PowerManager instance) {
    return (ShadowPowerManager) shadowOf_(instance);
  }

  public static ShadowPowerManager.ShadowWakeLock shadowOf(PowerManager.WakeLock instance) {
    return (ShadowPowerManager.ShadowWakeLock) shadowOf_(instance);
  }

  public static ShadowPreference shadowOf(Preference instance) {
    return (ShadowPreference) shadowOf_(instance);
  }

  public static ShadowPreferenceActivity shadowOf(PreferenceActivity instance) {
    return (ShadowPreferenceActivity) shadowOf_(instance);
  }

  public static ShadowPreferenceCategory shadowOf(PreferenceCategory instance) {
    return (ShadowPreferenceCategory) shadowOf_(instance);
  }

  public static ShadowPreferenceGroup shadowOf(PreferenceGroup instance) {
    return (ShadowPreferenceGroup) shadowOf_(instance);
  }

  public static ShadowPreferenceScreen shadowOf(PreferenceScreen instance) {
    return (ShadowPreferenceScreen) shadowOf_(instance);
  }

  public static ShadowProgressBar shadowOf(ProgressBar instance) {
    return (ShadowProgressBar) shadowOf_(instance);
  }

  public static ShadowProgressDialog shadowOf(ProgressDialog instance) {
    return (ShadowProgressDialog) shadowOf_(instance);
  }

  public static ShadowRemoteViews shadowOf(RemoteViews instance) {
    return (ShadowRemoteViews) shadowOf_(instance);
  }

  public static ShadowResolveInfo shadowOf(ResolveInfo instance) {
    return (ShadowResolveInfo) shadowOf_(instance);
  }

  public static ShadowResourceCursorAdapter shadowOf(ResourceCursorAdapter instance) {
    return (ShadowResourceCursorAdapter) shadowOf_(instance);
  }

  public static ShadowResources shadowOf(Resources instance) {
    return (ShadowResources) shadowOf_(instance);
  }

  public static ShadowResultReceiver shadowOf(ResultReceiver instance) {
    return (ShadowResultReceiver) shadowOf_(instance);
  }

  public static ShadowScaleGestureDetector shadowOf(ScaleGestureDetector instance) {
    return (ShadowScaleGestureDetector) shadowOf_(instance);
  }

  public static ShadowScanResult shadowOf(ScanResult instance) {
    return (ShadowScanResult) shadowOf_(instance);
  }

  public static ShadowScroller shadowOf(Scroller instance) {
    return (ShadowScroller) shadowOf_(instance);
  }

  public static ShadowScrollView shadowOf(ScrollView instance) {
    return (ShadowScrollView) shadowOf_(instance);
  }

  public static ShadowSeekBar shadowOf(SeekBar instance) {
    return (ShadowSeekBar) shadowOf_(instance);
  }

  public static ShadowSensorManager shadowOf(SensorManager instance) {
    return (ShadowSensorManager) shadowOf_(instance);
  }

  public static ShadowService shadowOf(Service instance) {
    return (ShadowService) shadowOf_(instance);
  }

  public static ShadowSimpleCursorAdapter shadowOf(SimpleCursorAdapter instance) {
    return (ShadowSimpleCursorAdapter) shadowOf_(instance);
  }

  public static ShadowSmsManager shadowOf(SmsManager instance) {
    return (ShadowSmsManager) shadowOf_(instance);
  }

  public static ShadowSslErrorHandler shadowOf(SslErrorHandler instance) {
    return (ShadowSslErrorHandler) shadowOf_(instance);
  }

  public static ShadowStateListDrawable shadowOf(StateListDrawable instance) {
    return (ShadowStateListDrawable) shadowOf_(instance);
  }

  public static ShadowTabHost shadowOf(TabHost instance) {
    return (ShadowTabHost) shadowOf_(instance);
  }

  public static ShadowTabHost.ShadowTabSpec shadowOf(TabHost.TabSpec instance) {
    return (ShadowTabHost.ShadowTabSpec) shadowOf_(instance);
  }

  public static ShadowTelephonyManager shadowOf(TelephonyManager instance) {
    return (ShadowTelephonyManager) shadowOf_(instance);
  }

  public static ShadowTextPaint shadowOf(TextPaint instance) {
    return (ShadowTextPaint) shadowOf_(instance);
  }

  public static ShadowTextToSpeech shadowOf(TextToSpeech instance) {
    return (ShadowTextToSpeech) shadowOf_(instance);
  }

  public static ShadowTextView shadowOf(TextView instance) {
    return (ShadowTextView) shadowOf_(instance);
  }

  public static ShadowResources.ShadowTheme shadowOf(Resources.Theme instance) {
    return (ShadowResources.ShadowTheme) shadowOf_(instance);
  }

  public static ShadowTimePickerDialog shadowOf(TimePickerDialog instance) {
    return (ShadowTimePickerDialog) shadowOf_(instance);
  }

  public static ShadowToast shadowOf(Toast instance) {
    return (ShadowToast) shadowOf_(instance);
  }

  public static ShadowTouchDelegate shadowOf(TouchDelegate instance) {
    return (ShadowTouchDelegate) shadowOf_(instance);
  }

  public static ShadowTranslateAnimation shadowOf(TranslateAnimation instance) {
    return (ShadowTranslateAnimation) shadowOf_(instance);
  }

  public static ShadowTypedArray shadowOf(TypedArray instance) {
    return (ShadowTypedArray) shadowOf_(instance);
  }

  public static ShadowTypeface shadowOf(Typeface instance) {
    return (ShadowTypeface) shadowOf_(instance);
  }

  public static ShadowView shadowOf(View instance) {
    return (ShadowView) shadowOf_(instance);
  }

  public static ShadowViewAnimator shadowOf(ViewAnimator instance) {
    return (ShadowViewAnimator) shadowOf_(instance);
  }

  public static ShadowViewConfiguration shadowOf(ViewConfiguration instance) {
    return (ShadowViewConfiguration) shadowOf_(instance);
  }

  public static ShadowViewTreeObserver shadowOf(ViewTreeObserver instance) {
    return (ShadowViewTreeObserver) shadowOf_(instance);
  }

  public static ShadowViewGroup shadowOf(ViewGroup instance) {
    return (ShadowViewGroup) shadowOf_(instance);
  }

  public static ShadowVideoView shadowOf(VideoView instance) {
    return (ShadowVideoView) shadowOf_(instance);
  }

  public static ShadowWebView shadowOf(WebView instance) {
    return (ShadowWebView) shadowOf_(instance);
  }

  public static ShadowWebViewDatabase shadowOf(WebViewDatabase instance) {
    return (ShadowWebViewDatabase) shadowOf_(instance);
  }

  public static ShadowWifiConfiguration shadowOf(WifiConfiguration instance) {
    return (ShadowWifiConfiguration) shadowOf_(instance);
  }

  public static ShadowWifiInfo shadowOf(WifiInfo instance) {
    return (ShadowWifiInfo) shadowOf_(instance);
  }

  public static ShadowWifiManager shadowOf(WifiManager instance) {
    return (ShadowWifiManager) shadowOf_(instance);
  }

  public static ShadowWindow shadowOf(Window instance) {
    return (ShadowWindow) shadowOf_(instance);
  }

  public static ShadowZoomButtonsController shadowOf(ZoomButtonsController instance) {
    return (ShadowZoomButtonsController) shadowOf_(instance);
  }

  public static ShadowWindowManager shadowOf(WindowManager instance) {
    return (ShadowWindowManager) shadowOf_(instance);
  }

  public static ShadowDrawerLayout shadowOf(DrawerLayout instance) {
    return (ShadowDrawerLayout) shadowOf_(instance);
  }

  public static ShadowPopupMenu shadowOf(PopupMenu instance) {
    return (ShadowPopupMenu) shadowOf_(instance);
  }

  public static ShadowDownloadManager shadowOf(DownloadManager instance) {
    return (ShadowDownloadManager) shadowOf_(instance);
  }

  public static ShadowDownloadManager.ShadowRequest shadowOf(DownloadManager.Request instance) {
    return (ShadowDownloadManager.ShadowRequest) shadowOf_(instance);
  }

  public static ShadowSurface shadowOf(Surface surface) {
    return (ShadowSurface) shadowOf_(surface);
  }

  @SuppressWarnings({"unchecked"})
  public static <P, R> P shadowOf_(R instance) {
    return (P) getShadowWrangler().shadowOf(instance);
  }

  /**
   * Runs any background tasks previously queued by {@link android.os.AsyncTask#execute(Object[])}.
   * <p/>
   * <p/>
   * Note: calling this method does not pause or un-pause the scheduler.
   */
  public static void runBackgroundTasks() {
    getBackgroundScheduler().advanceBy(0);
  }

  /**
   * Runs any immediately runnable tasks previously queued on the UI thread,
   * e.g. by {@link Activity#runOnUiThread(Runnable)} or {@link android.os.AsyncTask#onPostExecute(Object)}.
   * <p/>
   * <p/>
   * Note: calling this method does not pause or un-pause the scheduler.
   */
  public static void runUiThreadTasks() {
    getUiThreadScheduler().advanceBy(0);
  }

  public static void runUiThreadTasksIncludingDelayedTasks() {
    getUiThreadScheduler().advanceToLastPostedRunnable();
  }

  /**
   * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
   *
   * @param statusCode   the status code of the response
   * @param responseBody the body of the response
   * @param headers      optional headers for the request
   */
  public static void addPendingHttpResponse(int statusCode, String responseBody, Header... headers) {
    getFakeHttpLayer().addPendingHttpResponse(statusCode, responseBody, headers);
  }

  /**
   * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
   *
   * @param statusCode   the status code of the response
   * @param responseBody the body of the response
   * @param contentType  the contentType of the response
   * @deprecated use {@link #addPendingHttpResponse(int, String, Header...)} instead
   */
  public static void addPendingHttpResponseWithContentType(int statusCode, String responseBody, Header contentType) {
    getFakeHttpLayer().addPendingHttpResponse(statusCode, responseBody, contentType);
  }

  /**
   * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
   *
   * @param httpResponse the response
   */
  public static void addPendingHttpResponse(HttpResponse httpResponse) {
    getFakeHttpLayer().addPendingHttpResponse(httpResponse);
  }

  /**
   * Sets up an HTTP response to be returned by calls to Apache's {@code HttpClient} implementers.
   *
   * @param httpResponseGenerator an HttpResponseGenerator that will provide responses
   */
  public static void addPendingHttpResponse(HttpResponseGenerator httpResponseGenerator) {
    getFakeHttpLayer().addPendingHttpResponse(httpResponseGenerator);
  }

  /**
   * Accessor to obtain HTTP requests made during the current test in the order in which they were made.
   *
   * @param index index of the request to retrieve.
   * @return the requested request.
   */
  public static HttpRequest getSentHttpRequest(int index) {
    return getFakeHttpLayer().getSentHttpRequestInfo(index).getHttpRequest();
  }

  public static HttpRequest getLatestSentHttpRequest() {
    return ShadowDefaultRequestDirector.getLatestSentHttpRequest();
  }

  /**
   * Accessor to find out if HTTP requests were made during the current test.
   *
   * @return whether a request was made.
   */
  public static boolean httpRequestWasMade() {
    return getShadowApplication().getFakeHttpLayer().hasRequestInfos();
  }

  public static boolean httpRequestWasMade(String uri) {
    return getShadowApplication().getFakeHttpLayer().hasRequestMatchingRule(
        new FakeHttpLayer.UriRequestMatcher(uri));
  }

  /**
   * Accessor to obtain metadata for an HTTP request made during the current test in the order in which they were made.
   *
   * @param index index of the request to retrieve.
   * @return the requested request metadata.
   */
  public static HttpRequestInfo getSentHttpRequestInfo(int index) {
    return getFakeHttpLayer().getSentHttpRequestInfo(index);
  }

  /**
   * Accessor to obtain HTTP requests made during the current test in the order in which they were made.
   *
   * @return the requested request or null if there are none.
   */
  public static HttpRequest getNextSentHttpRequest() {
    HttpRequestInfo httpRequestInfo = getFakeHttpLayer().getNextSentHttpRequestInfo();
    return httpRequestInfo == null ? null : httpRequestInfo.getHttpRequest();
  }

  /**
   * Accessor to obtain metadata for an HTTP request made during the current test in the order in which they were made.
   *
   * @return the requested request metadata or null if there are none.
   */
  public static HttpRequestInfo getNextSentHttpRequestInfo() {
    return getFakeHttpLayer().getNextSentHttpRequestInfo();
  }

  /**
   * Adds an HTTP response rule. The response will be returned when the rule is matched.
   *
   * @param method   method to match.
   * @param uri      uri to match.
   * @param response response to return when a match is found.
   */
  public static void addHttpResponseRule(String method, String uri, HttpResponse response) {
    getFakeHttpLayer().addHttpResponseRule(method, uri, response);
  }

  /**
   * Adds an HTTP response rule with a default method of GET. The response will be returned when the rule is matched.
   *
   * @param uri      uri to match.
   * @param response response to return when a match is found.
   */
  public static void addHttpResponseRule(String uri, HttpResponse response) {
    getFakeHttpLayer().addHttpResponseRule(uri, response);
  }

  /**
   * Adds an HTTP response rule. The response will be returned when the rule is matched.
   *
   * @param uri      uri to match.
   * @param response response to return when a match is found.
   */
  public static void addHttpResponseRule(String uri, String response) {
    getFakeHttpLayer().addHttpResponseRule(uri, response);
  }

  /**
   * Adds an HTTP response rule. The response will be returned when the rule is matched.
   *
   * @param requestMatcher custom {@code RequestMatcher}.
   * @param response       response to return when a match is found.
   */
  public static void addHttpResponseRule(RequestMatcher requestMatcher, HttpResponse response) {
    getFakeHttpLayer().addHttpResponseRule(requestMatcher, response);
  }

  /**
   * Adds an HTTP response rule. For each time the rule is matched, responses will be shifted
   * off the list and returned. When all responses have been given and the rule is matched again,
   * an exception will be thrown.
   *
   * @param requestMatcher custom {@code RequestMatcher}.
   * @param responses      responses to return in order when a match is found.
   */
  public static void addHttpResponseRule(RequestMatcher requestMatcher, List<? extends HttpResponse> responses) {
    getFakeHttpLayer().addHttpResponseRule(requestMatcher, responses);
  }

  public static FakeHttpLayer getFakeHttpLayer() {
    return getShadowApplication().getFakeHttpLayer();
  }

  public static void setDefaultHttpResponse(int statusCode, String responseBody) {
    getFakeHttpLayer().setDefaultHttpResponse(statusCode, responseBody);
  }

  public static void setDefaultHttpResponse(HttpResponse defaultHttpResponse) {
    getFakeHttpLayer().setDefaultHttpResponse(defaultHttpResponse);
  }

  public static void clearHttpResponseRules() {
    getFakeHttpLayer().clearHttpResponseRules();
  }

  public static void clearPendingHttpResponses() {
    getFakeHttpLayer().clearPendingHttpResponses();
  }

  public static void pauseLooper(Looper looper) {
    ShadowLooper.pauseLooper(looper);
  }

  public static void unPauseLooper(Looper looper) {
    ShadowLooper.unPauseLooper(looper);
  }

  public static void pauseMainLooper() {
    ShadowLooper.pauseMainLooper();
  }

  public static void unPauseMainLooper() {
    ShadowLooper.unPauseMainLooper();
  }

  public static void idleMainLooper(long interval) {
    ShadowLooper.idleMainLooper(interval);
  }

  public static void idleMainLooperConstantly(boolean shouldIdleConstantly) {
    ShadowLooper.idleMainLooperConstantly(shouldIdleConstantly);
  }

  public static Scheduler getUiThreadScheduler() {
    return shadowOf(Looper.getMainLooper()).getScheduler();
  }

  public static Scheduler getBackgroundScheduler() {
    return getShadowApplication().getBackgroundScheduler();
  }

  public static ShadowApplication getShadowApplication() {
    return Robolectric.application == null ? null : shadowOf(Robolectric.application);
  }

  public static void setDisplayMetricsDensity(float densityMultiplier) {
    shadowOf(getShadowApplication().getResources()).setDensity(densityMultiplier);
  }

  public static void setDefaultDisplay(Display display) {
    shadowOf(getShadowApplication().getResources()).setDisplay(display);
  }

  /**
   * Calls {@code performClick()} on a {@code View} after ensuring that it and its ancestors are visible and that it
   * is enabled.
   *
   * @param view the view to click on
   * @return true if {@code View.OnClickListener}s were found and fired, false otherwise.
   * @throws RuntimeException if the preconditions are not met.
   */
  public static boolean clickOn(View view) {
    return shadowOf(view).checkedPerformClick();
  }

  /**
   * Returns a textual representation of the appearance of the object.
   *
   * @param view the view to visualize
   */
  public static String visualize(View view) {
    Canvas canvas = new Canvas();
    view.draw(canvas);
    return shadowOf(canvas).getDescription();
  }

  /**
   * Returns a textual representation of the appearance of the object.
   *
   * @param canvas the canvas to visualize
   */
  public static String visualize(Canvas canvas) {
    return shadowOf(canvas).getDescription();
  }

  /**
   * Returns a textual representation of the appearance of the object.
   *
   * @param bitmap the bitmap to visualize
   */
  public static String visualize(Bitmap bitmap) {
    return shadowOf(bitmap).getDescription();
  }

  /**
   * Emits an xml-like representation of the view to System.out.
   *
   * @param view the view to dump
   */
  @SuppressWarnings("UnusedDeclaration")
  public static void dump(View view) {
    shadowOf(view).dump();
  }

  /**
   * Returns the text contained within this view.
   *
   * @param view the view to scan for text
   */
  @SuppressWarnings("UnusedDeclaration")
  public static String innerText(View view) {
    return shadowOf(view).innerText();
  }

  public static ResourceLoader getResourceLoader(Context context) {
    return shadowOf(context.getApplicationContext()).getResourceLoader();
  }

  public static void reset() {
    Robolectric.application = null;
    Robolectric.packageManager = null;
    Robolectric.activityThread = null;
    ShadowAccountManager.reset();
    ShadowResources.reset();
    ShadowBinder.reset();
    ShadowBitmapFactory.reset();
    ShadowDrawable.reset();
    ShadowMediaStore.reset();
    ShadowLog.reset();
    ShadowContext.clearFilesAndCache();
    ShadowLooper.resetThreadLoopers();
    ShadowChoreographer.resetThreadLoopers();
    ShadowDialog.reset();
    ShadowContentResolver.reset();
//        ShadowLocalBroadcastManager.reset();
    ShadowMimeTypeMap.reset();
    ShadowPowerManager.reset();
    ShadowStatFs.reset();
    ShadowTypeface.reset();
    ShadowProcess.reset();
    ShadowMediaMetadataRetriever.reset();
  }

  public static <T extends Service> ServiceController<T> buildService(Class<T> serviceClass) {
    return new ServiceController<T>(serviceClass);
  }

  public static <T extends Service> T setupService(Class<T> serviceClass) {
    return new ServiceController<T>(serviceClass).attach().create().get();
  }

  public static <T extends Activity> ActivityController<T> buildActivity(Class<T> activityClass) {
    return new ActivityController<T>(activityClass);
  }

  public static <T extends Activity> T setupActivity(Class<T> activityClass) {
    return new ActivityController<T>(activityClass).create().start().resume().visible().get();
  }

  /**
   * Set to true if you'd like Robolectric to strictly simulate the real Android behavior when
   * calling {@link Context#startActivity(android.content.Intent)}. Real Android throws a
   * {@link android.content.ActivityNotFoundException} if given
   * an {@link Intent} that is not known to the {@link android.content.pm.PackageManager}
   *
   * By default, this behavior is off (false).
   *
   * @param checkActivities
   */
  public static void checkActivities(boolean checkActivities) {
    shadowOf(application).checkActivities(checkActivities);
  }

  /**
   * Reflection helper methods.
   */
  public static class Reflection {
    public static <T> T newInstanceOf(Class<T> clazz) {
      return Robolectric.newInstanceOf(clazz);
    }

    public static Object newInstanceOf(String className) {
      return Robolectric.newInstanceOf(className);
    }

    public static void setFinalStaticField(Class classWhichContainsField, String fieldName, Object newValue) {
      try {
        Field field = classWhichContainsField.getDeclaredField(fieldName);
        setFinalStaticField(field, newValue);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      }
    }

    public static Object setFinalStaticField(Field field, Object newValue) {
      Object oldValue;

      try {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        oldValue = field.get(null);
        field.set(null, newValue);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      return oldValue;
    }
  }

  // marker for shadow classes when the implementation class is unlinkable
  public interface Anything {
  }
}
