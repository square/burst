package com.squareup.burst;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;

import static org.junit.Assert.assertEquals;

public class ParentRunnerSpyTest {

  @Test
  public void testGetFilteredChildren() throws Exception {
    List<String> children =
        ParentRunnerSpy.getFilteredChildren(new ParentRunner<String>(ParentRunnerSpyTest.class) {
          @Override protected List<String> getChildren() {
            ArrayList<String> children = new ArrayList<>();
            children.add("children");
            return children;
          }

          @Override protected Description describeChild(String o) {
            return null;
          }

          @Override protected void runChild(String o, RunNotifier runNotifier) {}
        });

    assertEquals(1, children.size());
    assertEquals("children", children.get(0));
  }
}
