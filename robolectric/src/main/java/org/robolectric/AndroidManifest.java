package org.robolectric;

import android.app.Activity;
import android.graphics.Color;
import org.fest.util.Lists;
import org.robolectric.res.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP;
import static android.content.pm.ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA;
import static android.content.pm.ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING;
import static android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE;
import static android.content.pm.ApplicationInfo.FLAG_HAS_CODE;
import static android.content.pm.ApplicationInfo.FLAG_KILL_AFTER_RESTORE;
import static android.content.pm.ApplicationInfo.FLAG_PERSISTENT;
import static android.content.pm.ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS;
import static android.content.pm.ApplicationInfo.FLAG_RESTORE_ANY_VERSION;
import static android.content.pm.ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS;
import static android.content.pm.ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS;
import static android.content.pm.ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES;
import static android.content.pm.ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS;
import static android.content.pm.ApplicationInfo.FLAG_TEST_ONLY;
import static android.content.pm.ApplicationInfo.FLAG_VM_SAFE_MODE;

public class AndroidManifest {
  private final FsFile androidManifestFile;
  private final FsFile resDirectory;
  private final FsFile assetsDirectory;
  private boolean manifestIsParsed;

  private String applicationName;
  private String applicationLabel;
  private String rClassName;
  private String packageName;
  private String processName;
  private String themeRef;
  private String labelRef;
  private Integer targetSdkVersion;
  private Integer minSdkVersion;
  private int versionCode;
  private String versionName;
  private int applicationFlags;
  private final List<ContentProviderData> providers = new ArrayList<ContentProviderData>();
  private final List<ReceiverAndIntentFilter> receivers = new ArrayList<ReceiverAndIntentFilter>();
  private final Map<String, ActivityData> activityDatas = new LinkedHashMap<String, ActivityData>();
  private final List<String> usedPermissions = new ArrayList<String>();
  private MetaData applicationMetaData;
  private List<AndroidManifest> libraryManifests;

  /**
   * Creates a Robolectric configuration using default Android files relative to the specified base directory.
   * <p/>
   * The manifest will be baseDir/AndroidManifest.xml, res will be baseDir/res, and assets in baseDir/assets.
   *
   * @param baseDir the base directory of your Android project
   * @deprecated Use {@link #AndroidManifest(org.robolectric.res.FsFile, org.robolectric.res.FsFile, org.robolectric.res.FsFile)} instead.}
   */
  public AndroidManifest(final File baseDir) {
    this(Fs.newFile(baseDir));
  }

  public AndroidManifest(final FsFile androidManifestFile, final FsFile resDirectory) {
    this(androidManifestFile, resDirectory, resDirectory.getParent().join("assets"));
  }

  /**
   * @deprecated Use {@link #AndroidManifest(org.robolectric.res.FsFile, org.robolectric.res.FsFile, org.robolectric.res.FsFile)} instead.}
   */
  public AndroidManifest(final FsFile baseDir) {
    this(baseDir.join("AndroidManifest.xml"), baseDir.join("res"), baseDir.join("assets"));
  }

  /**
   * Creates a Robolectric configuration using specified locations.
   *
   * @param androidManifestFile location of the AndroidManifest.xml file
   * @param resDirectory        location of the res directory
   * @param assetsDirectory     location of the assets directory
   */
  public AndroidManifest(FsFile androidManifestFile, FsFile resDirectory, FsFile assetsDirectory) {
    this.androidManifestFile = androidManifestFile;
    this.resDirectory = resDirectory;
    this.assetsDirectory = assetsDirectory;
  }

  public String getThemeRef(Class<? extends Activity> activityClass) {
    ActivityData activityData = getActivityData(activityClass.getName());
    String themeRef = activityData != null ? activityData.getThemeRef() : null;
    if (themeRef == null) {
      themeRef = getThemeRef();
    }
    return themeRef;
  }

  public String getRClassName() throws Exception {
    parseAndroidManifest();
    return rClassName;
  }

  public Class getRClass() {
    try {
      String rClassName = getRClassName();
      return Class.forName(rClassName);
    } catch (Exception e) {
        return null;
    }
  }

  public void validate() {
    if (!androidManifestFile.exists() || !androidManifestFile.isFile()) {
      throw new RuntimeException(androidManifestFile + " not found or not a file; it should point to your project's AndroidManifest.xml");
    }
  }

  void parseAndroidManifest() {
    if (manifestIsParsed) {
      return;
    }
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      InputStream inputStream = androidManifestFile.getInputStream();
      Document manifestDocument = db.parse(inputStream);
      inputStream.close();

      if (packageName == null) {
        packageName = getTagAttributeText(manifestDocument, "manifest", "package");
      }
      versionCode = getTagAttributeIntValue(manifestDocument, "manifest", "android:versionCode", 0);
      versionName = getTagAttributeText(manifestDocument, "manifest", "android:versionName");
      rClassName = packageName + ".R";
      applicationName = getTagAttributeText(manifestDocument, "application", "android:name");
      applicationLabel = getTagAttributeText(manifestDocument, "application", "android:label");
      minSdkVersion = getTagAttributeIntValue(manifestDocument, "uses-sdk", "android:minSdkVersion");
      targetSdkVersion = getTagAttributeIntValue(manifestDocument, "uses-sdk", "android:targetSdkVersion");
      processName = getTagAttributeText(manifestDocument, "application", "android:process");
      if (processName == null) {
        processName = packageName;
      }

      themeRef = getTagAttributeText(manifestDocument, "application", "android:theme");
      labelRef = getTagAttributeText(manifestDocument, "application", "android:label");

      parseApplicationFlags(manifestDocument);
      parseReceivers(manifestDocument);
      parseActivities(manifestDocument);
      parseApplicationMetaData(manifestDocument);
      parseContentProviders(manifestDocument);
      parseUsedPermissions(manifestDocument);
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
    manifestIsParsed = true;
  }

  private void parseUsedPermissions(Document manifestDocument) {
    NodeList elementsByTagName = manifestDocument.getElementsByTagName("uses-permission");
    int length = elementsByTagName.getLength();
    for (int i = 0; i < length; i++) {
      Node node = elementsByTagName.item(i).getAttributes().getNamedItem("android:name");
      usedPermissions.add(node.getNodeValue());
    }
  }

  private void parseContentProviders(Document manifestDocument) {
    Node application = manifestDocument.getElementsByTagName("application").item(0);
    if (application == null) return;

    for (Node contentProviderNode : getChildrenTags(application, "provider")) {
      Node nameItem = contentProviderNode.getAttributes().getNamedItem("android:name");
      Node authorityItem = contentProviderNode.getAttributes().getNamedItem("android:authorities");
      if (nameItem != null && authorityItem != null) {
        providers.add(new ContentProviderData(resolveClassRef(nameItem.getTextContent()), authorityItem.getTextContent()));
      }
    }
  }

  private void parseReceivers(final Document manifestDocument) {
    Node application = manifestDocument.getElementsByTagName("application").item(0);
    if (application == null) return;

    for (Node receiverNode : getChildrenTags(application, "receiver")) {
      Node namedItem = receiverNode.getAttributes().getNamedItem("android:name");
      if (namedItem == null) continue;

      String receiverName = resolveClassRef(namedItem.getTextContent());
      MetaData metaData = new MetaData(getChildrenTags(receiverNode, "meta-data"));

      for (Node intentFilterNode : getChildrenTags(receiverNode, "intent-filter")) {
        List<String> actions = new ArrayList<String>();
        for (Node actionNode : getChildrenTags(intentFilterNode, "action")) {
          Node nameNode = actionNode.getAttributes().getNamedItem("android:name");
          if (nameNode != null) {
            actions.add(nameNode.getTextContent());
          }
        }
        receivers.add(new ReceiverAndIntentFilter(receiverName, actions, metaData));
      }
    }
  }

  private void parseActivities(final Document manifestDocument) {
    Node application = manifestDocument.getElementsByTagName("application").item(0);
    if (application == null) return;

    for (Node activityNode : getChildrenTags(application, "activity")) {
      final NamedNodeMap attributes = activityNode.getAttributes();
      final int attrCount = attributes.getLength();
      final List<IntentFilterData> intentFilterData = parseIntentFilters(activityNode);
      final HashMap<String, String> activityAttrs = new HashMap<String, String>(attrCount);
      for(int i = 0; i < attrCount; i++) {
        Node attr = attributes.item(i);
        String v = attr.getNodeValue();
        if( v != null) {
          activityAttrs.put(attr.getNodeName(), v);
        }
      }

      String activityName = resolveClassRef(activityAttrs.get(ActivityData.getNameAttr("android")));
      if (activityName == null) {
        continue;
      }
      activityAttrs.put(ActivityData.getNameAttr("android"), activityName);
      activityDatas.put(activityName, new ActivityData(activityAttrs, intentFilterData));
    }
  }

  private List<IntentFilterData> parseIntentFilters(final Node activityNode) {
    ArrayList<IntentFilterData> intentFilterDatas = new ArrayList<IntentFilterData>();
    for (Node n : getChildrenTags(activityNode, "intent-filter")) {
      ArrayList<String> actionNames = new ArrayList<String>();
      ArrayList<String> categories = new ArrayList<String>();
      //should only be one action.
      for (Node action : getChildrenTags(n, "action")) {
        NamedNodeMap attributes = action.getAttributes();
        Node actionNameNode = attributes.getNamedItem("android:name");
        if (actionNameNode != null) {
          actionNames.add(actionNameNode.getNodeValue());
        }
      }
      for (Node category : getChildrenTags(n, "category")) {
        NamedNodeMap attributes = category.getAttributes();
        Node categoryNameNode = attributes.getNamedItem("android:name");
        if (categoryNameNode != null) {
          categories.add(categoryNameNode.getNodeValue());
        }
      }
      IntentFilterData intentFilterData = new IntentFilterData(actionNames, categories);
      intentFilterData = parseIntentFilterData(n, intentFilterData);
      intentFilterDatas.add(intentFilterData);
    }

    return intentFilterDatas;
  }

  private IntentFilterData parseIntentFilterData(final Node intentFilterNode, IntentFilterData intentFilterData) {
    for (Node n : getChildrenTags(intentFilterNode, "data")) {
      NamedNodeMap attributes = n.getAttributes();
      String host = null;
      String port = null;

      Node schemeNode = attributes.getNamedItem("android:scheme");
      if (schemeNode != null) {
        intentFilterData.addScheme(schemeNode.getNodeValue());
      }

      Node hostNode = attributes.getNamedItem("android:host");
      if (hostNode != null) {
        host = hostNode.getNodeValue();
      }

      Node portNode = attributes.getNamedItem("android:port");
      if (portNode != null) {
        port = portNode.getNodeValue();
      }
      intentFilterData.addAuthority(host, port);

      Node pathNode = attributes.getNamedItem("android:path");
      if (pathNode != null) {
        intentFilterData.addPath(pathNode.getNodeValue());
      }

      Node pathPatternNode = attributes.getNamedItem("android:pathPattern");
      if (pathPatternNode != null) {
        intentFilterData.addPathPattern(pathPatternNode.getNodeValue());
      }

      Node pathPrefixNode = attributes.getNamedItem("android:pathPrefix");
      if (pathPrefixNode != null) {
        intentFilterData.addPathPrefix(pathPrefixNode.getNodeValue());
      }

      Node mimeTypeNode = attributes.getNamedItem("android:mimeType");
      if (mimeTypeNode != null) {
        intentFilterData.addMimeType(mimeTypeNode.getNodeValue());
      }
    }
    return intentFilterData;
  }

  /***
   * Attempt to parse a string in to it's appropriate type
   * @param value Value to parse
   * @return Parsed result
   */
  private static Object parseValue(String value) {
    if (value == null) {
      return null;
    } else if ("true".equals(value)) {
      return true;
    } else if ("false".equals(value)) {
      return false;
    } else if (value.startsWith("#")) {
      // if it's a color, add it and continue
      try {
        return Color.parseColor(value);
      } catch (IllegalArgumentException e) {
            /* Not a color */
      }
    } else if (value.contains(".")) {
      // most likely a float
      try {
        return Float.parseFloat(value);
      } catch (NumberFormatException e) {
          // Not a float
      }
    } else {
      // if it's an int, add it and continue
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException ei) {
          // Not an int
      }
    }

    // Not one of the above types, keep as String
    return value;
  }

  /***
   * Allows {@link org.robolectric.res.builder.RobolectricPackageManager} to provide
   * a resource index for initialising the resource attributes in all the metadata elements
   * @param resLoader used for getting resource IDs from string identifiers
   */
  public void initMetaData(ResourceLoader resLoader) {
    applicationMetaData.init(resLoader, packageName);
    for (ReceiverAndIntentFilter receiver : receivers) {
      receiver.metaData.init(resLoader, packageName);
    }
  }

  private void parseApplicationMetaData(final Document manifestDocument) {
    Node application = manifestDocument.getElementsByTagName("application").item(0);
    if (application == null) return;
    applicationMetaData = new MetaData(getChildrenTags(application, "meta-data"));
  }

  private String resolveClassRef(String maybePartialClassName) {
    return (maybePartialClassName.startsWith(".")) ? packageName + maybePartialClassName : maybePartialClassName;
  }

  private List<Node> getChildrenTags(final Node node, final String tagName) {
    List<Node> children = new ArrayList<Node>();
    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      Node childNode = node.getChildNodes().item(i);
      if (childNode.getNodeName().equalsIgnoreCase(tagName)) {
        children.add(childNode);
      }
    }
    return children;
  }

  private void parseApplicationFlags(final Document manifestDocument) {
    applicationFlags = getApplicationFlag(manifestDocument, "android:allowBackup", FLAG_ALLOW_BACKUP);
    applicationFlags += getApplicationFlag(manifestDocument, "android:allowClearUserData", FLAG_ALLOW_CLEAR_USER_DATA);
    applicationFlags += getApplicationFlag(manifestDocument, "android:allowTaskReparenting", FLAG_ALLOW_TASK_REPARENTING);
    applicationFlags += getApplicationFlag(manifestDocument, "android:debuggable", FLAG_DEBUGGABLE);
    applicationFlags += getApplicationFlag(manifestDocument, "android:hasCode", FLAG_HAS_CODE);
    applicationFlags += getApplicationFlag(manifestDocument, "android:killAfterRestore", FLAG_KILL_AFTER_RESTORE);
    applicationFlags += getApplicationFlag(manifestDocument, "android:persistent", FLAG_PERSISTENT);
    applicationFlags += getApplicationFlag(manifestDocument, "android:resizeable", FLAG_RESIZEABLE_FOR_SCREENS);
    applicationFlags += getApplicationFlag(manifestDocument, "android:restoreAnyVersion", FLAG_RESTORE_ANY_VERSION);
    applicationFlags += getApplicationFlag(manifestDocument, "android:largeScreens", FLAG_SUPPORTS_LARGE_SCREENS);
    applicationFlags += getApplicationFlag(manifestDocument, "android:normalScreens", FLAG_SUPPORTS_NORMAL_SCREENS);
    applicationFlags += getApplicationFlag(manifestDocument, "android:anyDensity", FLAG_SUPPORTS_SCREEN_DENSITIES);
    applicationFlags += getApplicationFlag(manifestDocument, "android:smallScreens", FLAG_SUPPORTS_SMALL_SCREENS);
    applicationFlags += getApplicationFlag(manifestDocument, "android:testOnly", FLAG_TEST_ONLY);
    applicationFlags += getApplicationFlag(manifestDocument, "android:vmSafeMode", FLAG_VM_SAFE_MODE);
  }

  private int getApplicationFlag(final Document doc, final String attribute, final int attributeValue) {
    String flagString = getTagAttributeText(doc, "application", attribute);
    return "true".equalsIgnoreCase(flagString) ? attributeValue : 0;
  }

  private Integer getTagAttributeIntValue(final Document doc, final String tag, final String attribute) {
    return getTagAttributeIntValue(doc, tag, attribute, null);
  }

  private Integer getTagAttributeIntValue(final Document doc, final String tag, final String attribute, final Integer defaultValue) {
    String valueString = getTagAttributeText(doc, tag, attribute);
    if (valueString != null) {
      return Integer.parseInt(valueString);
    }
    return defaultValue;
  }

  public String getApplicationName() {
    parseAndroidManifest();
    return applicationName;
  }

  public String getActivityLabel(Class<? extends Activity> activity) {
    parseAndroidManifest();
    ActivityData data = getActivityData(activity.getName());
    return (data != null && data.getLabel() != null) ? data.getLabel() : applicationLabel;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getPackageName() {
    parseAndroidManifest();
    return packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getLabelRef() {
    return labelRef;
  }

  public int getMinSdkVersion() {
    parseAndroidManifest();
    return minSdkVersion == null ? 1 : minSdkVersion;
  }

  public int getTargetSdkVersion() {
    parseAndroidManifest();
    return targetSdkVersion == null ? getMinSdkVersion() : targetSdkVersion;
  }

  public int getApplicationFlags() {
    parseAndroidManifest();
    return applicationFlags;
  }

  public String getProcessName() {
    parseAndroidManifest();
    return processName;
  }

  public Map<String, Object> getApplicationMetaData() {
    parseAndroidManifest();
    return applicationMetaData.valueMap;
  }

  public ResourcePath getResourcePath() {
    validate();
    return new ResourcePath(getRClass(), getPackageName(), resDirectory, assetsDirectory);
  }

  public List<ResourcePath> getIncludedResourcePaths() {
    Collection<ResourcePath> resourcePaths = new LinkedHashSet<ResourcePath>(); // Needs stable ordering and no duplicates
    resourcePaths.add(getResourcePath());
    for (AndroidManifest libraryManifest : getLibraryManifests()) {
      resourcePaths.addAll(libraryManifest.getIncludedResourcePaths());
    }
    return Lists.newArrayList(resourcePaths);
  }

  public List<ContentProviderData> getContentProviders() {
    parseAndroidManifest();
    return providers;
  }

  protected void createLibraryManifests() {
    libraryManifests = new ArrayList<AndroidManifest>();
    List<FsFile> libraryBaseDirs = findLibraries();

    for (FsFile libraryBaseDir : libraryBaseDirs) {
      AndroidManifest libraryManifest = createLibraryAndroidManifest(libraryBaseDir);
      libraryManifest.createLibraryManifests();
      libraryManifests.add(libraryManifest);
    }
  }

  protected List<FsFile> findLibraries() {
    FsFile baseDir = getBaseDir();
    List<FsFile> libraryBaseDirs = new ArrayList<FsFile>();

    Properties properties = getProperties(baseDir.join("project.properties"));
    // get the project.properties overrides and apply them (if any)
    Properties overrideProperties = getProperties(baseDir.join("test-project.properties"));
    if (overrideProperties!=null) properties.putAll(overrideProperties);
    if (properties != null) {
      int libRef = 1;
      String lib;
      while ((lib = properties.getProperty("android.library.reference." + libRef)) != null) {
        FsFile libraryBaseDir = baseDir.join(lib);
        if (libraryBaseDir.isDirectory()) {
          // Ignore directories without any files
          FsFile[] libraryBaseDirFiles = libraryBaseDir.listFiles();
          if (libraryBaseDirFiles != null && libraryBaseDirFiles.length > 0) {
            libraryBaseDirs.add(libraryBaseDir);
          }
        }

        libRef++;
      }
    }
    return libraryBaseDirs;
  }

  protected FsFile getBaseDir() {
    return getResDirectory().getParent();
  }

  protected AndroidManifest createLibraryAndroidManifest(FsFile libraryBaseDir) {
    return new AndroidManifest(libraryBaseDir);
  }

  public List<AndroidManifest> getLibraryManifests() {
    if (libraryManifests == null) createLibraryManifests();
    return Collections.unmodifiableList(libraryManifests);
  }

  private static Properties getProperties(FsFile propertiesFile) {
    if (!propertiesFile.exists()) return null;

    Properties properties = new Properties();
    InputStream stream;
    try {
      stream = propertiesFile.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      try {
        properties.load(stream);
      } finally {
        stream.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  public FsFile getResDirectory() {
    return resDirectory;
  }

  public FsFile getAssetsDirectory() {
    return assetsDirectory;
  }

  public int getReceiverCount() {
    parseAndroidManifest();
    return receivers.size();
  }

  public String getReceiverClassName(final int receiverIndex) {
    parseAndroidManifest();
    return receivers.get(receiverIndex).getBroadcastReceiverClassName();
  }

  public List<String> getReceiverIntentFilterActions(final int receiverIndex) {
    parseAndroidManifest();
    return receivers.get(receiverIndex).getIntentFilterActions();
  }

  public Map<String, Object> getReceiverMetaData(final int receiverIndex) {
    parseAndroidManifest();
    return receivers.get(receiverIndex).getMetaData().valueMap;
  }

  private static String getTagAttributeText(final Document doc, final String tag, final String attribute) {
    NodeList elementsByTagName = doc.getElementsByTagName(tag);
    for (int i = 0; i < elementsByTagName.getLength(); ++i) {
      Node item = elementsByTagName.item(i);
      Node namedItem = item.getAttributes().getNamedItem(attribute);
      if (namedItem != null) {
        return namedItem.getTextContent();
      }
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AndroidManifest that = (AndroidManifest) o;

    if (androidManifestFile != null ? !androidManifestFile.equals(that.androidManifestFile) : that.androidManifestFile != null)
      return false;
    if (assetsDirectory != null ? !assetsDirectory.equals(that.assetsDirectory) : that.assetsDirectory != null)
      return false;
    if (resDirectory != null ? !resDirectory.equals(that.resDirectory) : that.resDirectory != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = androidManifestFile != null ? androidManifestFile.hashCode() : 0;
    result = 31 * result + (resDirectory != null ? resDirectory.hashCode() : 0);
    result = 31 * result + (assetsDirectory != null ? assetsDirectory.hashCode() : 0);
    return result;
  }

  public ActivityData getActivityData(String activityClassName) {
    return activityDatas.get(activityClassName);
  }

  public String getThemeRef() {
    return themeRef;
  }

  public Map<String, ActivityData> getActivityDatas() {
    parseAndroidManifest();
    return activityDatas;
  }

  public List<String> getUsedPermissions() {
    parseAndroidManifest();
    return usedPermissions;
  }

  private static final class MetaData {
    private final Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
    private final Map<String, VALUE_TYPE> typeMap = new LinkedHashMap<String, VALUE_TYPE>();
    private boolean initialised;

    public MetaData(List<Node> nodes) {
      for (Node metaNode : nodes) {
        NamedNodeMap attributes = metaNode.getAttributes();
        Node nameAttr = attributes.getNamedItem("android:name");
        Node valueAttr = attributes.getNamedItem("android:value");
        Node resourceAttr = attributes.getNamedItem("android:resource");

        if (valueAttr != null) {
          valueMap.put(nameAttr.getNodeValue(), valueAttr.getNodeValue());
          typeMap.put(nameAttr.getNodeValue(), VALUE_TYPE.VALUE);
        } else if (resourceAttr != null) {
          valueMap.put(nameAttr.getNodeValue(), resourceAttr.getNodeValue());
          typeMap.put(nameAttr.getNodeValue(), VALUE_TYPE.RESOURCE);
        }
      }
    }

    public void init(ResourceLoader resLoader, String packageName) {
      ResourceIndex resIndex = resLoader.getResourceIndex();

      if (!initialised) {
        for (Map.Entry<String,VALUE_TYPE> entry : typeMap.entrySet()) {
          String value = valueMap.get(entry.getKey()).toString();
          if (value.startsWith("@")) {
            ResName resName = ResName.qualifyResName(value.substring(1), packageName, null);

            switch (entry.getValue()) {
              case RESOURCE:
                // Was provided by resource attribute, store resource ID
                valueMap.put(entry.getKey(), resIndex.getResourceId(resName));
                break;
              case VALUE:
                // Was provided by value attribute, need to parse it
                TypedResource<?> typedRes = resLoader.getValue(resName, "");
                // The typed resource's data is always a String, so need to parse the value.
                switch (typedRes.getResType()) {
                  case BOOLEAN: case COLOR: case INTEGER: case FLOAT:
                    valueMap.put(entry.getKey(),parseValue(typedRes.getData().toString()));
                    break;
                  default:
                    valueMap.put(entry.getKey(),typedRes.getData());
                }
                break;
            }
          } else if (entry.getValue() == VALUE_TYPE.VALUE) {
            // Raw value, so parse it in to the appropriate type and store it
            valueMap.put(entry.getKey(), parseValue(value));
          }
        }
        // Finished parsing, mark as initialised
        initialised = true;
      }
    }

    private enum VALUE_TYPE {
      RESOURCE,
      VALUE
    }
  }

  private static class ReceiverAndIntentFilter {
    private final List<String> intentFilterActions;
    private final String broadcastReceiverClassName;
    private final MetaData metaData;

    public ReceiverAndIntentFilter(final String broadcastReceiverClassName, final List<String> intentFilterActions, final MetaData metaData) {
      this.broadcastReceiverClassName = broadcastReceiverClassName;
      this.intentFilterActions = intentFilterActions;
      this.metaData = metaData;
    }

    public String getBroadcastReceiverClassName() {
      return broadcastReceiverClassName;
    }

    public List<String> getIntentFilterActions() {
      return intentFilterActions;
    }

    public MetaData getMetaData() {
      return metaData;
    }
  }
}
