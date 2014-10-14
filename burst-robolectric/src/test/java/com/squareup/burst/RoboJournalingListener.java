package com.squareup.burst;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.List;

final class RoboJournalingListener {
  private final RunNotifier notifier = new RunNotifier();
  private final List<String> journal = new ArrayList<>();

  RoboJournalingListener() {
    notifier.addListener(new RunListener() {
      @Override public void testRunStarted(Description description) throws Exception {
        throw new AssertionError();
      }

      @Override public void testRunFinished(Result result) throws Exception {
        throw new AssertionError();
      }

      @Override public void testStarted(Description description) throws Exception {
        journal.add("START " + description.getDisplayName());
      }

      @Override public void testFinished(Description description) throws Exception {
        journal.add("FINISH " + description.getDisplayName());
      }

      @Override public void testFailure(Failure failure) throws Exception {
        journal.add(
            "FAIL " + failure.getDescription().getDisplayName() + " " + failure.getMessage());
      }

      @Override public void testAssumptionFailure(Failure failure) {
        journal.add("ASSUMPTION FAIL "
            + failure.getDescription().getDisplayName()
            + " "
            + failure.getMessage());
      }

      @Override public void testIgnored(Description description) throws Exception {
        journal.add("IGNORE " + description.getDisplayName());
      }
    });
  }

  RunNotifier notifier() {
    return notifier;
  }

  List<String> journal() {
    return new ArrayList<>(journal);
  }
}
