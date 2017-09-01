/*
 * @(#)TestMonitorRunnable.java
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

/**
 * A helper class to more easily create monitors.  TestRunnable monitors
 * do not have to extend this class, but it helps in becoming more
 * conformant to the requirements of the superclass.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/09/29 21:09:40 $
 * @since July 12, 2003
 */
public abstract class TestMonitorRunnable extends TestRunnable {
    public TestMonitorRunnable() {
        super(true);
    }


    /**
     * Performs checks on the monitored object which is being subjected
     * to parallel processing.  This method should not perform looping
     * over the check(s), since the <tt>runTest()</tt> method will
     * perform these.
     *
     * @throws Throwable any exception may be thrown and will be
     *                   reported as a test failure, except for
     *                   <tt>InterruptedException</tt>s, which will be ignored.
     */
    public abstract void runMonitor() throws Throwable;


    /**
     * Performs all the necessary looping, end-of-threads, and interrupt
     * checking.  The inner loop calls the <tt>runMonitor()</tt>
     * method.
     */
    public void runTest() throws Throwable {
        while (!isDone() && !Thread.interrupted()) {
            runMonitor();
            yieldProcessing();
        }

        // perform one last pass, to ensure it's still valid
        runMonitor();
    }


    /**
     * Instructs the thread to pause for a while.  This method is called
     * by the <tt>runTest()</tt> method's loop, immediately after
     * each <tt>runMonitor()</tt> invocation.  The default implementation
     * performs a <tt>Thread.yield()</tt> call, but by putting it into
     * this method, that behavior can be modified.
     *
     * @throws InterruptedException allows for overloading methods to
     *                              perform a <tt>delay( long )</tt> call within their
     *                              implementation.
     */
    protected void yieldProcessing() throws InterruptedException {
        Thread.yield();
    }
}

