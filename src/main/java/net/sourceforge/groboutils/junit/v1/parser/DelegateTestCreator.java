/*
 * @(#)DelegateTestCreator.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Allows for an ordered set of TestCreator instances to be queried for
 * generating instances.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:20 $
 * @since November 4, 2002
 */
public class DelegateTestCreator implements ITestCreator {
    private static final Logger LOG = LoggerFactory.getLogger(DelegateTestCreator.class);

    private ITestCreator[] creators;


    /**
     * Create the delegation with an ordered array of creators.  The
     * creators are searched from index 0 to the last index for a valid
     * creator.
     */
    public DelegateTestCreator(ITestCreator[] tc) {
        if (tc == null || tc.length <= 0) {
            throw new IllegalArgumentException("no null args");
        }

        int len = tc.length;
        this.creators = new ITestCreator[len];
        for (int i = len; --i >= 0; ) {
            if (tc[i] == null) {
                throw new IllegalArgumentException("no null args");
            }
            this.creators[i] = tc[i];
        }
    }


    /**
     * Checks if the creator can be used on the given class.
     *
     * @param theClass the class to check if parsing is acceptable.
     * @return whether the creator can generate a test based on
     * <tt>theClass</tt>.
     */
    public boolean canCreate(Class theClass) {
        // order doesn't matter at this point
        for (int i = this.creators.length; --i >= 0; ) {
            if (this.creators[i].canCreate(theClass)) {
                return true;
            }
        }
        return false;
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
        // order matters here.
        for (int i = 0; i < this.creators.length; ++i) {
            ITestCreator tc = this.creators[i];
            try {
                if (tc.canCreate(theClass)) {
                    Test t = tc.createTest(theClass, method);
                    if (t != null) {
                        return t;
                    }
                }
            } catch (InstantiationException e) {
                LOG.info("Failed to create test with creator " + tc + ".", e);
            } catch (NoSuchMethodException e) {
                LOG.info("Failed to create test with creator " + tc + ".", e);
            } catch (InvocationTargetException e) {
                LOG.info("Failed to create test with creator " + tc + ".", e);
            } catch (IllegalAccessException e) {
                LOG.info("Failed to create test with creator " + tc + ".", e);
            } catch (ClassCastException e) {
                LOG.info("Failed to create test with creator " + tc + ".", e);
            }
        }

        // did not find a valid test creator.
        return null;
    }
}

