/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.ssf.query;

import java.io.PrintStream;
import sanchay.corpus.ssf.SSFStory;

/**
 *
 * @author anil
 */
public class QuerySourceDestination {

    protected String format = "ssf";
    protected String location;
    protected String charset = "UTF-8";

    protected SSFStory document;
    protected PrintStream stream;

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return the document
     */
    public SSFStory getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(SSFStory document) {
        this.document = document;
    }

    /**
     * @return the stream
     */
    public PrintStream getStream() {
        return stream;
    }

    /**
     * @param stream the stream to set
     */
    public void setStream(PrintStream stream) {
        this.stream = stream;
    }

}
