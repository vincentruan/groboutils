/*
 * @(#)InterfaceTestSuite.java
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

package net.sourceforge.groboutils.junit.v1.iftc;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.groboutils.junit.v1.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Vector;


/**
 * Allows for tests to be written on interfaces or abstract classes.  These
 * must be run through an InterfaceTestSuite to have the implemented object
 * be set correctly.
 * <p>
 * This class extends <tt>TestSuite</tt> only for the purpose of being a testing
 * repository.  The act of parsing TestCase classes is delegated to
 * new <tt>TestSuite</tt> instances.  A new instance will be created for each
 * test method (just as <tt>TestSuite</tt> does), If a <tt>TestCase</tt> class
 * has a constructor which is of the form <tt>( String, ImplFactory )</tt>,
 * then each test method instance will be created
 * once for each known <tt>ImplFactory</tt> object; these will be
 * stored and executed through the <tt>ImplFactory</tt> class.  All other
 * classes will be added just as TestSuite does (the standard method).
 * <p>
 * The creation of test instances is delayed until the tests are actually
 * retrieved via the <tt>testAt()</tt>, <tt>tests()</tt>, and
 * <tt>testCount()</tt> methods.  Therefore, adding new Classes and
 * ImplFactory instances after the creation time will cause an error, due to
 * problems with <tt>addTest()</tt> (they cannot be removed).
 * <p>
 * Currently, this class is slow: it does not do smart things like cache
 * results from inspection on the same class object.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:20 $
 * @see InterfaceTestCase
 * @see ImplFactory
 * @see TestSuite
 * @since March 2, 2002
 */
public class InterfaceTestSuite extends TestSuite {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceTestSuite.class);

    // these are not private for test-case usage.
    Vector creators = new Vector();
    Vector classes = new Vector();


    /**
     * Constructs a TestSuite from the given class, and sets the initial
     * set of creators. Adds all the methods
     * starting with "test" as test cases to the suite.
     */
    public InterfaceTestSuite() {
        // do nothing
    }


    /**
     * Constructs a TestSuite from the given class, and sets the initial
     * set of creators. Adds all the methods
     * starting with "test" as test cases to the suite.
     *
     * @param theClass the class under inspection
     */
    public InterfaceTestSuite(Class theClass) {
        addTestSuite(theClass);
    }


    /**
     * Constructs a TestSuite from the given class, and sets the initial
     * set of creators. Adds all the methods
     * starting with "test" as test cases to the suite.
     *
     * @param theClass the class under inspection
     * @param f        a factory to add to this suite.
     */
    public InterfaceTestSuite(Class theClass, ImplFactory f) {
        addTestSuite(theClass);
        addFactory(f);
    }


    /**
     * Add a new Implementation factory to the suite.  This should only be
     * called before any tests are extracted from this suite.  If it is
     * called after, then an IllegalStateException will be generated.
     *
     * @param f a factory to add to this suite.
     * @throws IllegalArgumentException if <tt>f</tt> is <tt>null</tt>
     * @throws IllegalStateException    if the tests have already been generated
     */
    public void addFactory(ImplFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("no null args");
        }
        if (creators == null) {
            throw new IllegalStateException("Already created TestSuites.");
        }
        this.creators.addElement(f);
    }


    /**
     * Add an array of new Implementation factories to the suite.
     * This should only be
     * called before any tests are extracted from this suite.
     *
     * @param f a set of factories to add to this suite.
     * @throws IllegalArgumentException if <tt>f</tt> is <tt>null</tt>, or
     *                                  any element in the list is <tt>null</tt>
     * @throws IllegalStateException    if the tests have already been generated
     */
    public void addFactories(ImplFactory f[]) {
        if (f == null) {
            throw new IllegalArgumentException("no null args");
        }
        for (int i = 0; i < f.length; ++i) {
            addFactory(f[i]);
        }
    }


    /**
     * Add an InterfaceTestSuite to this suite.  If an interface extends
     * another interface, it should add it's super interface's test suite
     * through this method.  The same goes for any abstract or base class.
     * Adding the parent suite through this method will cause both suites to
     * share creators.  In fact, the parent suite <b>cannot</b> have any
     * factories when passed into this method, because they will be ignored.
     * <p>
     * This allows for the flexibility of determining whether to add a full
     * test suite, without sharing factories, or not.
     *
     * @param t a test to add to the suite.  It can be <tt>null</tt>.
     */
    public void addInterfaceTestSuite(InterfaceTestSuite t) {
        if (t != null) {
            if (t.creators != null && t.classes != null && t.classes.size() > 0) {
                if (t.creators.size() > 0) {
                    LOG.warn("Passed in InterfaceTestSuite " + t +
                            " with factories registered.  This is a no-no.  " +
                            "You need to pass it in through addTest(), or not add " +
                            "factories to it.");
                } else {
                    Enumeration elements = t.classes.elements();
                    while (elements.hasMoreElements()) {
                        addTestSuite((Class) elements.nextElement());
                    }
                }
            }
        }
    }


    /**
     * Add an array of tests to the suite.
     *
     * @param t a set of tests to add to this suite.
     */
    public void addTests(Test[] t) {
        if (t == null) {
            throw new IllegalArgumentException("no null arguments");
        }
        for (int i = 0; i < t.length; ++i) {
            addTest(t[i]);
        }
    }


    /**
     * Adds all the methods
     * starting with "test" as test cases to the suite.
     * <p>
     * Overrides the parent implementation to allow for InterfaceTests.
     *
     * @param theClass the class under inspection
     * @throws IllegalArgumentException if <tt>theClass</tt> is <tt>null</tt>
     * @throws IllegalStateException    if the tests have already been generated
     */
    public void addTestSuite(Class theClass) {
        if (theClass == null) {
            throw new IllegalArgumentException("no null arguments");
        }
        if (this.classes == null) {
            throw new IllegalStateException("Class " + theClass.getName() +
                    " added after the load time.  See JavaDoc for proper usage.");
        }
        this.classes.addElement(theClass);
    }


    // from parent
    public Test testAt(int index) {
        loadTestSuites();
        return super.testAt(index);
    }


    // from parent
    public int testCount() {
        loadTestSuites();
        return super.testCount();
    }


    // from parent
    public Enumeration tests() {
        loadTestSuites();
        return super.tests();
    }


    /**
     * Load all the tests from the cache of classes and factories.
     */
    protected void loadTestSuites() {
        // if either of these Vectors are null, then the loading has
        // already been done.
        if (this.creators == null || this.classes == null) {
            return;
        }

        ITestCreator tc = createTestCreator(this.creators);
        TestClassCreator tcc = new TestClassCreator(tc);
        for (Enumeration elements = this.classes.elements(); elements.hasMoreElements(); ) {
            Class c = (Class) elements.nextElement();
            loadTestSuite(c, tcc);
        }

        // tell the instance to not load test suites again, and not allow
        // new factories to be registered.
        this.creators = null;
        this.classes = null;
    }


    /**
     * Load all the tests and warnings from the class and the creator
     * type into this instance's suite of tests.
     *
     * @param testClass the class being inspected for test instance
     *                  creation.
     * @param tcc       the creator type that will be used to create new tests.
     */
    protected void loadTestSuite(Class testClass, TestClassCreator tcc) {
        TestClassParser tcp = new TestClassParser(testClass);

        // ensure that all unwanted warnings are removed.
        tcc.clearWarnings();

        Test t[] = tcc.createTests(tcp);
        if (t == null || t.length <= 0) {
            // no discovered tests, so create an error test
            LOG.info("No tests for class discovered.");
            addTest(TestClassCreator.createWarningTest(
                    "No tests found in test class " + testClass.getName()));
        } else {
            addTests(t);
        }
        addTests(tcc.createWarningTests(tcp));

        // be a nice citizen and clean up after ourself.
        tcc.clearWarnings();
    }


    /**
     * Create a TestCreator that contains the knowledge of how to properly
     * parse and generate tests for all types of supported test classes.
     *
     * @param factories a vector of ImplFactory instances to load Interface
     *                  test class instances.
     * @return the new creator.
     */
    protected ITestCreator createTestCreator(Vector vf) {
        ImplFactory factories[] = new ImplFactory[vf.size()];
        vf.copyInto(factories);

        // Order matters!!!
        //
        // Use the original version before the new technique for backwards
        // compatibility.
        ITestCreator tc = new DelegateTestCreator(new ITestCreator[]{
                new IftcOrigCreator(factories),
                //new Iftc3_8Creator( factories ),
                new JUnitOrigCreator(),
                new JUnit3_8Creator()
        });
        return tc;
    }
}

