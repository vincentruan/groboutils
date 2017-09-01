/*
 * @(#)TestClassCreator.java
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

package net.sourceforge.groboutils.junit.v1.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;


/**
 * Creates Test instances based on a <tt>TestClassParser</tt>.
 * <p>
 * Ripped the test method discovery code out of junit.framework.TestSuite to
 * allow it to have usable logic.
 * <p>
 * This is not covered under the GroboUtils license, but rather under the
 * JUnit license (IBM Public License).  This heading may not be totally
 * in line with the license, so I'll change it when I find out what needs to
 * be changed.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:21 $
 * @since November 4, 2002
 */
public class TestClassCreator {
    private static final Logger LOG = LoggerFactory.getLogger(TestClassCreator.class);

    private ITestCreator creator;
    private Vector warnings = new Vector();


    /**
     * Creates an instance that creates test instances based on the given
     * creator.
     *
     * @throws IllegalArgumentException if <tt>theClass</tt> is
     *                                  <tt>null</tt>.
     */
    public TestClassCreator(final ITestCreator tc) {
        if (tc == null) {
            throw new IllegalArgumentException("no null arguments");
        }
        this.creator = tc;
    }


    //-------------------------------------------------------------------------
    // Public methods

    /**
     * Create a new Test that will fail with the given error message.
     *
     * @param message the text to report in the failure of the created
     *                Test.
     * @return a test that will fail with the given message.
     */
    public static Test createWarningTest(final String message) {
        return new TestCase("warning") {
            protected void runTest() {
                fail(message);
            }
        };
    }

    /**
     * Retrieve all warnings generated during the introspection of the class,
     * or test creation.  If a <tt>clearWarnings()</tt> call was ever made, then
     * only those warnings that were encountered after the call will be
     * returned.
     *
     * @return an array of all warnings generated while creating the test
     * array.
     */
    public String[] getWarnings() {
        String w[] = new String[this.warnings.size()];
        this.warnings.copyInto(w);
        return w;
    }

    /**
     * Remove all current warnings.
     */
    public void clearWarnings() {
        this.warnings.removeAllElements();
    }

    /**
     * This will create all test objects for the test registered with the
     * parser.  Any errors reported during generation will be added to the
     * warnings list.
     *
     * @return all valid tests created through inspection.
     */
    public Test[] createTests(TestClassParser tcp) {
        Vector t = new Vector();
        if (this.creator.canCreate(tcp.getTestClass())) {
            Method m[] = tcp.getTestMethods();
            for (int i = 0; i < m.length; ++i) {
                try {
                    Test tt = this.creator.createTest(
                            tcp.getTestClass(), m[i]);
                    if (tt != null) {
                        t.addElement(tt);
                    } else {
                        warning("Could not create test for class " +
                                tcp.getTestClass().getName() + " and method " +
                                m[i].getName() + ".");
                    }
                } catch (InstantiationException ie) {
                    warning("Method " + m[i].getName() +
                            " could not be added as a test: " +
                            ie);
                } catch (NoSuchMethodException nsme) {
                    warning("No valid constructor for " +
                            tcp.getTestClass().getName() +
                            ": " + nsme);
                } catch (InvocationTargetException ite) {
                    warning("Construction of class " +
                            tcp.getTestClass().getName() +
                            " caused an exception: " + ite.getTargetException());
                } catch (IllegalAccessException iae) {
                    warning("Protection on constructor for class " +
                            tcp.getTestClass().getName() +
                            " was invalid: " + iae);
                } catch (ClassCastException cce) {
                    warning("Class " + tcp.getTestClass().getName() +
                            " is not of Test type.");
                }
            }
        } else {
            warning("TestCreator does not know how to handle class " +
                    tcp.getTestClass().getName() + ".");
        }

        Test tt[] = new Test[t.size()];
        t.copyInto(tt);
        return tt;
    }

    /**
     * For every warning currently known in this creator and the parser,
     * create a Test that fails with the warning's message.  Note that after
     * creating a test with the warnings, this instance will still know about
     * the warnings.
     *
     * @return an array of tests that fail with a particular warning.
     */
    public Test[] createWarningTests(TestClassParser tcp) {
        String s1[] = getWarnings();
        String s2[] = tcp.getWarnings();
        Test t[] = new Test[s1.length + s2.length];
        for (int i = 0; i < s1.length; ++i) {
            t[i] = createWarningTest(s1[i]);
        }
        for (int i = 0; i < s2.length; ++i) {
            t[i + s1.length] = createWarningTest(s2[i]);
        }
        return t;
    }

    /**
     * Create a new TestSuite, containing the tests returned by the call to
     * <tt>createTests()</tt>.  No warning tests will be added.
     *
     * @return a new TestSuite with all the valid, discovered tests.
     */
    public TestSuite createTestSuite(TestClassParser tcp) {
        TestSuite ts = new TestSuite(tcp.getName());
        Test t[] = createTests(tcp);
        for (int i = 0; i < t.length; ++i) {
            if (t[i] != null) {
                ts.addTest(t[i]);
            }
        }
        return ts;
    }

    /**
     * Create a new TestSuite, containing the tests returned by the call to
     * <tt>createTests()</tt> and <tt>createWarningTests</tt>.
     *
     * @return a new TestSuite with all the valid, discovered tests, and the
     * warning tests.
     */
    public TestSuite createAllTestSuite(TestClassParser tcp) {
        TestSuite ts = createTestSuite(tcp);
        Test t[] = createWarningTests(tcp);
        for (int i = 0; i < t.length; ++i) {
            if (t[i] != null) {
                ts.addTest(t[i]);
            }
        }
        return ts;
    }


    //-------------------------------------------------------------------------
    // Protected methods

    /**
     * Adds a warning message to the inner list of warnings.
     *
     * @param message the message describing the warning.
     */
    protected void warning(final String message) {
        LOG.info("WARNING: " + message);
        this.warnings.addElement(message);
    }
}
