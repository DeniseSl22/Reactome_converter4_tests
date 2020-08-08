/* Copyright (C) 2020  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the <organization> nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
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
package org.wikipathways.covid;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import nl.unimaas.bigcat.wikipathways.curator.assertions.*;
import nl.unimaas.bigcat.wikipathways.curator.tests.*;
import nl.unimaas.bigcat.wikipathways.curator.SPARQLHelper;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class CheckRDF {

    public static void main(String[] args) throws Exception {
        String gpmlFile = args[0];
        String wpid     = gpmlFile.substring(9,gpmlFile.indexOf(".ttl"));
        System.out.println("# WikiPathways " + wpid + "\n");
        System.out.println("GPML file: [" + gpmlFile + "](../" + gpmlFile + ")\n");
        List<IAssertion> assertions = new ArrayList<IAssertion>();
        Model loadedData = ModelFactory.createDefaultModel();
        loadedData.read(new FileInputStream(new File(gpmlFile)), "", "TURTLE");

        SPARQLHelper helper = new SPARQLHelper(loadedData);
        assertions.addAll(GeneTests.all(helper));
        assertions.addAll(ReferencesTests.all(helper));
        assertions.addAll(InteractionTests.all(helper));

        System.out.println("## Tests");

        List<IAssertion> failedAssertions = new ArrayList<IAssertion>();
        int testClasses = 0;
        int tests = 0;
        String currentTestClass = "";
        String currentTest = "";
        String message = "";
        String errors = "";
        for (IAssertion assertion : assertions) {
            if (assertion.getTestClass() != currentTestClass) {
                currentTestClass = assertion.getTestClass();
                currentTest = "";
                testClasses++;
                if (!errors.isEmpty()) message += "\n" + errors;
                if (!message.isEmpty()) System.out.println(message);
                message = "";
                System.out.println("\n* " + currentTestClass);
            }
            if (assertion.getTest() != currentTest) {
                currentTest = assertion.getTest();
                if (!message.isEmpty()) System.out.println(message);
                message = "    * " + currentTest + ": ";
                errors = "";
                tests++;
            }
            if (assertion instanceof AssertEquals) {
                AssertEquals typedAssertion = (AssertEquals)assertion;
                if (!typedAssertion.getExpectedValue().equals(typedAssertion.getValue())) {
                   message += "x";
                   errors += "        * " + typedAssertion.getMessage();
                   failedAssertions.add(assertion);
                } else {
                    message += ".";
                }
            } else if (assertion instanceof AssertNotNull) {
                AssertNotNull typedAssertion = (AssertNotNull)assertion;
                if (typedAssertion.getValue() == null) {
                   message += "x";
                   errors += "            * Unexpected null found";
                   failedAssertions.add(assertion);
                } else {
                    message += ".";
                }
            } else {
                errors += "        * Unrecognized assertion type: " + assertion.getClass().getName();
                failedAssertions.add(assertion);
            }
        }
        if (!message.isEmpty()) System.out.println(message);
        System.out.println("\n## Summary\n");
        System.out.println("* Number of test classes: " + testClasses);
        System.out.println("* Number of tests: " + tests);
        System.out.println("* Number of assertions: " + assertions.size());
        System.out.println("* Number of fails: " + failedAssertions.size());

        System.out.println("\n## Fails\n");
        for (IAssertion assertion : failedAssertions) {
            System.out.println("## " + assertion.getTestClass() + "." + assertion.getTest());
            System.out.println("\n```\n" + assertion.getMessage() + "\n```");
        }        
    }

}
