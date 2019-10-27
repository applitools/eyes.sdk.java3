package com.applitools.eyes;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

public class TestResultsSummary implements Iterable<TestResultContainer>{
    private List<TestResultContainer> allResults;
    private int passed = 0;
    private int unresolved = 0;
    private int failed = 0;
    private int exceptions = 0;
    private int mismatches = 0;
    private int missing = 0;
    private int matches = 0;

    public TestResultsSummary(List<TestResultContainer> allResults) {
        this.allResults = allResults;
        for (TestResultContainer resultContainer : allResults) {
            if (resultContainer != null && resultContainer.getException() != null){
                this.exceptions++;
            }
            TestResults result = resultContainer.getTestResults();
            if (result == null) continue;
            if (result.getStatus() != null) {
                switch (result.getStatus()) {
                    case Failed:
                        this.failed++;
                        break;
                    case Passed:
                        this.passed++;
                        break;
                    case Unresolved:
                        this.unresolved++;
                        break;
                }
            }
            matches += result.getMatches();
            missing += result.getMissing();
            mismatches += result.getMismatches();
        }
    }

    public TestResultContainer[] getAllResults() {
        return allResults.toArray(new TestResultContainer[0]);
    }

    @Override
    public String toString() {
        return "result summary {" +
                "\n\tall results=\n\t\t" + StringUtils.join(allResults,"\n\t\t") +
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
        return this.allResults.iterator();
    }

    public int size() {
        return this.allResults.size();
    }
}
