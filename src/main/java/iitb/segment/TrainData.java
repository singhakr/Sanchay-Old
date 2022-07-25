package iitb.segment;
import iitb.crf.DataIter;
import iitb.crf.DataSequence;
import iitb.segment.*;
/**
 *
 * @author Sunita Sarawagi
 *
 */ 

public interface TrainData extends DataIter {
    int size();   // number of training records
    void startScan(); // start scanning the training data
    boolean hasMoreRecords(); 
    public TrainRecord nextRecord();
    boolean hasNext(); 
    public DataSequence next();
};

