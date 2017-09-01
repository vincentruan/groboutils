/*
 * @(#)TestClassParser.java
 */

package net.sourceforge.groboutils.junit.v1.parser;

import junit.framework.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;


/**
 * Parses Test classes to discover the usable test methods.
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
 * @version $Date: 2002/11/05 00:49:31 $
 * @since March 28, 2002
 */
public class TestClassParser {
    private static final Logger LOG = LoggerFactory.getLogger(
            TestClassParser.class);
    Vector testMethods = new Vector();
    private Class testClass;
    private Vector warnings = new Vector();


    /**
     * The primary constructor, which will cause this instance to know how to
     * parse only the passed-in class.
     *
     * @param theClass the class to parse for testing.
     * @throws IllegalArgumentException if <tt>theClass</tt> is
     *                                  <tt>null</tt>.
     */
    public TestClassParser(final Class theClass) {
        if (theClass == null) {
            throw new IllegalArgumentException("no null arguments");
        }
        this.testClass = theClass;

        if (testClass(theClass)) {
            discoverTestMethods(theClass);
        }
    }


    //-------------------------------------------------------------------------
    // Public methods


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
     * Retrieve all public test methods discovered through inspection.
     *
     * @return all test methods.
     */
    public Method[] getTestMethods() {
        Method m[] = new Method[this.testMethods.size()];
        this.testMethods.copyInto(m);
        return m;
    }


    /**
     * Get the name of the test suite.  By default, this is the class name.
     *
     * @return the name of the test suite.
     */
    public String getName() {
        return this.testClass.getName();
    }


    /**
     * Get the class under test.  This will never return <tt>null</tt>, and
     * will always match the class passed into the constructor.
     *
     * @return the class under test.
     */
    public Class getTestClass() {
        return this.testClass;
    }


    //-------------------------------------------------------------------------
    // Parse methods


    /**
     * Discover if the given class is a valid testing class.
     *
     * @param theClass the class to parse for testing.
     * @return <tt>true</tt> if the class is a public test class, otherwise
     * <tt>false</tt>.
     */
    protected boolean testClass(final Class theClass) {
        boolean result = true;
        if (!Modifier.isPublic(theClass.getModifiers())) {
            warning("Class " + theClass.getName() + " is not public.");
            result = false;
        }
        if (!Test.class.isAssignableFrom(theClass)) {
            warning("Class " + theClass.getName() +
                    " does not implement " + Test.class.getName());
            result = false;
        }
        return result;
    }


    /**
     * Discover and record the test methods of the public test class
     * <tt>theClass</tt>.
     *
     * @param theClass the class to parse for testing.
     */
    protected void discoverTestMethods(final Class theClass) {
        Class superClass = theClass;
        Vector names = new Vector();
        while (Test.class.isAssignableFrom(superClass)) {
            Method[] methods = superClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                addTestMethod(methods[i], names);
            }
            superClass = superClass.getSuperclass();
        }
    }


    /**
     * Adds the method <tt>m</tt> to the inner list of known test methods,
     * but only if it is a public test method.
     *
     * @param m     the method to add.
     * @param names a list of method names that have already been inspected.
     */
    protected void addTestMethod(Method m, Vector names) {
        String name = m.getName();
        if (names.contains(name) || this.testMethods.contains(m)) {
            return;
        }

        if (isPublicTestMethod(m)) {
            names.addElement(name);

            this.testMethods.addElement(m);
        } else {
            // almost a test method
            if (isTestMethod(m)) {
                warning("Test method isn't public: " + m.getName());
            }
        }
    }


    /**
     * Asserts that the method is public, and that it is also a test method.
     *
     * @param m the method under scrutiny.
     * @return <tt>true</tt> if <tt>m</tt> is a public test method, otherwise
     * <tt>false</tt>.
     * @see #isTestMethod(Method)
     */
    protected boolean isPublicTestMethod(Method m) {
        return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
    }

    /**
     * Test if method <tt>m</tt> is a test method, which means it accepts
     * no parameters, returns <tt>void</tt>, and the name of the method
     * begins with <tt>test</tt>.
     *
     * @param m the method under scrutiny.
     * @return <tt>true</tt> if <tt>m</tt> is a public test method, otherwise
     * <tt>false</tt>.
     */
    protected boolean isTestMethod(Method m) {
        String name = m.getName();
        Class[] parameters = m.getParameterTypes();
        Class returnType = m.getReturnType();
        return parameters.length == 0 && name.startsWith("test") &&
                returnType.equals(Void.TYPE);
    }


    /**
     * Adds a warning message to the inner list of warnings.
     *
     * @param message the message describing the warning.
     */
    protected void warning(final String message) {
        LOG.debug("WARNING: " + message);
        this.warnings.addElement(message);
    }
}
