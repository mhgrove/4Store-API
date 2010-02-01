/*
 * Copyright (c) 2005-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarkparsia.fourstore.impl.results;

import org.openrdf.model.Literal;
import org.openrdf.query.BindingSet;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;
import java.util.Collection;

/**
 * <p>Sax implementation to parse a SPARQL XML result set into a result set.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class SparqlXmlResultSetParser extends DefaultHandler {
	private ResultSetBuilder mResults;

    private String mElementString;
    private String mBindingName;
    private String mLang;
    private String mDatatype;

    private static final String RESULTS = "http://www.w3.org/2005/sparql-results#results";
    private static final String RESULT = "http://www.w3.org/2005/sparql-results#result";
    private static final String BINDING = "http://www.w3.org/2005/sparql-results#binding";
    private static final String TYPE_LITERAL = "http://www.w3.org/2005/sparql-results#literal";
    private static final String TYPE_BNODE = "http://www.w3.org/2005/sparql-results#bnode";
    private static final String TYPE_URI = "http://www.w3.org/2005/sparql-results#uri";
    private static final String NAME = "name";
    private static final String LANG = "xml:lang";
    private static final String DATATYPE = "datatype";

    public SparqlXmlResultSetParser(ResultSetBuilder theBuilder) {
		mResults = theBuilder;
	}

	/**
	 * @inheritDoc
	 */
	@Override
    public void startDocument() {
        mResults.startResultSet();
    }

	/**
	 * @inheritDoc
	 */
	@Override
    public void endDocument() {
		mResults.endResultSet();
    }

	public List<String> bindingNames() {
		return mResults.bindingNames();
	}

	public Collection<BindingSet> bindingSet() {
		return mResults.bindingSet();
	}

	/**
	 * @inheritDoc
	 */
	@Override
    public void startElement(String theURI, String theLocalName, String theQName, Attributes theAttrs) {
        String aURI = theURI + theLocalName;

        if (aURI.equals(BINDING)) {
            mBindingName = theAttrs.getValue(NAME).replaceAll("\\?","");
        }
        else if (aURI.equals(TYPE_LITERAL)) {
            mLang = theAttrs.getValue(LANG);
            mDatatype = theAttrs.getValue(DATATYPE);
            mElementString = "";
        }
        else if (aURI.equals(TYPE_URI)) {
            mElementString = "";
        }
        else if (aURI.equals(TYPE_BNODE)) {
            mElementString = "";
        }
        else if (aURI.equals(RESULT)) {
			mResults.startBinding();
        }
    }

	@Override
    public void endElement(String theURI, String theLocalName, String theQName) {
        String aURI = theURI + theLocalName;

        if (aURI.equals(RESULT)) {
			mResults.endBinding();
        }
        else if (aURI.equals(TYPE_URI)) {
			mResults.addToBinding(mBindingName, mResults.getValueFactory().createURI(mElementString));
        }
        else if (aURI.equals(TYPE_LITERAL)) {
            Literal aLiteral = null;

            if (mLang == null && mDatatype == null) {
                aLiteral = mResults.getValueFactory().createLiteral(mElementString);
            }
            else if (mLang != null) {
                aLiteral = mResults.getValueFactory().createLiteral(mElementString, mLang);
            }
            else if (mDatatype != null) {
                aLiteral = mResults.getValueFactory().createLiteral(mElementString, mResults.getValueFactory().createURI(mDatatype));
            }

            mResults.addToBinding(mBindingName, aLiteral);
        }
        else if (aURI.equals(TYPE_BNODE)) {
            mResults.addToBinding(mBindingName, mResults.getValueFactory().createBNode(mElementString));
        }
    }

	@Override
    public void characters(char[] theChars, int theStart, int theLength) {
        StringBuffer aBuffer = new StringBuffer();

        for (int i = 0; i < theLength; i++)
            aBuffer.append(theChars[theStart + i]);

        mElementString += aBuffer.toString();
    }
}
