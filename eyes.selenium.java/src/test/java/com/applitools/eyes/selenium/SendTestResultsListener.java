package com.applitools.eyes.selenium;
import com.google.gson.JsonObject;
import org.testng.*;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SendTestResultsListener implements ISuiteListener {

    Set<ITestResult> allTestResults = null;
    private static Retrofit retrofit = null;

    @Override
    public void onStart(ISuite iSuite) {

    }

    @Override
    public void onFinish(ISuite iSuite) {
        allTestResults = new HashSet<>();
        collectTestResultsIntoSet(iSuite);
        JsonObject resultJson = getResultJsonAsString();
        ApiService service = getClient("http://applitools-sdk-test-results.herokuapp.com").create(ApiService.class);
        try {
            service.sendResults(resultJson).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void collectTestResultsIntoSet(ISuite iSuite){
        Map<String, ISuiteResult> suiteResultMap = iSuite.getResults();
        Iterator<ISuiteResult> suiteResultIterator = suiteResultMap.values().iterator();
        while (suiteResultIterator.hasNext()) {
            ITestContext testContext = suiteResultIterator.next().getTestContext();
            allTestResults.addAll(testContext.getPassedTests().getAllResults());
            allTestResults.addAll(testContext.getFailedButWithinSuccessPercentageTests().getAllResults());
            allTestResults.addAll(testContext.getFailedConfigurations().getAllResults());
            allTestResults.addAll(testContext.getFailedTests().getAllResults());
            allTestResults.addAll(testContext.getSkippedConfigurations().getAllResults());
            allTestResults.addAll(testContext.getSkippedTests().getAllResults());
        }
    }

    private JsonObject getResultJsonAsString(){
        Iterator<ITestResult> resultsIterator = allTestResults.iterator();
        JsonObject finalJsonObject = new JsonObject();
        JsonObject innerResultsJsonObject = new JsonObject();
        while (resultsIterator.hasNext()){
            ITestResult testResult = resultsIterator.next();
            String methodName = testResult.getMethod().getMethodName();
            boolean isPassed = testResult.isSuccess();
            innerResultsJsonObject.addProperty(methodName, isPassed);
        }
        finalJsonObject.addProperty("sdk", "java");
        finalJsonObject.add("results", innerResultsJsonObject);
        return finalJsonObject;
    }

    private static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    interface ApiService {

        @POST("/result")
        Call<Void> sendResults(@Body JsonObject body);

    }
}
