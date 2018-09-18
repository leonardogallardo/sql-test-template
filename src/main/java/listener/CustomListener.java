package listener;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class CustomListener extends RunListener {

    @Override
    public void testRunStarted(Description description) {
        // Do nothing
    }

    @Override
    public void testRunFinished(Result result) {
        // Do nothing
    }

    @Override
    public void testStarted(Description description) {
        // Do nothing
    }

    @Override
    public void testFinished(Description description) {
        // Do nothing
    }

    @Override
    public void testFailure(Failure failure) {
        // Do nothing
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        // Do nothing
    }

    @Override
    public void testIgnored(Description description) {
        // Do nothing
    }

}
