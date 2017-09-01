/*
 * @(#)JUnit3_8Creator.java
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Emulates the JUnit 3.8+ construction mechanism.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:21 $
 * @since November 3, 2002
 */
public class JUnit3_8Creator implements ITestCreator {
    /**
     * Checks if the creator can be used on the given class.
     *
     * @param theClass the class to check if parsing is acceptable.
     */
    public boolean canCreate(Class theClass) {
        if (!TestCase.class.isAssignableFrom(theClass)) {
            return false;
        }

        try {
            Constructor c = theClass.getConstructor(new Class[0]);
            return (c != null);
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * Creates a new test, based on the given class and method of the
     * class.
     *
     * @param theClass the class to parse for testing.
     * @param m        the method that will be tested with the new class instance.
     * @throws InstantiationException if there was a problem creating
     *                                the class.
     * @throws NoSuchMethodException  if the method does not exist in the
     *                                class.
     */
    public Test createTest(Class theClass, Method method)
            throws InstantiationException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException,
            ClassCastException {
        TestCase tc;
        try {
            tc = (TestCase) theClass.newInstance();
        } catch (IllegalArgumentException iae) {
            StringBuffer args = new StringBuffer(
                    "Arguments didn't match for default constructor in class "
            );
            args.append(theClass.getName()).append(".");
            throw new InstantiationException(args.toString());
        }
        tc.setName(method.getName());
        return tc;
    }
}

