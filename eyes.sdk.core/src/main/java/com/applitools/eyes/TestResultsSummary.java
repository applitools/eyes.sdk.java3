package com.applitools.eyes;

import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.util.Iterator;
import java.util.List;

public class TestResultsSummary implements Iterable<TestResultContainer>{
    private List<TestResultContainer> results;
    private Integer passed;
    private Integer unresolved;
    private Integer failed;
    private Integer exceptions;
    private Integer mismatches;
    private Integer missing;
    private Integer matches;

    public TestResultsSummary(List<TestResultContainer> allResults) {
        this.results = allResults;
        for (TestResultContainer resultContainer : allResults) {
            TestResults result = null;
            if (resultContainer != null) {
                if (resultContainer.getException() != null) {
                    this.exceptions = 1;
                }
                result = resultContainer.getTestResults();
            }
            if (result == null) {
                continue;
            }
            if (result.getStatus() != null) {
                switch (result.getStatus()) {
                    case Failed:
                        this.failed = 1;
                        break;
                    case Passed:
                        this.passed = 1;
                        break;
                    case Unresolved:
                        this.unresolved = 1;
                        break;
                }
            }
            matches = result.getMatches();
            missing = result.getMissing();
            mismatches = result.getMismatches();
        }
    }

    public TestResultsSummary(List<TestResultContainer> allResults, Integer passed, Integer unresolved,
        Integer failed, Integer exceptions, Integer mismatches, Integer missing, Integer matches) {
        this.results = allResults;
        this.passed = passed;
        this.unresolved = unresolved;
        this.failed = failed;
        this.exceptions = exceptions;
        this.mismatches = mismatches;
        this.missing = missing;
        this.matches = matches;
    }

    public TestResultContainer[] getAllResults() {
        return results.toArray(new TestResultContainer[0]);
    }

    @Override
    public String toString() {
        return "result summary {" +
                "\n\tall results=\n\t\t" + StringUtils.join(results,"\n\t\t") +
                "\n\tpassed=" + passed +
                "\n\tunresolved=" + unresolved +
                "\n\tfailed=" + failed +
                "\n\texceptions=" + exceptions +
                "\n\tmismatches=" + mismatches +
                "\n\tmissing=" + missing +
                "\n\tmatches=" + matches +
                "\n}";
    }

    @Override
    public Iterator<TestResultContainer> iterator() {
        return this.results.iterator();
    }

    public int size() {
        return this.results.size();
    }

}
