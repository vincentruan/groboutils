/*
 * @(#)ImplFactory.java
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
 * Allows for tests to be written on interfaces or abstract classes, by creating
 * a specific instance of the interface or abstract class.  Test classes will
 * invoke this method to retrieve the specific instance for their tests.
 * <p>
 * Since October 21, 2002, the <tt>createImplObject()</tt> method can now
 * throw any exception.  Some construction implementations throw all kinds
 * of errors, such as <tt>IOException</tt> or <tt>SQLException</tt>.  This
 * makes the task of creating factories a bit easier, since we no longer
 * need to worry about proper try/catch blocks.
 *
 * @author Matt Albrecht <a href="mailto:groboclown@users.sourceforge.net">groboclown@users.sourceforge.net</a>
 * @version $Date: 2003/02/10 22:52:20 $
 * @since March 2, 2002
 */
public interface ImplFactory {
    /**
     * Create a new instance of the interface type for testing through
     * an InterfaceTest.
     * <p>
     * As of 21-Oct-2002, this method can raise any exception, and it will be
     * correctly caught and reported as a failure by the
     * <tt>InterfaceTestCase.createImplObject()</tt> method, so that the
     * creation method can simplify its logic, and add any kind of
     * initialization without having to worry about the correct way to
     * handle exceptions.
     *
     * @return a new instance of the expected type that the corresponding
     * <tt>InterfaceTestCase</tt>(s) cover.
     * @throws Exception thrown under any unexpected condition that
     *                   results in the failure to properly create the instance.
     * @since October 21, 2002: Since this date, this method can now throw
     * exceptions to make creation a bit easier on us.
     */
    public Object createImplObject() throws Exception;


    /**
     * All ImplFactory instances should specify a distinguishable name
     * to help in debugging failed tests due to a particular factory's
     * instance setup.
     *
     * @return a distinguishable name for the factory.
     * @see CxFactory CxFactory: a helper that simplifies this task for us.
     */
    public String toString();
}

