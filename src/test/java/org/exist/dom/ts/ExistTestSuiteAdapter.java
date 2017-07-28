/**
 * Copyright © 2017, eXist-db
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exist.dom.ts;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.exist.test.ExistEmbeddedServer;

import java.util.Enumeration;

/**
 * Simply shuts down the database after the tests have
 * executed.
 */
public class ExistTestSuiteAdapter  extends TestSuite {
    private final TestSuite testSuite;
    private final ExistEmbeddedServer existEmbeddedServer;

    public ExistTestSuiteAdapter(final ExistEmbeddedServer existEmbeddedServer, final TestSuite testSuite) {
        this.testSuite = testSuite;
        this.existEmbeddedServer = existEmbeddedServer;
    }

    @Override
    public void run(final TestResult result) {
        try {
            testSuite.run(result);
        } finally {
            // shutdown the database
            existEmbeddedServer.stopDb();
        }
    }

    @Override
    public void runTest(final Test test, final TestResult result) {
        testSuite.runTest(test, result);
    }

    @Override
    public void addTest(final Test test) {
        testSuite.addTest(test);
    }

    @Override
    public void addTestSuite(final Class<? extends TestCase> testClass) {
        testSuite.addTestSuite(testClass);
    }

    @Override
    public int countTestCases() {
        return testSuite.countTestCases();
    }

    @Override
    public String getName() {
        return testSuite.getName();
    }

    @Override
    public void setName(final String name) {
        testSuite.setName(name);
    }

    @Override
    public Test testAt(final int index) {
        return testSuite.testAt(index);
    }

    @Override
    public int testCount() {
        return testSuite.testCount();
    }

    @Override
    public Enumeration<Test> tests() {
        return testSuite.tests();
    }

    @Override
    public String toString() {
        return testSuite.toString();
    }
}
