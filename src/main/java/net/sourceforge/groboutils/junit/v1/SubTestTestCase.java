/*
 * @(#)SubTestTestCase.java
 *
 * Copyright (C) 2002-2003 Matt Albrecht
 * groboclown@users.sourceforge.net
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * A TestCase which enables tests to run a subset of tests from an active
 * test.  Examples would include running unit tests associated with an
 * object returned from a creation method called on the class under test.
 * <p>
 * Note that added sub-tests should be new Test instances, not the same test.
 * This is because these sub-tests will run after the registered instance
 * has <tt>tearDown()</tt> called on it.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:19 $
 * @since July 26, 2002
 */
public class SubTestTestCase extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(SubTestTestCase.class);

    private Hashtable perThreadTestList = new Hashtable();


    /**
     * Creates a new test case with the given name.  This allows for JUnit
     * 3.7 compatibility.
     */
    public SubTestTestCase(String name) {
        super(name);
    }


    /**
     * Default constructor that uses the JUnit 3.8 version of <tt>setName()</tt>
     * for setting the test's name.
     *
     * @since December 28, 2002
     */
    public SubTestTestCase() {
        // do nothing
    }


    /**
     * Allows for execution of the given test instance from inside another
     * test.  This will use the current test's TestResult, or if there
     * is no current <tt>TestResult</tt>, it will create a new one for the
     * test. Note that the list of tests will be run at the end of the current
     * test, after the <tt>tearDown()</tt> method has been called.  This is for
     * legacy TestListener support.
     *
     * @param test the test object to add as a sub-test.
     */
    public void addSubTest(Test test) {
        // Since the vector will be pulled from a hashtable that stores the
        // vectors on a per thread basis, there is no chance that the same
        // vector will be requested from two different threads at the same time.
        // Hence, no need for synchronization.
        if (test != null) {
            Thread t = Thread.currentThread();
            Vector v = (Vector) this.perThreadTestList.get(t);
            if (v != null) {
                LOG.debug("Adding test [" + test + "] to test [" + getName() +
                        "]");
                v.addElement(test);
            } else {
                LOG.warn("Attemted to add test [" + test + "] to test [" +
                        getName() + "] without calling the run() method.");
            }
        } else {
            LOG.warn("Attempted to add null test to test [" + getName() + "]");
        }
    }


    /**
     * Runs the test case and collects the results in TestResult.
     */
    public void run(TestResult result) {
        // Note: it is 'bad form' for the test to store the result.  It is
        // also very bad to store the result for the purpose of running the
        // requested sub-test when asked, as this will cause a recursive-
        // style event in the result listeners, which some listeners may
        // not support.  Therefore, the tests are loaded into a list, and
        // executed at the end of the test run.  Note that this causes the
        // added tests to run after the tearDown method.

        Thread t = Thread.currentThread();
        Vector list = (Vector) this.perThreadTestList.get(t);
        // shouldn't be re-entrant!
        // but we'll allow it, however the tests added in this run will
        // only be executed at the end of the recursive run calls.
        if (list == null) {
            this.perThreadTestList.put(t, new Vector());
        }

        super.run(result);

        // if this method is not a reentrant method...
        if (list == null) {
            // run all the added tests
            list = (Vector) this.perThreadTestList.get(t);
            if (list != null) {
                LOG.debug("run method now executing all added tests (count=" +
                        list.size() + "); current ran test count = " +
                        result.runCount());
                Enumeration elements = list.elements();
                while (elements.hasMoreElements()) {
                    Test test = (Test) elements.nextElement();
                    LOG.debug("running test [" + test + "] from test [" +
                            getName() + "]");
                    test.run(result);
                    LOG.debug("run over for test [" + test +
                            "] from test [" + getName() +
                            "]; current ran test count = " + result.runCount());
                }

                // now remove the list from the hashtable, for future
                // reentrancy
                this.perThreadTestList.remove(t);
            }
        } else {
            LOG.debug(
                    "run method was re-entered.  Ignoring added tests for now.");
        }
    }
}

