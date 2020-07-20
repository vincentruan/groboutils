/*
 * @(#)ITestCreator.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Interface that can create test objects based on a class and a method from
 * within that class, using a specific method.  Also provides means to check
 * the class object to see if the implementation can instantiate the test
 * class.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:21 $
 * @since November 3, 2002
 */
public interface ITestCreator {
    /**
     * Creates a new test, based on the given class and method of the
     * class.
     *
     * @param theClass the class to parse for testing.
     * @param m        the method that will be tested with the new class instance.
     * @return the generated test, or <tt>null</tt> if the test could not
     * be created.
     * @throws InstantiationException if there was a problem creating
     *                                the class.
     * @throws NoSuchMethodException  if the method does not exist in the
     *                                class.
     */
    public Test createTest(Class theClass, Method method)
            throws InstantiationException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException,
            ClassCastException;

    /**
     * Checks if the creator can be used on the given class.
     *
     * @param theClass the class to check if parsing is acceptable.
     * @return whether the creator can generate a test based on
     * <tt>theClass</tt>.
     */
    public boolean canCreate(Class theClass);
}

