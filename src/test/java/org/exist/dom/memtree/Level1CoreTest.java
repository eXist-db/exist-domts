package org.exist.dom.persistent;

import junit.framework.TestSuite;
import org.exist.dom.ts.ExistTestSuiteAdapter;
import org.exist.test.ExistEmbeddedServer;
import org.exist.xmldb.XmldbURI;
import org.w3c.domts.*;

public class Level1CoreTest extends TestSuite {

    private static ExistEmbeddedServer existEmbeddedServer =
            new ExistEmbeddedServer(true, true);

    private final static XmldbURI TEST_COLLECTION = XmldbURI.ROOT_COLLECTION_URI.append("domts/level1/core");

    public static TestSuite suite() throws Exception {

        // start the database
        existEmbeddedServer.startDb();

        final DocumentBuilderSetting[] settings = JAXPDOMTestDocumentBuilderFactory.getConfiguration1();
        final DOMTestDocumentBuilderFactory factory =
                new ExistTestDocumentBuilderFactory(existEmbeddedServer.getBrokerPool(), TEST_COLLECTION, settings);

        final DOMTestSuite core1Tests = new org.w3c.domts.level1.core.alltests(factory);

        return new ExistTestSuiteAdapter(existEmbeddedServer, new JUnitTestSuiteAdapter(core1Tests));
    }
}
