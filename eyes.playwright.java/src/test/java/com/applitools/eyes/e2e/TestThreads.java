package com.applitools.eyes.e2e;

import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.playwright.ClassicRunner;
import com.applitools.eyes.playwright.Eyes;
import com.applitools.eyes.playwright.fluent.Target;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestThreads {

    @Test
    public void testWithThreads() {

        List<Thread> threads = new ArrayList<>();
        List<String> urls = new ArrayList<String>(){{
            add("https://applitools.com");
            add("https://demo.applitools.com");
            add("https://demo.applitools.com/app.html");
            add("https://applitools.com/platform/eyes/");
            add("https://applitools.com/platform/ultrafast-grid/");
            add("https://applitools.com/platform/native-mobile-grid/");
            add("https://applitools.com/platform/integrations/");
        }};

        for (int i = 0; i < urls.size(); i++) {
            int runnerIndex = i;
            threads.add(new Thread(() -> {
                String url = urls.get(runnerIndex);
                runEyesTest(url);
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void runEyesTest(String url) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();
        page.navigate(url);

        ClassicRunner runner = new ClassicRunner();

        Eyes eyes = new Eyes(runner);
        eyes.open(page, "Playwright SDK - Threads", "Test Threads - " + url);
        eyes.check(Target.window());
        eyes.close(false);

        TestResultsSummary results = runner.getAllTestResults();
        System.out.println(Arrays.toString(results.getAllResults()));
    }
}
