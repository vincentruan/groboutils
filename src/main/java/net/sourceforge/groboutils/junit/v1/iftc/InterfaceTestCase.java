/*
 * @(#)InterfaceTestCase.java
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


import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Stack;


/**
 * A subclass of TestCase to ease the requirements of creating an
 * interface test.  The tests should be thought of as "contract tests".
 * Subclasses can call <tt>createImplObject()</tt> to create a new instance
 * of a subclass of <tt>interfaceClass</tt>, which is generated from the
 * <tt>ImplFactory</tt> passed into the constructor.
 * <p>
 * Subclasses that want to use the InterfaceTestSuite helper class will need
 * to specify a constructor similar to:
 * <PRE>
 * public MyClassTest( String name, ImplFactory f )
 * {
 * super( name, MyClass.class, f );
 * }
 * </PRE>
 * where <tt>MyClass</tt> is the interface or base class under test.
 * <p>
 * As of October 30, 2002, the InterfaceTestCase has a slightly different
 * behavior when the factory instance is an implementation of ICxFactory.
 * In this scenario, it will store all the instantiated objects in the stack,
 * and each instantiated object will be passed to the ICxFactory instance
 * during the test's normal tearDown.  If the ICxFactory throws an exception
 * during any of the tearDown calls, they will be stored up and reported
 * in a single exception.  Therefore, if you want this functionality, then
 * you will need to ensure that your <tt>tearDown()</tt> method calls the
 * super <tt>tearDown()</tt>.
 * <p>
 * Even though JUnit 3.8+ allows for a TestCase to have a default (no-arg)
 * constructor, the <tt>InterfaceTestCase</tt> does not support this.  The
 * benefits simply aren't there for interface tests: they will still have to
 * create a constructor which passes <tt>InterfaceTestCase</tt> which class
 * is being tested.  Since a constructor is required anyway, the little extra
 * effort to add two arguments to the constructor and call to the super
 * is trivial compared to not needing the constructor at all.
 * <p>
 * As of 08-Dec-2002, the returned name of the test can include the class's
 * name, without the package, to improve traceability.  This will allow
 * the user to be able to see in which specific test class an error occured
 * through the Ant JUnit report mechanism.  This is enabled by default, but
 * can be disabled by setting the Java system-wide property
 * "net.sourceforge.groboutils.junit.v1.iftc.InterfaceTestCase.no-classname"
 * to <tt>true</tt>,
 * which is dynamically checked at runtime at each call.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:20 $
 * @see ImplFactory
 * @see ICxFactory
 * @see InterfaceTestSuite
 * @since March 2, 2002
 */
public abstract class InterfaceTestCase extends TestCase {
    // package protected for test-case use.
    static final String DONT_USE_CLASSNAME =
            InterfaceTestCase.class.getName() + ".no-classname";

    private ImplFactory factory;
    private Class interfaceClass;

    // Due to possible memory-leak, this stack will only contain
    // instantiated objects when the factory is of type ICxFactory.
    private Stack instantiatedObjects = new Stack();

    // As a slight optimization, we will cache the check if the
    // factory is an instance of ICxFactory or not.
    private boolean isICxFactory = false;

    // allows for manual setting of the classname display in the output name
    private Boolean useClassInName = null;


    /**
     * The standard constructor used by JUnit up to version 3.7.
     *
     * @param name           the name of the test to execute.
     * @param interfaceClass the class which this test case tests.
     * @param f              the factory which will create specific subclass instances.
     */
    public InterfaceTestCase(String name, Class interfaceClass, ImplFactory f) {
        super(name);
        if (interfaceClass == null || f == null) {
            throw new IllegalArgumentException("no null arguments");
        }

        // need to ensure that a common test coding error didn't occur...
        assertTrue(
                "Interface under test argument (" + interfaceClass.getName() +
                        ") is the same as the current test's class (" +
                        getClass().getName() +
                        ").  The correct usage is to pass in the Class object which " +
                        "corresponds to the superclass or interface all instance methods " +
                        "tested must extend.",
                !getClass().equals(interfaceClass));

        // the class can be an interface, an abstract class, or just
        // a regular class.  We don't care as long as it isn't null.
        this.interfaceClass = interfaceClass;
        this.factory = f;

        // cache the assertion for if the factory is an ICxFactory instance.
        if (f instanceof ICxFactory) {
            this.isICxFactory = true;
        }
    }


    /**
     * Sets whether the classname is put in the output or not.  If you don't
     * set this value here, it will use the value of the
     * system property described above.
     *
     * @since 03-Dec-2002
     */
    public void setUseClassInName(boolean use) {
        this.useClassInName = new Boolean(use);
    }


    /**
     * Calls the stored factory to create an implemented object.  Subclasses
     * should make their own method, say <tt>getObject()</tt>, which returns
     * this method's result, but casted to the right class.
     * <p>
     * This method makes an assertion that the factory's created object is not
     * <tt>null</tt>, so that the system state is ensured.  Therefore,
     * this method will never return <tt>null</tt>.  Also, this method asserts
     * that the created object is of the correct type (as passed in through
     * the constructor), so that it can be correctly cast without errors.
     *
     * @return the object created by the factory.
     */
    public Object createImplObject() {
        // ensure the factory was set.
        assertNotNull(
                "The factory instance was never set.",
                this.factory);

        Object o;
        try {
            o = this.factory.createImplObject();
        } catch (Exception ex) {
            // allow for the factory creation to throw exceptions.
            fail("Factory " + this.factory.toString() +
                    " threw exception " + ex + " during creation: " +
                    exceptionToString(ex));
            // the above call will always exit, but the compiler doesn't
            // know that.  So to make it happy the next line has been added.
            o = null;
        }
        assertNotNull(
                "The implementation factory " + this.factory + " created a null.",
                o);

        // Since the generated object is non-null, we will store it in our
        // stack, even if the next assert fails.  This allows for correct
        // deconstruction of *every* non-null generated object.
        if (this.isICxFactory) {
            this.instantiatedObjects.push(o);
        }

        assertTrue(
                "The implementation factory did not create a valid class: created " +
                        o.getClass().getName() + ", but should have been of type " +
                        getInterfaceClass().getName() + ".",
                getInterfaceClass().isInstance(o));
        return o;
    }


    /**
     * Return the interface or abstract class this test covers.
     *
     * @return the interface under test.
     */
    public Class getInterfaceClass() {
        return this.interfaceClass;
    }


    /**
     * Override the TestCase default getName so that the factory names are
     * returned as well.
     *
     * @return the method name being tested, along with the factory's
     * name.
     */
    public String getName() {
        return getNamePrefix() + super.getName() +
                "[" + this.factory.toString() + "]";
    }


    /**
     * Ensure, for JUnit 3.7 support, that the original name() method is
     * still supported.
     *
     * @return getName().
     */
    public String name() {
        return getName();
    }


    /**
     * Send each instantiated object to the factory for cleanup.
     *
     * @throws Exception thrown if the super's tearDown throws an
     *                   exception, or if any exceptions are thrown during the tear-down
     *                   of the factory generated instances.
     */
    protected void tearDown() throws Exception {
        if (this.isICxFactory) {
            int errorCount = 0;
            StringBuffer sb = new StringBuffer(
                    "Encountered factory tearDown exceptions: ");
            ICxFactory cf = (ICxFactory) this.factory;
            while (!this.instantiatedObjects.isEmpty()) {
                try {
                    cf.tearDown(this.instantiatedObjects.pop());
                } catch (ThreadDeath td) {
                    // never swallow thread death exceptions
                    throw td;
                } catch (Throwable t) {
                    // catch all factory exceptions, and sum them up into
                    // a single exception at the end.
                    if (errorCount > 0) {
                        sb.append("; ");
                    }
                    sb.append(t.toString());

                    ++errorCount;

                    // Tell the user about this exception.
                    t.printStackTrace();
                }
            }
            // only do the assertion *after* all the generated objects have
            // been torn down.
            assertTrue(sb.toString(), errorCount <= 0);
        }

        // tell the super-class to tear itself down.
        super.tearDown();
    }


    /**
     * Allow for easy translation of exceptions to strings, including
     * stack traces.
     *
     * @param t the exception to translate into a string.
     * @return the exception + stack trace as a string.
     */
    private String exceptionToString(Throwable t) {
        if (t == null) {
            return "<null exception>";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    /**
     * Generate the prefix for the name of this test class.
     *
     * @return the prefix string
     * @since 08-Dec-2002
     */
    private String getNamePrefix() {
        boolean usePrefix;
        if (this.useClassInName != null) {
            usePrefix = this.useClassInName.booleanValue();
        } else {
            usePrefix = !Boolean.getBoolean(DONT_USE_CLASSNAME);
        }
        String ret = "";
        if (usePrefix) {
            // get the classname of the current test, without the package.
            ret = this.getClass().getName();
            int pos = ret.lastIndexOf('.');
            if (pos >= 0) {
                ret = ret.substring(pos + 1);
            }
            ret = ret + '.';
        }
        return ret;
    }
}

