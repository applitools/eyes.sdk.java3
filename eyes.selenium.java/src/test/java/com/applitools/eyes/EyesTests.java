package com.applitools.eyes;

import java.util.Arrays;
import java.util.List;

import com.applitools.eyes.locators.TextRegionSettings;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.selenium.CommandExecutor;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.Reference;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.ContextReferenceDto;
import com.applitools.eyes.selenium.universal.dto.FrameLocatorDto;
import com.applitools.eyes.selenium.universal.dto.FrameNameOrId;
import com.applitools.eyes.selenium.universal.dto.FrameSelector;
import com.applitools.eyes.selenium.universal.dto.IFrame;
import com.applitools.eyes.selenium.universal.dto.ResponseDto;
import com.applitools.eyes.selenium.universal.dto.ResponsePayload;
import com.applitools.eyes.selenium.universal.dto.SelectorRegionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * eyes tests
 */
public class EyesTests {

  @Test
  public void should_OpenEyes_When_ChromeDriver() {
    Configuration configuration = new Configuration();
    configuration.setAppName("app name");
    configuration.setTestName("test name");
    configuration.setBranchName("branch name");
    configuration.setParentBranchName("parent branch name");
    configuration.setAgentId("agentId");
    configuration.setEnvironmentName("environment name");
    configuration.setSaveDiffs(false);
    configuration.setSessionType(SessionType.SEQUENTIAL);
    BatchInfo batchInfo = new BatchInfo();
    batchInfo.setId("batchId");
    batchInfo.setCompleted(false);
    batchInfo.setNotifyOnCompletion(true);
    batchInfo.setSequenceName("sequence name");
    batchInfo.setStartedAt(null);
    configuration.setBatch(batchInfo);
    configuration.setBaselineEnvName("baseline env name");
    RectangleSize rectangleSize = new RectangleSize(500, 500);

    //configuration.setViewportSize(rectangleSize);
    //configuration.setIgnoreDisplacements(false);
//    ImageMatchSettings imageMatchSettings = new ImageMatchSettings();
//    imageMatchSettings.setEnablePatterns(false);
//    imageMatchSettings.setIgnoreCaret(false);
//    imageMatchSettings.setMatchLevel(MatchLevel.EXACT);
//    configuration.setDefaultMatchSettings(imageMatchSettings);
    //configuration.setBrowsersInfo(null);
    Eyes eyes = new Eyes();
    eyes.setConfiguration(configuration);
    eyes.open(new ChromeDriver());
    Reference eyesRef = eyes.getEyesRef();
    Assert.assertNotNull(eyesRef);
  }

  @Test
  public void should_OpenEyes_When_Configured() {
    Eyes eyes = new Eyes();
    eyes.open(new ChromeDriver(), "app name", "test name");
    Reference eyesRef = eyes.getEyesRef();
    Assert.assertNotNull(eyesRef);
    Assert.assertEquals("app name", eyes.getConfiguration().getAppName());
    Assert.assertEquals("test name", eyes.getConfiguration().getTestName());
  }

  @Test
  public void should_OpenAndCheckEyes_When_Configured() {
    WebDriver webDriver = new ChromeDriver();
    webDriver.get("http://applitools.github.io/demo/TestPages/FramesTestPage/");
    Eyes eyes = new Eyes();
    eyes.open(webDriver, "app name", "test name", new RectangleSize(800, 600));
    Reference eyesRef = eyes.getEyesRef();
    eyes.check("Check region ignore region", Target.region(By.cssSelector("overflowing-div")).ignore(new Region(50, 110, 90, 550)));
  }


  @Test
  public void should_OpenEyes_When_ConfiguredLocate() {
    Eyes eyes = new Eyes();
    eyes.open(new ChromeDriver(), "app name", "test name");
    Reference eyesRef = eyes.getEyesRef();
    eyes.locate(new VisualLocatorSettings().name("applitools_title"));
    Assert.assertNotNull(eyesRef);
    Assert.assertEquals("app name", eyes.getConfiguration().getAppName());
    Assert.assertEquals("test name", eyes.getConfiguration().getTestName());
  }

  @Test
  public void should_OpenEyes_When_ConfiguredExtractTextRegions() {
    Eyes eyes = new Eyes();
    eyes.open(new ChromeDriver(), "app name", "test name");
    Reference eyesRef = eyes.getEyesRef();
    eyes.extractTextRegions(new TextRegionSettings("header d: Hello world", "d..+", "make").ignoreCase(true));
  }

  @Test
  public void test() {
    ResponseDto<List<String>> response = new ResponseDto<>();
    response.setName("Eyes.extractText");
    response.setKey("6f02c78c-8056-11ec-a51b-a4c3f0e5bafa");
    ResponsePayload<List<String>> responsePayload = new ResponsePayload<>();
//    Map<String, List<TextRegion>> map = new HashMap<>();
//    map.put("header", Arrays.asList(new TextRegion(1,1,1,1, "text")));
    //dto.setResult(map);
    responsePayload.setResult(Arrays.asList("test", "test1", "test2", "test3"));
    response.setPayload(responsePayload);

    Gson gson = new Gson();
    System.out.println(gson.toJson(response));

  }


  @Test
  public void testCheck() throws JsonProcessingException {
    CheckSettingsDto checkSettingsDto = new CheckSettingsDto();
    checkSettingsDto.setName(null);
    checkSettingsDto.setLayoutRegions(null);
    ContextReferenceDto contextReferenceDto = new ContextReferenceDto();
    contextReferenceDto.setScrollRootElement(null);
    FrameLocatorDto frameLocatorDto = new FrameLocatorDto();
    //frameLocatorDto.setFrameIndex(1);
    frameLocatorDto.setFrameNameOrId("id");
    //frameLocatorDto.setElement(null);
    SelectorRegionDto ss = new SelectorRegionDto();
    ss.setType("type");
    ss.setSelector("selector");
    FrameSelector frameSelector = new FrameSelector();
    frameSelector.setSelector(ss);
    frameLocatorDto.setSelector(ss);

    contextReferenceDto.setFrame("frame1");
    checkSettingsDto.setFrames(Arrays.asList(contextReferenceDto));

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println("request: " + objectMapper.writeValueAsString(checkSettingsDto));


  }


}
