package org.robolectric.util;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import org.robolectric.res.Fs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes sqlite native libraries.
 */
public class SQLiteLibraryLoader {
  private static SQLiteLibraryLoader instance;
  private static final String SQLITE4JAVA = "sqlite4java";
  private static final String OS_WIN = "windows", OS_LINUX = "linux", OS_MAC = "mac";

  private final LibraryNameMapper libraryNameMapper;
  private boolean loaded;

  protected SQLiteLibraryLoader() {
    this(DEFAULT_MAPPER);
  }

  protected SQLiteLibraryLoader(LibraryNameMapper mapper) {
    libraryNameMapper = mapper;
  }

  private static final LibraryNameMapper DEFAULT_MAPPER = new LibraryNameMapper() {
    @Override
    public String mapLibraryName(String name) {
      return System.mapLibraryName(name);
    }
  };

  public static synchronized void load() {
    if (instance == null) {
      instance = new SQLiteLibraryLoader();
    }
    instance.doLoad();
  }

  protected void doLoad() {
    if (loaded) { return; }

    final long startTime = System.currentTimeMillis();
    final File extractedLibrary = getNativeLibraryPath();

    if (isExtractedLibUptodate(extractedLibrary)) {
      loadFromDirectory(extractedLibrary.getParentFile());
    } else {
      extractAndLoad(getLibraryStream(), extractedLibrary);
    }

    logWithTime("SQLite natives prepared in", startTime);
  }

  protected File getNativeLibraryPath() {
    String tempPath = System.getProperty("java.io.tmpdir");
    if (tempPath == null) {
      throw new IllegalStateException("Java temporary directory is not defined (java.io.tmpdir)");
    }
    return new File(Fs.fileFromPath(tempPath).join("robolectric-libs", getLibName()).getPath());
  }

  protected void mustReload() {
    loaded = false;
  }

  protected String getLibClasspathResourceName() {
    return "/" + getNativesResourcesPathPart() + "/" + getNativesResourcesFilePart();
  }

  private InputStream getLibraryStream() {
    final String classpathResourceName = getLibClasspathResourceName();
    final InputStream libraryStream = SQLiteLibraryLoader.class.getResourceAsStream(classpathResourceName);
    if (libraryStream == null) {
      throw new RuntimeException("Cannot find '" + classpathResourceName + "' in classpath");
    }
    return libraryStream;
  }

  private void logWithTime(final String message, final long startTime) {
    log(message + " " + (System.currentTimeMillis() - startTime));
  }

  private void log(final String message) {
    // System.out.println(message);
  }

  private boolean isExtractedLibUptodate(File extractedLib) {
    if (extractedLib.exists()) {
      try {
        String existingMd5 = md5sum(new FileInputStream(extractedLib));
        String actualMd5 = md5sum(getLibraryStream());
        return existingMd5.equals(actualMd5);
      } catch (IOException e) {
        return false;
      }
    } else {
      return false;
    }
  }

  private void extractAndLoad(final InputStream input, final File output) {
    File libPath = output.getParentFile();
    if (!libPath.exists() && !libPath.mkdirs()) {
      throw new RuntimeException("could not create " + libPath);
    }

    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(output);
      copy(input, outputStream);
    } catch (IOException e) {
      throw new RuntimeException("Cannot extractAndLoad SQLite library into " + output, e);
    } finally {
      closeQuietly(outputStream);
      closeQuietly(input);
    }

    loadFromDirectory(libPath);
  }

  private void loadFromDirectory(final File libPath) {
    // configure less verbose logging
    Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.WARNING);

    SQLite.setLibraryPath(libPath.getAbsolutePath());
    try {
      log("SQLite version: library " + SQLite.getLibraryVersion() + " / core " + SQLite.getSQLiteVersion());
    } catch (SQLiteException e) {
      throw new RuntimeException(e);
    }
    loaded = true;
  }

  private String getLibName() {
    return libraryNameMapper.mapLibraryName(SQLITE4JAVA);
  }

  private String getNativesResourcesPathPart() {
    return getOsPrefix() + "-" + getArchitectureSuffix();
  }

  private String getNativesResourcesFilePart() {
    return getLibName().replace(".dylib", ".jnilib");
  }

  private String getOsPrefix() {
    String name = System.getProperty("os.name").toLowerCase(Locale.US);
    if (name.contains("win")) {
      return OS_WIN;
    } else if (name.contains("linux")) {
      return OS_LINUX;
    } else if (name.contains("mac")) {
      return OS_MAC;
    } else {
      throw new UnsupportedOperationException("Architecture '" + name + "' is not supported by SQLite library");
    }
  }

  private String getArchitectureSuffix() {
    String arch = System.getProperty("os.arch").toLowerCase(Locale.US).replaceAll("\\W", "");
    if ("i386".equals(arch) || "x86".equals(arch)) {
      return "x86";
    } else {
      return "x86_64";
    }
  }

  private String md5sum(InputStream input) throws IOException {
    BufferedInputStream in = new BufferedInputStream(input);

    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      DigestInputStream digestInputStream = new DigestInputStream(in, digest);
      while (digestInputStream.read() >= 0) ;
      ByteArrayOutputStream md5out = new ByteArrayOutputStream();
      md5out.write(digest.digest());
      return new BigInteger(md5out.toByteArray()).toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("MD5 algorithm is not available: " + e);
    }
    finally {
      in.close();
    }
  }

  protected static void copy(final InputStream input, final OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int n;
    while ((n = input.read(buffer)) != -1) {
      output.write(buffer, 0, n);
    }
  }

  private static void closeQuietly(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }

  protected interface LibraryNameMapper {
    String mapLibraryName(String name);
  }
}
