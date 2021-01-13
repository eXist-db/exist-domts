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
package org.exist.dom.persistent;

import org.exist.EXistException;
import org.exist.collections.Collection;
import org.exist.collections.IndexInfo;
import org.exist.security.PermissionDeniedException;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.util.*;
import org.exist.xmldb.XmldbURI;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.domts.*;
import org.xml.sax.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import static org.apache.xerces.impl.Constants.*;
import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

public class ExistTestDocumentBuilderFactory extends DOMTestDocumentBuilderFactory {

    private final BrokerPool brokerPool;
    private final MimeTable mimeTable;
    private final XmldbURI collectionUri;
    private final DOMImplementation domImplementation;

    public ExistTestDocumentBuilderFactory(final BrokerPool brokerPool, final XmldbURI collectionUri, final DocumentBuilderSetting[] settings)
            throws DOMTestIncompatibleException {
        super(settings);
        this.brokerPool = brokerPool;
        this.mimeTable = MimeTable.getInstance();
        this.collectionUri = collectionUri;
        this.domImplementation = new PersistentDOMImplementation();

        //settings are set in #load(url)
    }

    @Override
    public DOMTestDocumentBuilderFactory newInstance(final DocumentBuilderSetting[] newSettings)
            throws DOMTestIncompatibleException {
        if (newSettings == null) {
            return this;
        }
        final DocumentBuilderSetting[] mergedSettings = mergeSettings(newSettings);
        return new ExistTestDocumentBuilderFactory(brokerPool, collectionUri, mergedSettings);
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return domImplementation;
    }

    @Override
    public Document load(final URL url) throws DOMTestLoadException {
        try (final DBBroker broker = brokerPool.get(Optional.of(brokerPool.getSecurityManager().getSystemSubject()));
             final Txn transaction = brokerPool.getTransactionManager().beginTransaction()) {

            final Collection collection = broker.getOrCreateCollection(transaction, collectionUri);
            broker.saveCollection(transaction, collection);

            final String fileName = FileUtils.fileName(Paths.get(url.toURI()));
            final XmldbURI name = XmldbURI.create(fileName);
            final MimeType mimeType = mimeTable.getContentTypeFor(fileName);


            final Document document;
            if(mimeType.isXMLType()) {
                // xml document
                try(final InputStream vis = url.openStream()) {
                    final InputSource visrc = new InputSource(vis);
                    visrc.setSystemId(fileName);

                    XMLReader reader = null;
                    try {
                        reader = broker.getBrokerPool().getParserPool().borrowXMLReader();
                        setSettings(reader);

                        final IndexInfo indexInfo = collection.validateXMLResource(transaction, broker, name, visrc);

                        try (final InputStream sis = url.openStream()) {
                            final InputSource sisrc = new InputSource(sis);
                            sisrc.setSystemId(fileName);

                            collection.store(transaction, broker, indexInfo, sisrc);

                            document = broker.getXMLResource(collectionUri.append(name));
                        }
                    } finally {
                        if(reader != null) {
                            broker.getBrokerPool().getParserPool().returnXMLReader(reader);
                        }
                    }
                }

            } else {
                // binary document
                try (final InputStream is = url.openStream()) {
                    document = collection.addBinaryResource(transaction, broker, name, is, mimeType.getName(), -1);
                }
            }

            transaction.commit();

            return document;

        } catch (final EXistException | LockException | PermissionDeniedException | SAXException | IOException | URISyntaxException | DOMTestIncompatibleException e) {
            throw new DOMTestLoadException(e);
        }
    }

    private void setSettings(final XMLReader reader) throws SAXNotRecognizedException, SAXNotSupportedException, DOMTestIncompatibleException {
        for(final DocumentBuilderSetting setting : getActualSettings()) {

            switch(setting.getProperty()) {
                case "coalescing":
                    //TODO(AR) need to do something here
//                    reader.setFeature(XERCES_FEATURE_PREFIX + CREATE_CDATA_NODES_FEATURE, setting.getValue());
                    break;

                case "expandEntityReferences":
                    //TODO(AR) need to do something here
//                    reader.setFeature(XERCES_FEATURE_PREFIX + CREATE_ENTITY_REF_NODES_FEATURE, setting.getValue());
                    break;

                case "ignoringElementContentWhitespace":
                    //TODO(AR) need to do something here
//                    reader.setFeature(XERCES_FEATURE_PREFIX + INCLUDE_IGNORABLE_WHITESPACE, !setting.getValue());
                    break;

                case "namespaceAware":
                    reader.setFeature(SAX_FEATURE_PREFIX + NAMESPACES_FEATURE, setting.getValue());
                    break;

                case "validating":
                    reader.setFeature(SAX_FEATURE_PREFIX + VALIDATION_FEATURE, setting.getValue());
                    break;

                case "signed":
                    if (!setting.getValue()) {
                        throw new DOMTestIncompatibleException(null, DocumentBuilderSetting.notSigned);
                    }
                    break;

                case "hasNullString":
                    if (!setting.getValue()) {
                        throw new DOMTestIncompatibleException(null, DocumentBuilderSetting.notHasNullString);
                    }
                    break;

                case "schemaValidating":
                    if(setting.getValue()) {
                        reader.setFeature(SAX_FEATURE_PREFIX + NAMESPACES_FEATURE, true);
                        reader.setFeature(SAX_FEATURE_PREFIX + VALIDATION_FEATURE, true);
                        reader.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

                        XMLReaderObjectFactory.setReaderValidationMode(XMLReaderObjectFactory.VALIDATION_SETTING.ENABLED, reader);
                    } else {
                        reader.setProperty(JAXP_SCHEMA_LANGUAGE, "http://www.w3.org/TR/REC-xml");
                    }

                    break;

                case "ignoringComments":
                    reader.setFeature(SAX_FEATURE_PREFIX + INCLUDE_COMMENTS_FEATURE, !setting.getValue());
                    break;
            }
        }
    }

    @Override
    public boolean hasFeature(final String feature, final String version) {
        return _hasFeature(feature, version);
    }

    private boolean _hasFeature(final String feature, final String version) {
        if("Core".equalsIgnoreCase(feature) || "XML".equalsIgnoreCase(feature)) {
            return (version == null || version.isEmpty() || "1.0".equals(version) || "2.0".equals(version) || "3.0".equals(version));
        }

        try (final DBBroker broker = brokerPool.get(Optional.of(brokerPool.getSecurityManager().getSystemSubject()))) {
            final XMLReader reader = broker.getBrokerPool().getParserPool().borrowXMLReader();

            try {
                return reader.getFeature(feature);
            } finally {
                broker.getBrokerPool().getParserPool().returnXMLReader(reader);
            }
        } catch (final EXistException | SAXException e) {
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

    private class PersistentDOMImplementation implements DOMImplementation {

        @Override
        public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException {
            return new DocumentTypeImpl(qualifiedName, publicId, systemId); // TODO(AR) is this okay?
        }

        @Override
        public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
            return null; // TODO(AR) eXist does not yet implement this
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
}
