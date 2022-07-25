/* ShbdanjaliDict.java
 * Main Class which creates the gui.
 *
 * */


package sanchay.resources.shabdanjali;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
//import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sanchay.GlobalProperties;
import sanchay.tree.*;

/**
 *
 *  @author Bharat Ram Ambati
 */


public class ShabdanjaliDict extends JPanel 
implements ActionListener {

	private int newNodeSuffix = 1;
	private static String ADD_COMMAND = GlobalProperties.getIntlString("add");
	private static String REMOVE_COMMAND = GlobalProperties.getIntlString("remove");
	private static String SAVE_COMMAND = GlobalProperties.getIntlString("save");
	private static String RETRIVE_COMMAND = GlobalProperties.getIntlString("retrive");
    private static String PREV_COMMAND = GlobalProperties.getIntlString("previous");
    private static String NEXT_COMMAND = GlobalProperties.getIntlString("next");
    private int matchIndex=0,matchCount=0;
    private String[] matchWordsList=null;

    private JPanel topPanel;
    private JPanel bottomPanel;

	private DynamicTree treePanel;
	private JTextField wordTextField;
	private JButton retriveButton,prevButton,nextButton,addButton,removeButton,saveButton;
	private Dictionary shabdanjali;
	private JLabel textLabel;

	public ShabdanjaliDict() throws IOException{
		super(new BorderLayout());
        prepareDict();
        inits();
    }

    public ShabdanjaliDict(boolean min) throws IOException{
		super(new BorderLayout());
        prepareDict();
        inits();
        setMinMode(true);
    }

    public void setMinMode(boolean min)
    {
    if(min)
        {
            topPanel.setVisible(false);
        }
    }

    public void minimalMode()
    {

    }
    public void inits()
    {
		//Create the components.
		treePanel = new DynamicTree();
		
		textLabel = new JLabel(GlobalProperties.getIntlString("Enter_the_word"));
		wordTextField = new JTextField("");

		retriveButton = new JButton(GlobalProperties.getIntlString("Retrive"));
		retriveButton.setActionCommand(RETRIVE_COMMAND);
		retriveButton.addActionListener(this);

		addButton = new JButton(GlobalProperties.getIntlString("Add"));
		addButton.setActionCommand(ADD_COMMAND);
		addButton.addActionListener(this);
        //addButton.setDisplayedMnemonic("A");
        addButton.setMnemonic(KeyEvent.VK_A);

		removeButton = new JButton(GlobalProperties.getIntlString("Remove"));
		removeButton.setActionCommand(REMOVE_COMMAND);
		removeButton.addActionListener(this);


        prevButton = new JButton(GlobalProperties.getIntlString("Previous"));
		prevButton.setActionCommand(PREV_COMMAND);
		prevButton.addActionListener(this);
        prevButton.setMnemonic(KeyEvent.VK_P);

        nextButton = new JButton(GlobalProperties.getIntlString("Next"));
		nextButton.setActionCommand(NEXT_COMMAND);
		nextButton.addActionListener(this);
        nextButton.setMnemonic(KeyEvent.VK_F3);
        //nextButton.setMnemonic(KeyEvent.VK_N);

		saveButton = new JButton(GlobalProperties.getIntlString("Save"));
		saveButton.setActionCommand(SAVE_COMMAND);
		saveButton.addActionListener(this);


        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

		//Lay everything out.

		topPanel = new JPanel(new GridLayout(0,3));
		topPanel.add(textLabel);
		topPanel.add(wordTextField);
		topPanel.add(retriveButton);
		add(topPanel, BorderLayout.NORTH);

		treePanel.setPreferredSize(new Dimension(600, 450));
		add(treePanel, BorderLayout.CENTER);

		bottomPanel = new JPanel(new GridLayout(0,5));
		bottomPanel.add(addButton);
		bottomPanel.add(removeButton);
        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
		bottomPanel.add(saveButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

    public void prepareDict(String dictFile) throws IOException
    {
        shabdanjali = new Dictionary();
        shabdanjali.makeDictionary(dictFile);
    }

    public void prepareDict() throws IOException
    {
        shabdanjali = new Dictionary();
//        shabdanjali.makeDictionary("shabdanjali.unicode");
    }
    
    public String[] getEntryList(String word)
    {
        SortedMap dict = shabdanjali.getDictionary();
        Iterator iterator = dict.keySet().iterator();
        String match = "";
        String[] matchWordsList = null;
        int index=0;
        //List matchWordsList = new ArrayList();
        for (Object key : dict.keySet()) {
            String keyW = (String) key;
            Pattern p = Pattern.compile(word);
            Matcher m = p.matcher(keyW);
            if(m.find())
            {
                match+=key+"\t";
            }
        }

        /*
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            Pattern p = Pattern.compile(word);
            Matcher m = p.matcher(key);
            if(m.find())
            {
                match+=key+" ";
                //matchWordsList[index]=key;
                //index++;
                //matchWordsList.add(key);
            }
        }
         * */
        match=match.trim();
        matchWordsList=match.split("\t");
        System.out.println(word + "\tmatching_String_is_-->" + match);
        return matchWordsList;
    }
    
    public boolean findEntry(String word, boolean regExp)
    {
        if(regExp==true)
        {
            SortedMap dict = shabdanjali.getDictionary();
            Iterator iterator = dict.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Pattern p = Pattern.compile(word);
                Matcher m = p.matcher(key);
                if(m.find())
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            return false;
        }
        else
        {
            List<DictEntry> entryList = new ArrayList<DictEntry>();
            entryList = shabdanjali.getDictionaryitem(word);
            if (entryList == null) {
                return false;
                //System.out.println("No entries in the dictionary");
            }
            else {
                return true;
            }
        }
    }

	public void populateTree(String word) {

		treePanel.clear();
		treePanel.setRootName(word);

		List<DictEntry> entryList = new ArrayList<DictEntry>();
		entryList = shabdanjali.getDictionaryitem(word);
		DictEntry dictEntry = new DictEntry();
		if (entryList == null) {
			System.out.println(GlobalProperties.getIntlString("No_entries_in_the_dictionary"));
		}
		else {
			String pos, meaning;
			SanchayMutableTreeNode posNode, meanNode;
			for (int i = 0 ; i< entryList.size() ; i++) {
				dictEntry = entryList.get(i);
				pos = dictEntry.getPOS();
				posNode = treePanel.addObject(null, GlobalProperties.getIntlString("POS:_") + pos);
				for (String mean : dictEntry.getMeanings()) {
					meaning = mean;
					meanNode = treePanel.addObject(posNode,GlobalProperties.getIntlString("Meaning:_") + meaning);
					for (String example : dictEntry.getExamples(mean)) {
						treePanel.addObject(meanNode, GlobalProperties.getIntlString("Eg:_") + example);
					}
				}
			}
		}
		treePanel.expandall();
	}

    public void writeEntryToFile(String word,File inFile)
    {
        shabdanjali.writeEntry(word,inFile);
    }

    public DynamicTree getTreePanel()
    {
        return treePanel;
    }

    public void retriveButtonClicked(String word)
    {
            matchWordsList = getEntryList(word);
            matchCount = matchWordsList.length;
            if(matchCount > 0)
            {
                String keyW = matchWordsList[matchIndex];
                populateTree(keyW);
            }
            prevButton.setEnabled(false);
            if(matchCount>1)
            {
                nextButton.setEnabled(true);
            }
			System.out.println(GlobalProperties.getIntlString("Word_is:_") + word);
    }

    public void nextButtonClicked()
    {
        matchIndex++;
        if(matchIndex>=0 && matchIndex<matchCount)
        {
                String keyW = matchWordsList[matchIndex];
                populateTree(keyW);
        }
        if(matchIndex==0)
            prevButton.setEnabled(false);
        else
        {
            prevButton.setEnabled(true);
        }
        if(matchIndex < matchCount)
            nextButton.setEnabled(true);
        else
        {
            nextButton.setEnabled(false);
        }
    }

    public void prevButtonClicked()
    {
        matchIndex--;
        if(matchIndex>=0 && matchIndex<matchCount)
        {
                String keyW = matchWordsList[matchIndex];
                populateTree(keyW);
        }
        if(matchIndex==0)
            prevButton.setEnabled(false);
        else
        {
            prevButton.setEnabled(true);
        }
        if(matchIndex < matchCount)
            nextButton.setEnabled(true);
        else
        {
            nextButton.setEnabled(false);
        }
    }


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (ADD_COMMAND.equals(command)) {
			//Add button clicked
			treePanel.addObject(GlobalProperties.getIntlString("New_Node_") + newNodeSuffix++);
		} else if (REMOVE_COMMAND.equals(command)) {
			//Remove button clicked
			treePanel.removeCurrentNode();
		} else if (SAVE_COMMAND.equals(command)) {
			//Save button clicked.
			String word = wordTextField.getText();
			shabdanjali.removeEntry(word);
			shabdanjali.addEntry(word,treePanel.save());
            shabdanjali.writeFile();
		} else if (RETRIVE_COMMAND.equals(command)) {
			//Retrive button clicked.
            String word = wordTextField.getText();
            retriveButtonClicked(word);
		}
        else if (PREV_COMMAND.equals(command))
        {
            prevButtonClicked();
        }
        else if (NEXT_COMMAND.equals(command)) {
			//Next Button clicked.
            nextButtonClicked();
		}

	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() throws IOException{
		//Create and set up the window.
		JFrame frame = new JFrame(GlobalProperties.getIntlString("Shabdanjali_English2Hindi_Dictionary"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		ShabdanjaliDict newContentPane = new ShabdanjaliDict();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				try {
				createAndShowGUI();
				}
				catch (IOException e) {
				}
				}
				});
	}
}
