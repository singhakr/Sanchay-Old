/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.xml.diff;

import com.topologi.diffx.DiffXException;
import com.topologi.diffx.algorithm.DiffXAlgorithm;
import com.topologi.diffx.algorithm.DiffXFitopsy;
import com.topologi.diffx.config.DiffXConfig;
import com.topologi.diffx.format.SmartXMLFormatter;
import com.topologi.diffx.load.DOMRecorder;
import com.topologi.diffx.load.Recorder;
import com.topologi.diffx.load.SAXRecorder;
import com.topologi.diffx.sequence.EventSequence;
import com.topologi.diffx.sequence.SequenceSlicer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author anil
 */
public class XMLDiff {
// equivalent methods -------------------------------------------------------------------

  /**
   * Returns <code>true</code> if the two specified files are XML equivalent by looking at the
   * sequance SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(File xml1, File xml2)
      throws DiffXException, IOException {
    Recorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(xml1);
    EventSequence seq1 = recorder.process(xml2);
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified inputstream are equivalent by looking at the
   * sequance SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(InputStream xml1, InputStream xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(new InputSource(xml1));
    EventSequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

  /**
   * Returns <code>true</code> if the two specified readers are equivalent by looking at the
   * sequance SAX events reported an XML reader.
   *
   * @param xml1 The first XML stream to compare.
   * @param xml2 The first XML stream to compare.
   *
   * @return <code>true</code> If the XML are considered equivalent;
   *         <code>false</code> otherwise.
   *
   * @throws DiffXException If a DiffX exception is reported by the recorders.
   * @throws IOException    Should an I/O exception occur.
   */
  public static boolean equivalent(Reader xml1, Reader xml2)
      throws DiffXException, IOException {
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq0 = recorder.process(new InputSource(xml1));
    EventSequence seq1 = recorder.process(new InputSource(xml2));
    return seq0.equals(seq1);
  }

// diff methods -------------------------------------------------------------------------

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1   The first XML node to compare.
   * @param xml2   The first XML node to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Node xml1, Node xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the events from the XML
    DOMRecorder loader = new DOMRecorder();
    if (config != null) loader.setConfig(config);
    EventSequence seq1 = loader.process(xml1);
    EventSequence seq2 = loader.process(xml2);
    // start slicing
    diff(seq1, seq2, out, config);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1   The first XML reader to compare.
   * @param xml2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    if (config != null) recorder.setConfig(config);
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, config);
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML reader to compare.
   * @param xml2 The first XML reader to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(Reader xml1, Reader xml2, Writer out)
      throws DiffXException, IOException {
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    // start slicing
    diff(seq1, seq2, out, new DiffXConfig());
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param xml1 The first XML input stream to compare.
   * @param xml2 The first XML input stream to compare.
   * @param out  Where the output goes
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  public static void diff(InputStream xml1, InputStream xml2, OutputStream out)
      throws DiffXException, IOException {
    // records the events from the XML
    SAXRecorder recorder = new SAXRecorder();
    EventSequence seq1 = recorder.process(new InputSource(xml1));
    EventSequence seq2 = recorder.process(new InputSource(xml2));
    diff(seq1, seq2, new OutputStreamWriter(out), new DiffXConfig());
  }

  /**
   * Compares the two specified xml files and prints the diff onto the given writer.
   *
   * @param seq1   The first XML reader to compare.
   * @param seq2   The first XML reader to compare.
   * @param out    Where the output goes.
   * @param config The DiffX configuration to use.
   *
   * @throws DiffXException Should a Diff-X exception occur.
   * @throws IOException    Should an I/O exception occur.
   */
  private static void diff(EventSequence seq1, EventSequence seq2, Writer out, DiffXConfig config)
      throws DiffXException, IOException {
    SmartXMLFormatter formatter = new SmartXMLFormatter(out);
    formatter.declarePrefixMapping(seq1.getPrefixMapping());
    formatter.declarePrefixMapping(seq2.getPrefixMapping());

    if (config != null) formatter.setConfig(config);
    SequenceSlicer slicer = new SequenceSlicer(seq1, seq2);
    slicer.slice();
    slicer.formatStart(formatter);
    DiffXAlgorithm df = new DiffXFitopsy(seq1, seq2);
    df.process(formatter);
    slicer.formatEnd(formatter);
  }
}
