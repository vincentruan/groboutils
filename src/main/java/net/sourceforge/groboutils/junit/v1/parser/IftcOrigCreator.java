/*
 * @(#)IftcOrigCreator.java
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
import junit.framework.TestSuite;
import net.sourceforge.groboutils.junit.v1.iftc.ImplFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Creates Interface test cases based on the original JUnit construction
 * mechanism.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:21 $
 * @since November 4, 2002
 */
public class IftcOrigCreator implements ITestCreator {
    private ImplFactory factories[];


    /**
     * Default constructor.
     *
     * @param f factory list used in generating the tests.  If this is
     *          <tt>null</tt>, or if any entries are <tt>null</tt>, then an
     *          IllegalArgumentException will be thrown.
     */
    public IftcOrigCreator(ImplFactory[] f) {
        if (f == null) {
            throw new IllegalArgumentException("no null args");
        }

        // allow for empty factory list
        int len = f.length;
        this.factories = new ImplFactory[len];
        for (int i = len; --i >= 0; ) {
            if (f[i] == null) {
                throw new IllegalArgumentException("no null args");
            }
            this.factories[i] = f[i];
        }
    }


    /**
     * Creates a new test, based on the given class and method of the class.
     * This calls <tt>createTest( Class, Object[] )</tt> to create the new
     * class, which itself calls <tt>getConstructorArgTypes( Class )</tt> to
     * determine which constructor to get.  Also,
     * <tt>createTestArguments( Class, Method )</tt> is called to generate the
     * constructor's arguments.
     *
     * @param theClass the class to parse for testing.
     * @param m        the method that will be tested with the new class instance.
     * @throws InstantiationException if there was a problem creating the
     *                                class.
     * @throws NoSuchMethodException  if the method does not exist in the
     *                                class.
     * @see #createTest(Class, Object[])
     */
    public Test createTest(Class theClass, Method method)
            throws InstantiationException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException,
            ClassCastException {
        TestSuite suite = new TestSuite();

        int goodTestCount = 0;
        for (int i = 0; i < this.factories.length; ++i) {
            Test t = createTest(theClass, createTestArguments(theClass,
                    method, this.factories[i]));
            if (t != null) {
                ++goodTestCount;
                suite.addTest(t);
            }
        }

        if (goodTestCount <= 0) {
            suite.addTest(TestClassCreator.createWarningTest(
                    "No factories or valid instances for test class " +
                            theClass.getName() + ", method " + method.getName() + "."));
        }

        return suite;
    }


    /**
     * Checks if the creator can be used on the given class.
     *
     * @param theClass the class to check if parsing is acceptable.
     */
    public boolean canCreate(Class theClass) {
        try {
            Constructor c = getConstructor(theClass);
            return (c != null);
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * Discovers the constructor for the test class which will be used in
     * the instantiation of a new instance of the class.  This constructor
     * will be discovered through a call to
     * <tt>getConstructorArgTypes</tt>.  The returned constructor must be
     * callable through <tt>createTestArguments</tt>.
     *
     * @param theClass the class to parse for testing.
     * @return the constructor to create a new test instance with.
     * @throws NoSuchMethodException if the class does not have a
     *                               constructor with the arguments returned by
     *                               <tt>getConstructorArgTypes</tt>.
     * @see #getConstructorArgTypes(Class)
     * @see #createTest(Class, Method)
     * @see #createTestArguments(Class, Method, ImplFactory)
     */
    protected Constructor getConstructor(final Class theClass)
            throws NoSuchMethodException {
        return theClass.getConstructor(
                getConstructorArgTypes(theClass));
    }


    /**
     * Allows for pluggable constructor types.
     *
     * @param theClass the class to parse for testing.
     * @return the set of classes which define the constructor to extract.
     */
    protected Class[] getConstructorArgTypes(Class theClass) {
        /*
        return new Class[] {
            findClass( "java.lang.String" ),
            findClass( "net.sourceforge.groboutils.junit.v1.iftc.ImplFactory" )
        };
        */

        return new Class[]{
                String.class,
                ImplFactory.class
        };
    }


    /**
     * @param theClass the class to parse for testing.
     * @param m        the method that will be tested with the new class instance.
     */
    protected Object[] createTestArguments(Class theClass, Method method,
                                           ImplFactory implf) {
        return new Object[]{method.getName(), implf};
    }


    /**
     * Creates a new test class instance.
     *
     * @param theClass        the class to parse for testing.
     * @param constructorArgs arguments for the constructor retrieved through
     *                        <tt>getConstructor()</tt>.
     * @return the new Test.
     * @throws InstantiationException if a new instance could not be made
     *                                of the test class.
     * @throws NoSuchMethodException  if the constructor could not be found.
     * @see #getConstructor(Class)
     */
    protected Test createTest(Class theClass, Object[] constructorArgs)
            throws InstantiationException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException,
            ClassCastException {
        Constructor c = getConstructor(theClass);
        Test t;
        try {
            t = (Test) c.newInstance(constructorArgs);
        } catch (IllegalArgumentException iae) {
            StringBuffer args = new StringBuffer(
                    "Arguments didn't match for constructor ");
            args.append(c).append(" in class ").append(
                    theClass.getName()).append(".  Arguments = [");
            for (int i = 0; i < constructorArgs.length; ++i) {
                if (i > 0) {
                    args.append(", ");
                }
                args.append(constructorArgs[i].getClass().getName()).
                        append(" = '").
                        append(constructorArgs[i]).
                        append("'");
            }
            args.append("]: ").append(iae);
            throw new InstantiationException(args.toString());
        }
        return t;
    }



    
    
    /*
     * JDK 1.1 needs its own implementation of this, to avoid some hairy
     * situations.
    private static final Class findClass( String name )
    {
        try
        {
            return Class.forName( name );
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new IllegalStateException( cnfe.getMessage() );
        }
    }
     */

}

