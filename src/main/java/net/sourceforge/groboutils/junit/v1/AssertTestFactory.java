/*
 * @(#)AssertTestFactory.java
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

import junit.framework.TestCase;
import org.junit.Assert;


/**
 * A factory that creates test instances for the standard set of assert
 * methods.  The created test instances should have their <tt>setName()</tt>
 * method invoked to properly set the name of the test.  Alternatively, the
 * factory instance can have the name set so that all tests will have the
 * same name.
 * <p>
 * To support JUnit 3.8 functionality, but remain backwards compatible with
 * earlier JUnit libraries, the names for the JUnit 3.8 methods will be
 * allowed, but they will call JUnit 3.7 compatible methods.
 * <p>
 * As of Dec 8, 2002, the factory can uniquely (per instance) name each
 * generated test via an index.  This can help traceability in identifying
 * each created test.  Alternatively, the user can set the factory's name
 * before invoking a create method.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2004/01/09 20:32:26 $
 * @since July 26, 2002
 */
public class AssertTestFactory {
    private String name;
    private int index = 0;
    private boolean useIndex = false;

    /**
     * Creates a new factory that can generate assertions as independent
     * test objects.
     */
    public AssertTestFactory() {
        // do nothing
    }


    /**
     * Creates a new factory with a specific name for each generated test,
     * but will not add an index to each generated test's name.
     *
     * @param name default name shared by all generated assertion tests.
     */
    public AssertTestFactory(String name) {
        this(name, false);
    }


    /**
     * Creates a new factory with a specific name for each generated test,
     * and can optionally add an index to each generated test's name.
     *
     * @param name             default name shared by all generated assertion tests.
     * @param useIndexWithName <tt>true</tt> if indecies will be appended
     *                         to each generated test's name, or <tt>false</tt> if they
     *                         will use the passed-in name exactly.
     * @since 08-Dec-2002
     */
    public AssertTestFactory(String name, boolean useIndexWithName) {
        setName(name);
        setUseIndexWithName(useIndexWithName);
    }

    /**
     * Returns the default test name.  If the name has never been set, then
     * this will return <tt>null</tt>.
     *
     * @return default name shared by all generated assertion tests.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the default test name.  This will not reset the generated index.
     *
     * @param name default name shared by all generated assertion tests.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns whether each generated test will add a unique (for this
     * instance) index to the test's name.
     *
     * @return <tt>true</tt> if an index is appended to the name, or
     * <tt>false</tt> if the test's name is exactly the factory's name.
     * @since 08-Dec-2002
     */
    public boolean getUseIndexWithName() {
        return this.useIndex;
    }

    /**
     * Sets whether each generated test will add a unique (for this instance)
     * index to the test's name.  Reseting this value will not affect the
     * index's value.
     *
     * @param useIndexWithName <tt>true</tt> if indecies will be appended
     *                         to each generated test's name, or <tt>false</tt> if they
     *                         will use the passed-in name exactly.
     * @since 08-Dec-2002
     */
    public void setUseIndexWithName(boolean useIndexWithName) {
        this.useIndex = useIndexWithName;
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError with the given message.
     *
     * @param message   message that describes what failed if the assertion
     *                  fails.
     * @param condition boolean to check for failure
     */
    public InnerTest createAssertTrue(String message, boolean condition) {
        return new AssertTrue1(getNextTestName(), message, condition);
    }


    //-----------------------------------------------------------------------

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError.
     *
     * @param condition boolean to check for failure
     */
    public InnerTest createAssertTrue(boolean condition) {
        return createAssertTrue(null, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError with the given message.
     *
     * @param message   message that describes what failed if the assertion
     *                  fails.
     * @param condition boolean to check for failure
     * @since 30-Oct-2002
     */
    public InnerTest createAssertFalse(String message, boolean condition) {
        return new AssertTrue1(getNextTestName(), message, !condition);
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError.
     *
     * @param condition boolean to check for failure
     * @since 30-Oct-2002
     */
    public InnerTest createAssertFalse(boolean condition) {
        // don't 'not' the condition here - it will be done in the
        // invoked method.
        return createAssertFalse(null, condition);
    }

    /**
     * Fails a test with the given message.
     *
     * @param message message that describes what failed if the assertion
     *                fails.
     */
    public InnerTest createFail(String message) {
        return new Fail1(getNextTestName(), message);
    }

    /**
     * Fails a test with no message.
     */
    public InnerTest createFail() {
        return createFail(null);
    }

    //-----------------------------------------------------------------------

    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, Object expected,
                                        Object actual) {
        return new AssertEquals1(getNextTestName(), message, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(Object expected, Object actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @since 30-Oct-2002
     */
    public InnerTest createAssertEquals(String message, String expected,
                                        String actual) {
        return new AssertEquals1(getNextTestName(), message, expected, actual);
    }


    //-----------------------------------------------------------------------

    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @since 30-Oct-2002
     */
    public InnerTest createAssertEquals(String expected, String actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @param delta    maximum distance between expected and actual such that
     *                 the two values are considered equivalent.  Necessary since
     *                 floating-point numbers on computers are approximations of their
     *                 equivalent values; that is, storing <tt>1.1</tt> may actually be
     *                 stored as <tt>1.099999999999</tt>.
     */
    public InnerTest createAssertEquals(String message, double expected,
                                        double actual, double delta) {
        return new AssertEquals2(getNextTestName(), message, expected, actual,
                delta);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @param delta    maximum distance between expected and actual such that
     *                 the two values are considered equivalent.  Necessary since
     *                 floating-point numbers on computers are approximations of their
     *                 equivalent values; that is, storing <tt>1.1</tt> may actually be
     *                 stored as <tt>1.099999999999</tt>.
     */
    public InnerTest createAssertEquals(double expected, double actual,
                                        double delta) {
        return createAssertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @param delta    maximum distance between expected and actual such that
     *                 the two values are considered equivalent.  Necessary since
     *                 floating-point numbers on computers are approximations of their
     *                 equivalent values; that is, storing <tt>1.1</tt> may actually be
     *                 stored as <tt>1.099999999999</tt>.
     */
    public InnerTest createAssertEquals(String message, float expected,
                                        float actual, float delta) {
        return new AssertEquals3(getNextTestName(), message, expected, actual,
                delta);
    }

    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @param delta    maximum distance between expected and actual such that
     *                 the two values are considered equivalent.  Necessary since
     *                 floating-point numbers on computers are approximations of their
     *                 equivalent values; that is, storing <tt>1.1</tt> may actually be
     *                 stored as <tt>1.099999999999</tt>.
     */
    public InnerTest createAssertEquals(float expected, float actual,
                                        float delta) {
        return createAssertEquals(null, expected, actual, delta);
    }

    //-----------------------------------------------------------------------

    /**
     * Asserts that two longs are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, long expected,
                                        long actual) {
        return createAssertEquals(message, new Long(expected),
                new Long(actual));
    }

    /**
     * Asserts that two longs are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(long expected, long actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two booleans are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, boolean expected,
                                        boolean actual) {
        return createAssertEquals(message, new Boolean(expected),
                new Boolean(actual));
    }


    //-----------------------------------------------------------------------

    /**
     * Asserts that two booleans are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(boolean expected, boolean actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two bytes are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, byte expected,
                                        byte actual) {
        return createAssertEquals(message, new Byte(expected),
                new Byte(actual));
    }

    /**
     * Asserts that two bytes are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(byte expected, byte actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two chars are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, char expected,
                                        char actual) {
        return createAssertEquals(message, new Character(expected),
                new Character(actual));
    }

    /**
     * Asserts that two chars are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(char expected, char actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two shorts are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, short expected,
                                        short actual) {
        return createAssertEquals(message, new Short(expected),
                new Short(actual));
    }

    /**
     * Asserts that two shorts are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(short expected, short actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that two ints are equal.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(String message, int expected,
                                        int actual) {
        return createAssertEquals(message, new Integer(expected),
                new Integer(actual));
    }

    /**
     * Asserts that two ints are equal.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertEquals(int expected, int actual) {
        return createAssertEquals(null, expected, actual);
    }

    /**
     * Asserts that an object isn't null.
     *
     * @param message message that describes what failed if the assertion
     *                fails.
     * @param object  test object that must not be null.
     */
    public InnerTest createAssertNotNull(String message, Object object) {
        return new AssertNotNull1(getNextTestName(), message, object);
    }

    /**
     * Asserts that an object isn't null.
     *
     * @param object test object that must not be null.
     */
    public InnerTest createAssertNotNull(Object object) {
        return createAssertNotNull(null, object);
    }

    /**
     * Asserts that an object is null.
     *
     * @param message message that describes what failed if the assertion
     *                fails.
     * @param object  test object that must be null.
     */
    public InnerTest createAssertNull(String message, Object object) {
        return new AssertNull1(getNextTestName(), message, object);
    }

    /**
     * Asserts that an object is null.
     *
     * @param object test object that must be null.
     */
    public InnerTest createAssertNull(Object object) {
        return createAssertNull(null, object);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertSame(String message, Object expected,
                                      Object actual) {
        return new AssertSame1(getNextTestName(), message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     *
     * @param expected value that the test expects to find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     */
    public InnerTest createAssertSame(Object expected, Object actual) {
        return createAssertSame(null, expected, actual);
    }


    //-----------------------------------------------------------------------

    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  message that describes what failed if the assertion
     *                 fails.
     * @param expected value that the test expects to not find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @since 30-Oct-2002
     */
    public InnerTest createAssertNotSame(String message, Object expected,
                                         Object actual) {
        String str = "expected not same";
        if (message != null) {
            str = message + ' ' + str;
        }
        // manual emulation of JUnit 3.8 functionality
        return new AssertTrue1(getNextTestName(),
                str, expected != actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     *
     * @param expected value that the test expects to not find from the tested
     *                 code.
     * @param actual   actual value generated by tested code.
     * @since 30-Oct-2002
     */
    public InnerTest createAssertNotSame(Object expected, Object actual) {
        return createAssertNotSame(null, expected, actual);
    }

    /**
     * Generates the next test's name.
     *
     * @return the next name for a generated test, possibly appending an
     * index value.
     * @since 08-Dec-2002
     */
    private String getNextTestName() {
        String n = getName();
        if (this.useIndex) {
            synchronized (this) {
                ++this.index;
                n += this.index;
            }
        }
        return n;
    }


    //-----------------------------------------------------------------------

    /**
     * Inner test instance which specializes in generating a message with
     * the test's instance's specific name.
     */
    public static abstract class InnerTest extends TestCase {
        private String message;

        /**
         * JUnit 3.8 allows for test constructors to not have to specify
         * a name in the super() call, but for JUnit 3.7 compatibility,
         * we will only support the original usage.
         */
        public InnerTest(String name, String msg) {
            super(name);
            this.message = msg;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String msg) {
            this.message = msg;
        }

        public String getFullMessage() {
            String msg = getMessage();
            String name = this.getName();
            if (name != null) {
                msg = name + ": " + msg;
            }
            return msg;
        }

        protected final void runTest() {
            callAssert(getFullMessage());
        }

        public abstract void callAssert(String message);
    }

    private static class AssertTrue1 extends InnerTest {
        boolean condition;

        public AssertTrue1(String n, String m, boolean c) {
            super(n, m);
            this.condition = c;
        }

        public void callAssert(String msg) {
            Assert.assertTrue(msg, this.condition);
        }
    }

    private static class Fail1 extends InnerTest {
        public Fail1(String n, String m) {
            super(n, m);
        }

        public void callAssert(String msg) {
            Assert.fail(msg);
        }
    }


    //-----------------------------------------------------------------------

    private static class AssertEquals1 extends InnerTest {
        Object expected;
        Object actual;

        public AssertEquals1(String n, String m, Object e, Object a) {
            super(n, m);
            this.expected = e;
            this.actual = a;
        }

        public void callAssert(String msg) {
            Assert.assertEquals(msg, this.expected, this.actual);
        }
    }

    private static class AssertEquals2 extends InnerTest {
        double expected;
        double actual;
        double delta;

        public AssertEquals2(String n, String m, double e, double a,
                             double d) {
            super(n, m);
            this.expected = e;
            this.actual = a;
            this.delta = d;
        }

        public void callAssert(String msg) {
            Assert.assertEquals(msg, this.expected, this.actual,
                    this.delta);
        }
    }

    private static class AssertEquals3 extends InnerTest {
        float expected;
        float actual;
        float delta;

        public AssertEquals3(String n, String m, float e, float a, float d) {
            super(n, m);
            this.expected = e;
            this.actual = a;
            this.delta = d;
        }

        public void callAssert(String msg) {
            Assert.assertEquals(msg, this.expected, this.actual,
                    this.delta);
        }
    }

    private static class AssertNotNull1 extends InnerTest {
        Object object;

        public AssertNotNull1(String n, String m, Object o) {
            super(n, m);
            this.object = o;
        }

        public void callAssert(String msg) {
            Assert.assertNotNull(msg, this.object);
        }
    }

    private static class AssertNull1 extends InnerTest {
        Object object;

        public AssertNull1(String n, String m, Object o) {
            super(n, m);
            this.object = o;
        }

        public void callAssert(String msg) {
            Assert.assertNull(msg, this.object);
        }
    }

    private static class AssertSame1 extends InnerTest {
        Object expected;
        Object actual;

        public AssertSame1(String n, String m, Object e, Object a) {
            super(n, m);
            this.expected = e;
            this.actual = a;
        }

        public void callAssert(String msg) {
            Assert.assertSame(msg, this.expected, this.actual);
        }
    }
}

