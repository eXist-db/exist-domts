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
package org.exist.dom.memtree;

import org.exist.Namespaces;
import org.exist.util.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.DOMTestIncompatibleException;
import org.w3c.domts.DOMTestLoadException;
import org.w3c.domts.DocumentBuilderSetting;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExistTestDocumentBuilderFactory extends DOMTestDocumentBuilderFactory {

    private final DOMImplementation domImplementation;
    private final SAXParserFactory saxParserFactory;

    public ExistTestDocumentBuilderFactory(final DocumentBuilderSetting[] settings)
            throws DOMTestIncompatibleException {
        super(settings);
        this.domImplementation = new MemtreeDOMImplementation();
        this.saxParserFactory = ExistSAXParserFactory.getSAXParserFactory();

        saxParserFactory.setNamespaceAware(true);

        // TODO(AR) set settings
//        if (settings != null) {
//            for (int i = 0; i < settings.length; i++) {
//                settings[i].applySetting(saxParserFactory);
//            }
//        }
    }

    @Override
    public DOMTestDocumentBuilderFactory newInstance(final DocumentBuilderSetting[] newSettings)
            throws DOMTestIncompatibleException {
        if (newSettings == null) {
            return this;
        }
        final DocumentBuilderSetting[] mergedSettings = mergeSettings(newSettings);
        return new ExistTestDocumentBuilderFactory(mergedSettings);
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return domImplementation;
    }

    @Override
    public Document load(final URL url) throws DOMTestLoadException {
        try {
            final SAXAdapter saxAdapter = new SAXAdapter();

            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(saxAdapter);
            xmlReader.setProperty(Namespaces.SAX_LEXICAL_HANDLER, saxAdapter);
            xmlReader.setEntityResolver(new LocalEntityResolver());

            try (final InputStream is = url.openStream()) {
                final Path path = Paths.get(url.toURI());
                final InputSource src = new InputSource(is);
                src.setSystemId(path.toUri().toString());

                xmlReader.parse(src);

                return saxAdapter.getDocument();
            }
        } catch (final ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
            throw new DOMTestLoadException(e);
        }
    }

    @Override
    public boolean hasFeature(final String feature, final String version) {
        return _hasFeature(feature, version);
    }

    private boolean _hasFeature(final String feature, final String version) {
        if("XML".equals(feature)) {
            return ("1.0".equals(version) || "2.0".equals(version));
        }

        try {
            return saxParserFactory.getFeature(feature);
        } catch (final ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isCoalescing() {
        return false;
    }

    @Override
    public boolean isExpandEntityReferences() {
        return false;
    }

    @Override
    public boolean isIgnoringElementContentWhitespace() {
        return false;
    }

    @Override
    public boolean isNamespaceAware() {
        return true;
    }

    @Override
    public boolean isValidating() {
        return false;
    }

    private class MemtreeDOMImplementation implements DOMImplementation {

        @Override
        public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException {
            return null; // TODO(AR) eXist does not yet implement this
        }

        @Override
        public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
            return new DocumentImpl(null, true);    // TODO(AR) is this okay?
        }

        @Override
        public Object getFeature(final String feature, final String version) {
            return _hasFeature(feature, version);
        }

        @Override
        public boolean hasFeature(final String feature, final String version) {
            return _hasFeature(feature, version);
        }
    }

    private class LocalEntityResolver implements EntityResolver2 {

        @Override
        public InputSource getExternalSubset(final String name, final String baseURI) throws SAXException, IOException {
            return null;
        }

        @Override
        public InputSource resolveEntity(final String name, final String publicId, final String baseURI, final String systemId) throws SAXException, IOException {
            try {
                final Path path = Paths.get(new URI(baseURI)).resolveSibling(systemId);
                final InputStream is = Files.newInputStream(path);

                final InputSource src = new InputSource(is);
                src.setSystemId(path.toUri().toString());

                return src;
            } catch (final URISyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
            return null;
        }
    }
}
