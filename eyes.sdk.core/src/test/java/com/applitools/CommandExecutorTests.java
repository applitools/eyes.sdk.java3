package com.applitools;

import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.universal.CommandExecutor;
import com.applitools.universal.ManagerType;
import com.applitools.universal.Reference;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kanan
 */
public class CommandExecutorTests {

  @Test
  public void test() {
    CommandExecutor commandExecutor = new CommandExecutor("sdk_name", "sdk_version");
    Reference reference = commandExecutor.coreMakeManager(ManagerType.CLASSIC.value, null, null);
    System.out.println("applitools ref id: " + reference.getApplitoolsRefId());
    System.out.println("reference: " + reference);
    Assert.assertNotNull(reference);
  }


  @Test
  public void test1() {
    ClassicRunner classicRunner = new ClassicRunner();
    Reference reference = classicRunner.getManagerRef();
    System.out.println("reference: " + reference);
    Assert.assertNotNull(reference.getApplitoolsRefId());
  }

  public void test2() {
  }

}
