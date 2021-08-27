package org.redhat.authz.opa;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public class Result {

    private boolean allow;

    private CheckResult checkResult;

    public CheckResult getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(CheckResult checkResult) {
        this.checkResult = checkResult;
    }

    @Override
    public String toString() {
        return "Result{" +
                "allow=" + allow +
                ", checkResult=" + checkResult +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return allow == result.allow && Objects.equals(checkResult, result.checkResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allow, checkResult);
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public class CheckResult {
        private String explanation;
        private boolean result;

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CheckResult that = (CheckResult) o;
            return Objects.equals(explanation, that.explanation) && Objects.equals(result, that.result);
        }

        @Override
        public int hashCode() {
            return Objects.hash(explanation, result);
        }

        @Override
        public String toString() {
            return "CheckResult{" +
                    "explanation='" + explanation + '\'' +
                    ", result='" + result + '\'' +
                    '}';
        }
    }
}
