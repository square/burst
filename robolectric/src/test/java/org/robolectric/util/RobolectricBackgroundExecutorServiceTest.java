package org.robolectric.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

@RunWith(TestRunners.WithDefaults.class)
public class RobolectricBackgroundExecutorServiceTest {
  private Transcript transcript;
  private RobolectricBackgroundExecutorService executorService;
  private Runnable runnable;

  @Before public void setUp() throws Exception {
    transcript = new Transcript();
    executorService = new RobolectricBackgroundExecutorService();

    Robolectric.getBackgroundScheduler().pause();

    runnable = new Runnable() {
      @Override public void run() {
        transcript.add("background event ran");
      }
    };
  }

  @Test
  public void execute_shouldRunStuffOnBackgroundThread() throws Exception {
    executorService.execute(runnable);

    transcript.assertNoEventsSoFar();

    Robolectric.runBackgroundTasks();
    transcript.assertEventsSoFar("background event ran");
  }

  @Test
  public void submitRunnable_shouldRunStuffOnBackgroundThread() throws Exception {
    Future<String> future = executorService.submit(runnable, "foo");

    transcript.assertNoEventsSoFar();
    assertFalse(future.isDone());

    Robolectric.runBackgroundTasks();
    transcript.assertEventsSoFar("background event ran");
    assertTrue(future.isDone());

    assertEquals("foo", future.get());
  }

  @Test
  public void submitCallable_shouldRunStuffOnBackgroundThread() throws Exception {
    Future<String> future = executorService.submit(new Callable<String>() {
      @Override public String call() throws Exception {
        runnable.run();
        return "foo";
      }
    });

    transcript.assertNoEventsSoFar();
    assertFalse(future.isDone());

    Robolectric.runBackgroundTasks();
    transcript.assertEventsSoFar("background event ran");
    assertTrue(future.isDone());

    assertEquals("foo", future.get());
  }
}
