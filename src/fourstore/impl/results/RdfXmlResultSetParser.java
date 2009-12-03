package fourstore.impl.results;

import fourstore.api.rdf.Literal;

import fourstore.api.results.ResultSet;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Title: <br/>
 * Description: <br/>
 * Company: Clark & Parsia, LLC. <http://clarkparsia.com><br/>
 * Created: Dec 1, 2009 3:46:25 PM<br/>
 *
 * @author Michael Grove <mike@clarkparsia.com><br/>
 */
public class RdfXmlResultSetParser extends DefaultHandler {
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

    public RdfXmlResultSetParser(ResultSetBuilder theBuilder) {
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

	public ResultSet resultSet() {
		return mResults.resultSet();
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

    public void characters(char[] theChars, int theStart, int theLength) {
        StringBuffer aBuffer = new StringBuffer();

        for (int i = 0; i < theLength; i++)
            aBuffer.append(theChars[theStart + i]);

        mElementString += aBuffer.toString();
    }
}
