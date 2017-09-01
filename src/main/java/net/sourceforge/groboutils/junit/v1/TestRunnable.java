/*
 * @(#)TestRunnable.java
 *
 * The basics are taken from an article by Andy Schneider
 * andrew.schneider@javaworld.com
 * The article is "JUnit Best Practices"
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1221-junit_p.html
 *
 * Part of the GroboUtils package at:
 * http://groboutils.sourceforge.net
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 *  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 */

package net.sourceforge.groboutils.junit.v1;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Instances of this class only execute in the
 * <tt>runTestRunnables</tt> method of
 * the <tt>MultiThreadedTestRunner</tt> class.
 * TestCases should define inner classes as a subclass of this,
 * implement the <tt>runTest()</tt> method, and pass in the
 * instantiated class as part of an array to the
 * <tt>runTestRunnables</tt> method.  Call <tt>delay( long )</tt>
 * to easily include a waiting period.  This class allows for
 * all assertions to be invoked, so that subclasses can be static or
 * defined outside a TestCase.  If an exception is thrown from the
 * <tt>runTest()</tt> method, then all other test threads will
 * terminate due to the error.
 * <p>
 * The <tt>runTest()</tt> method needs to be responsive to
 * <tt>InterruptedException</tt>, resulting from the owning
 * <tt>MultiThreadedTestRunner</tt> interrupting the thread in order to
 * signal the early termination of the threads.  The
 * <tt>InterruptedException</tt>s may be propigated outside the
 * <tt>runTest()</tt> implementation with no harmful effects.  Note that
 * this means that <tt>InterruptedException</tt>s are part of the
 * framework, and as such carry information that your <tt>runTest()</tt>
 * implementations cannot override; in other words, don't let your test
 * propigate an <tt>InterruptedException</tt> to indicate an error.
 * <p>
 * Tests which perform a set of monitoring checks on the object-under-test
 * should extend <tt>TestMonitorRunnable</tt>, since monitors run until
 * told to stop.  The <tt>Thread.stop()</tt> command will be sent with a
 * <tt>MultiThreadedTestRunner.TestDeathException</tt>.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/10/03 14:26:45 $
 * @since March 28, 2002
 */
public abstract class TestRunnable extends Assert
        implements Runnable {
    private static final Class THIS_CLASS = TestRunnable.class;
    protected static final Logger LOG = LoggerFactory.getLogger(THIS_CLASS);
    private static int testCount = 0;

    private MultiThreadedTestRunner mttr;
    private int testIndex;
    private boolean ignoreStopErrors = false;


    public TestRunnable() {
        synchronized (THIS_CLASS) {
            this.testIndex = testCount++;
        }
    }


    TestRunnable(boolean ignoreStopErrors) {
        this();
        this.ignoreStopErrors = ignoreStopErrors;
    }


    /**
     * Performs the set of processing or checks on the object-under-test,
     * which will be in parallel with other <tt>TestRunnable</tt>
     * instances.
     * <p>
     * The implementation should be responsive to
     * <tt>InterruptedException</tt> exceptions or to the status of
     * <tt>Thread.isInterrupted()</tt>, as that is the signal used to tell
     * running <tt>TestRunnable</tt> instances to halt their processing;
     * instances which do not stop within a reasonable time frame will
     * have <tt>Thread.stop()</tt> called on them.
     * <p>
     * Non-monitor instances must have this method implemented such that
     * it runs in a finite time; if any instance executes over the
     * <tt>MultiThreadedTestRunner</tt> instance maximum time limit, then
     * the <tt>MultiThreadedTestRunner</tt> instance assumes that a
     * test error occurred.
     *
     * @throws Throwable any exception may be thrown and will be
     *                   reported as a test failure, except for
     *                   <tt>InterruptedException</tt>s, which will be ignored.
     */
    public abstract void runTest() throws Throwable;

    /**
     * Sleep for <tt>millis</tt> milliseconds.  A convenience method.
     *
     * @throws InterruptedException if an interrupt occured during the
     *                              8      sleep.
     */
    public void delay(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    /**
     * Unable to make this a "final" method due to JDK 1.1 compatibility.
     * However, implementations should not override this method.
     */
    public void run() {
        if (this.mttr == null) {
            throw new IllegalStateException(
                    "Owning runner never defined.  The runnables should only be " +
                            "started through the MultiThreadedTestRunner instance.");
        }

        LOG.info("Starting test thread " + this.testIndex);
        try {
            runTest();
        } catch (InterruptedException ie) {
            // ignore these exceptions - they represent the MTTR
            // interrupting the tests.
        } catch (MultiThreadedTestRunner.TestDeathException tde) {
            // ignore these exceptions as they relate to thread-related
            // exceptions.  These represent the MTTR stopping us.
            // Our response is to actually rethrow the exception.
            if (!this.ignoreStopErrors) {
                LOG.info("Aborted test thread " + this.testIndex);
                throw tde;
            }
        } catch (Throwable t) {
            // for any exception, handle it and interrupt the
            // other threads

            // Note that ThreadDeath exceptions must be re-thrown after
            // the interruption has occured.
            this.mttr.handleException(t);
        }
        LOG.info("Ended test thread " + this.testIndex);
    }


    /**
     * Returns the status of the owning <tt>MultiThreadedTestRunner</tt>
     * instance: <tt>true</tt> means that the tests have completed (monitors
     * may still be active), and <tt>false</tt> means that the tests are
     * still running.
     *
     * @return <tt>true</tt> if the tests have completed their run,
     * otherwise <tt>false</tt>.
     */
    public boolean isDone() {
        return this.mttr.areThreadsFinished();
    }


    void setTestRunner(MultiThreadedTestRunner mttr) {
        this.mttr = mttr;
    }
}

