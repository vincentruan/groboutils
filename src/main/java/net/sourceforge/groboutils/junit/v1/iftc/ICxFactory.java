/*
 * @(#)ICxFactory.java
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


/**
 * An ICxFactory is an extension of ImplFactory that has provisions for
 * cleaning up generated objects on test tear down.  Generated objects could
 * either have a finalize() method (not recommended, especially for tests),
 * or the factory could implement this interface to allow proper, timely
 * clean-up.
 * <p>
 * The clean-up is performed by <tt>InterfaceTestCase</tt> in the
 * <tt>tearDown()</tt> method.  If the hierarchy tests do not use this,
 * but instead use a variation of <tt>junit.framework.Test</tt>, then
 * they will need to implement their own tearDown() functionality.
 * <p>
 * A valid alternative is to use Mock-Objects
 * (<a href="http://www.mockobjects.org">www.mockobjects.org</a>), which would
 * not need to be cleaned up like "live" objects do.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:20 $
 * @since October 30, 2002
 */
public interface ICxFactory extends ImplFactory {
    /**
     * Allows the implementation to clean-up the instantiated object.
     * <tt>implObject</tt> is guaranteed to have been generated by this exact
     * factory.  Each object generated by this specific factory will be passed
     * to this method on the test's <tt>tearDown()</tt> method, in the
     * reverse order that they were created.
     *
     * @param implObject one of the objects created by this factory.
     * @throws Exception can be thrown when the deconstruction fails.
     *                   This will not disrupt the remaining objects' tear down
     *                   call.
     */
    public void tearDown(Object implObject) throws Exception;
}

