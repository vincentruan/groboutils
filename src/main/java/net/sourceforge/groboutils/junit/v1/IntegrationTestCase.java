/*
 * @(#)IntegrationTestCase.java
 *
 * Copyright (C) 2002-2004 Matt Albrecht
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

/**
 * A TestCase specific for Integration tests.  It has the additional
 * functionality of soft validation through the AssertTestFactory class.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2004/01/09 20:32:26 $
 * @since March 28, 2002
 */
public class IntegrationTestCase extends SubTestTestCase {
    private AssertTestFactory atf;


    /**
     * Standard JUnit format for test constructors (pre JUnit 3.8).
     *
     * @param name the name of the test case.
     */
    public IntegrationTestCase(String name) {
        super(name);

        this.atf = new AssertTestFactory(getName(), true);
    }


    //-------------------------------------------------------------------------
    // Soft assert calls

    /**
     * Asserts that a condtion is true.  If it isn't it throws an
     * AssertionFailedError.
     *
     * @param message   text reported during failure.
     * @param condition must be true or an exception is raised.
     */
    public void softAssertTrue(String message, boolean condition) {
        addSubTest(this.atf.createAssertTrue(message, condition));
    }


    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError.
     *
     * @param condition must be true or an exception is raised.
     */
    public void softAssertTrue(boolean condition) {
        addSubTest(this.atf.createAssertTrue(condition));
    }


    /**
     * Asserts that a condtion is false.  If it isn't it throws an
     * AssertionFailedError.
     *
     * @param message   text reported during failure.
     * @param condition must be false or an exception is raised.
     * @since 03-Nov-2002
     */
    public void softAssertFalse(String message, boolean condition) {
        addSubTest(this.atf.createAssertFalse(message, condition));
    }


    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError.
     *
     * @param condition must be false or an exception is raised.
     * @since 03-Nov-2002
     */
    public void softAssertFalse(boolean condition) {
        addSubTest(this.atf.createAssertFalse(condition));
    }


    /**
     * Fails a test with the given message.
     *
     * @param message text reported during failure.
     */
    public void softFail(String message) {
        addSubTest(this.atf.createFail(message));
    }


    /**
     * Fails a test with no message.
     */
    public void softFail() {
        addSubTest(this.atf.createFail());
    }


    /**
     * Asserts that two objects are equal (<tt>.equals()</tt>). If they
     * are not an AssertionFailedError is thrown.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, Object expected, Object actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(Object expected, Object actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @since 03-Nov-2002
     */
    public void softAssertEquals(String message, String expected, String actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @since 03-Nov-2002
     */
    public void softAssertEquals(String expected, String actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @param delta    Description of the Parameter
     */
    public void softAssertEquals(String message, double expected,
                                 double actual, double delta) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual,
                delta));
    }


    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @param delta    Description of the Parameter
     */
    public void softAssertEquals(double expected, double actual, double delta) {
        addSubTest(this.atf.createAssertEquals(expected, actual, delta));
    }


    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @param delta    Description of the Parameter
     */
    public void softAssertEquals(String message, float expected, float actual,
                                 float delta) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual,
                delta));
    }


    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @param delta    Description of the Parameter
     */
    public void softAssertEquals(float expected, float actual, float delta) {
        addSubTest(this.atf.createAssertEquals(expected, actual, delta));
    }


    /**
     * Asserts that two longs are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, long expected, long actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two longs are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(long expected, long actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two booleans are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, boolean expected,
                                 boolean actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two booleans are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(boolean expected, boolean actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two bytes are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, byte expected, byte actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two bytes are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(byte expected, byte actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two chars are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, char expected, char actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two chars are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(char expected, char actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two shorts are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, short expected, short actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two shorts are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(short expected, short actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that two ints are equal.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(String message, int expected, int actual) {
        addSubTest(this.atf.createAssertEquals(message, expected, actual));
    }


    /**
     * Asserts that two ints are equal.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertEquals(int expected, int actual) {
        addSubTest(this.atf.createAssertEquals(expected, actual));
    }


    /**
     * Asserts that an object isn't null.
     *
     * @param message text reported during failure.
     * @param object  Description of the Parameter
     */
    public void softAssertNotNull(String message, Object object) {
        addSubTest(this.atf.createAssertNotNull(message, object));
    }


    /**
     * Asserts that an object isn't null.
     *
     * @param object Description of the Parameter
     */
    public void softAssertNotNull(Object object) {
        addSubTest(this.atf.createAssertNotNull(object));
    }


    /**
     * Asserts that an object is null.
     *
     * @param message text reported during failure.
     * @param object  Description of the Parameter
     */
    public void softAssertNull(String message, Object object) {
        addSubTest(this.atf.createAssertNull(message, object));
    }


    /**
     * Asserts that an object is null.
     *
     * @param object Description of the Parameter
     */
    public void softAssertNull(Object object) {
        addSubTest(this.atf.createAssertNull(object));
    }


    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertSame(String message, Object expected, Object actual) {
        addSubTest(this.atf.createAssertSame(message, expected, actual));
    }


    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     */
    public void softAssertSame(Object expected, Object actual) {
        addSubTest(this.atf.createAssertSame(expected, actual));
    }


    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown.
     *
     * @param message  text reported during failure.
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @since 03-Nov-2002
     */
    public void softAssertNotSame(String message, Object expected, Object actual) {
        addSubTest(this.atf.createAssertNotSame(message, expected, actual));
    }


    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     *
     * @param expected value expected from the test.
     * @param actual   actual value generated by the test.
     * @since 03-Nov-2002
     */
    public void softAssertNotSame(Object expected, Object actual) {
        addSubTest(this.atf.createAssertNotSame(expected, actual));
    }
}

