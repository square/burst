package org.robolectric.shadows;

import android.content.IntentFilter;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(TestRunners.WithDefaults.class)
public class IntentFilterTest {
  @Test
  public void addDataScheme_shouldAddTheDataScheme() throws Exception {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataScheme("http");
    intentFilter.addDataScheme("ftp");

    assertThat(intentFilter.getDataScheme(0)).isEqualTo("http");
    assertThat(intentFilter.getDataScheme(1)).isEqualTo("ftp");
  }

  @Test
  public void addDataAuthority_shouldAddTheDataAuthority() throws Exception {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataAuthority("test.com", "8080");
    intentFilter.addDataAuthority("example.com", "42");

    assertThat(intentFilter.getDataAuthority(0).getHost()).isEqualTo("test.com");
    assertThat(intentFilter.getDataAuthority(0).getPort()).isEqualTo(8080);
    assertThat(intentFilter.getDataAuthority(1).getHost()).isEqualTo("example.com");
    assertThat(intentFilter.getDataAuthority(1).getPort()).isEqualTo(42);
  }

  @Test
  public void addDataType_shouldAddTheDataType() throws Exception {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataType("image/test");

    assertThat(intentFilter.getDataType(0)).isEqualTo("image/test");
  }

  @Test
  public void hasAction() {
    IntentFilter intentFilter = new IntentFilter();
    assertThat(intentFilter.hasAction("test")).isFalse();
    intentFilter.addAction("test");

    assertThat(intentFilter.hasAction("test")).isTrue();
  }
  
  @Test
  public void hasDataScheme() {
    IntentFilter intentFilter = new IntentFilter();
    assertThat(intentFilter.hasDataScheme("test")).isFalse();
    intentFilter.addDataScheme("test");
  
    assertThat(intentFilter.hasDataScheme("test")).isTrue();
  }

  @Test
  public void hasDataType() throws IntentFilter.MalformedMimeTypeException{
    IntentFilter intentFilter = new IntentFilter();
    assertThat(intentFilter.hasDataType("image/test")).isFalse();
    intentFilter.addDataType("image/test");

    assertThat(intentFilter.hasDataType("image/test")).isTrue();
  }

  @Test
  public void matchDataAuthority_matchHostAndPort() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataAuthority("testHost1", "1");
    intentFilter.addDataAuthority("testHost2", "2");

    Uri uriTest1 = Uri.parse("http://testHost1:1");
    Uri uriTest2 = Uri.parse("http://testHost2:2");
    assertThat(intentFilter.matchDataAuthority(uriTest1)).isEqualTo(
        IntentFilter.MATCH_CATEGORY_HOST + IntentFilter.MATCH_CATEGORY_PORT + IntentFilter.MATCH_ADJUSTMENT_NORMAL);
    assertThat(intentFilter.matchDataAuthority(uriTest1)).isEqualTo(
        IntentFilter.MATCH_CATEGORY_HOST + IntentFilter.MATCH_CATEGORY_PORT + IntentFilter.MATCH_ADJUSTMENT_NORMAL);
  }

  @Test
  public void matchDataAuthority_matchHostWithNoPort() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataAuthority("testHost1", "-1");
    intentFilter.addDataAuthority("testHost2", "-1");

    Uri uriTest1 = Uri.parse("http://testHost1:100");
    Uri uriTest2 = Uri.parse("http://testHost2:200");
    assertThat(intentFilter.matchDataAuthority(uriTest1)).isEqualTo(
        IntentFilter.MATCH_CATEGORY_HOST + IntentFilter.MATCH_ADJUSTMENT_NORMAL);
    assertThat(intentFilter.matchDataAuthority(uriTest2)).isEqualTo(
        IntentFilter.MATCH_CATEGORY_HOST + IntentFilter.MATCH_ADJUSTMENT_NORMAL);
  }

  @Test
  public void matchDataAuthority_NoMatch() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataAuthority("testHost1", "1");
    intentFilter.addDataAuthority("testHost2", "2");

    // Port doesn't match
    Uri uriTest1 = Uri.parse("http://testHost1:2");
    // Host doesn't match
    Uri uriTest2 = Uri.parse("http://testHost3:2");
    assertThat(intentFilter.matchDataAuthority(uriTest1)).isEqualTo(
        IntentFilter.NO_MATCH_DATA);
    assertThat(intentFilter.matchDataAuthority(uriTest2)).isEqualTo(
        IntentFilter.NO_MATCH_DATA);
  }

  @Test
  public void matchData_MatchAll() throws IntentFilter.MalformedMimeTypeException{
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataType("image/test");
    intentFilter.addDataScheme("http");
    intentFilter.addDataAuthority("testHost1", "1");

    Uri uriTest1 = Uri.parse("http://testHost1:1");
    assertThat(intentFilter.matchData("image/test", "http", uriTest1))
        .isGreaterThanOrEqualTo(0);
  }

  @Test
  public void matchData_MatchType() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataType("image/test");

    Uri uriTest1 = Uri.parse("http://testHost1:1");
    assertThat(intentFilter.matchData("image/test", "http", uriTest1))
        .isGreaterThanOrEqualTo(0);
  }

  @Test
  public void matchData_MatchScheme() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataScheme("http");

    Uri uriTest1 = Uri.parse("http://testHost1:1");
    assertThat(intentFilter.matchData(null, "http", uriTest1))
        .isGreaterThanOrEqualTo(0);
  }

  @Test
  public void matchData_MatchEmpty() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();

    assertThat(intentFilter.matchData(null, "noscheme", null))
        .isGreaterThanOrEqualTo(0);
  }

  @Test
  public void matchData_NoMatchType() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataType("image/testFail");

    Uri uriTest1 = Uri.parse("http://testHost1:1");
    assertThat(intentFilter.matchData("image/test", "http", uriTest1))
        .isLessThan(0);
  }

  @Test
  public void matchData_NoMatchScheme() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataScheme("http");
    intentFilter.addDataType("image/test");

    Uri uriTest1 = Uri.parse("https://testHost1:1");
    assertThat(intentFilter.matchData("image/test", "https", uriTest1))
        .isLessThan(0);
  }

  @Test
  public void matchData_NoMatchDataAuthority() throws IntentFilter.MalformedMimeTypeException {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addDataType("image/test");
    intentFilter.addDataScheme("http");
    intentFilter.addDataAuthority("testHost1", "1");

    Uri uriTest1 = Uri.parse("http://testHost1:2");
    assertThat(intentFilter.matchData("image/test", "http", uriTest1))
        .isLessThan(0);
  }
}
