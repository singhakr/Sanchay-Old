/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.discourse;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class DiscourseAnnotationInterfaceAction extends AbstractAction {
    protected DiscourseArgOptionalJPanel currentAnnotationJPanel;

    public static final int FREEZE =0;
    public static final int SELECT_LANGUAGE = 1;
    public static final int SELECT_INPUT_METHOD = 2;
    public static final int NEW_ANNOTATED = 3;
    public static final int OPEN_ANNOTATED = 4;
    public static final int CLOSE_ANNOTATED = 5;
    public static final int SAVE_ANNOTATED = 6;
    public static final int SAVE_AS_ANNOTATED = 7;
    public static final int CONNECTIVE_TYPE = 8;
    public static final int HEAD_CON = 9;
    public static final int PART_OF_CONNECTIVE = 10;
    public static final int ADD_INFO_CONNECTIVE = 11;
//    public static final int IMPLICIT_CASE = 10;
//    public static final int ENTREL_CASE = 11;
//    public static final int NOREL_CASE = 12;
//    public static final int SOURCE_CON = 13;
//    public static final int FACTUALITY_CON = 14;
//    public static final int POLARITY_CON = 15;
    public static final int _BASIC_ACTIONS_ = 12;
    
    
    public static final int SET_ARG1 = 0;
    public static final int PART_OF_ARG1 = 1;
//    public static final int SET_OPT1 = 2;
    public static final int ADD_INFO_ARG1 = 2;
    public static final int ADD_SUP1 = 3;
//    public static final int SOURCE_ARG1 = 4;
//    public static final int FACTUALITY_ARG1 = 5;
//    public static final int POLARITY_ARG1 = 6;
    public static final int _ARG1_ACTIONS = 4;
    
    public static final int SET_ARG2 = 0;
    public static final int PART_OF_ARG2 = 1;
//    public static final int SET_OPT2 = 2;
    public static final int ADD_INFO_ARG2 = 2;
    public static final int ADD_SUP2 = 3;
//    public static final int SOURCE_ARG2 = 4;
//    public static final int FACTUALITY_ARG2 = 5;
//    public static final int POLARITY_ARG2 = 6;
    public static final int _ARG2_ACTIONS = 4;
    
    public static final int REMOVE_CONNECTIVE = 0;
    public static final int REMOVE_INSTANCE = 1;
    public static final int REMOVE_ARG1 = 2;
    public static final int REMOVE_ARG2 = 3;
//    public static final int REMOVE_OPT1 = 4;
//    public static final int REMOVE_OPT2 = 5;
    public static final int REMOVE_INFO_CONNECTIVE = 4;
    public static final int REMOVE_INFO_ARG1 = 5;
    public static final int REMOVE_INFO_ARG2 = 6;
    public static final int REMOVE_SUP1 = 7;
    public static final int REMOVE_SUP2 = 8;
    public static final int REMOVE_PART_CONNECTIVE = 9;
    public static final int REMOVE_PART_ARG1 = 10;
    public static final int REMOVE_PART_ARG2 = 11;
    
    public static final int _MORE_ACTIONS_ = 12;
    
    public DiscourseAnnotationInterfaceAction(DiscourseArgOptionalJPanel editorJPanel, String text, ImageIcon icon,
                      String desc, Integer mnemonic, KeyStroke acclerator) {
        super(text, icon);
        
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, acclerator);

        currentAnnotationJPanel = editorJPanel;
    }

    public DiscourseAnnotationInterfaceAction(DiscourseArgOptionalJPanel editorJPanel, String text) {
        super(text);

        currentAnnotationJPanel = editorJPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
    
    }
    
    public static Action createAction(DiscourseArgOptionalJPanel jpanel, int mode)
    {
	Action act = null;
        String selection;
	
	switch(mode)
	{
	    case FREEZE:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Freeze")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.freeze(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Freeze_the_file."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

            case SELECT_LANGUAGE:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Switch_Language")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.switchLanguage(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_language."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

	    case SELECT_INPUT_METHOD:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Input_Method")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.selectInputMethod(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Select_the_input_method."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
		return act;

            case NEW_ANNOTATED:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("New")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.newFile(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Create_a_new_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;

            case OPEN_ANNOTATED:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Open")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.open(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Open_an_existing_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

            case CLOSE_ANNOTATED:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Close")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.closeFile(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Close_the_current_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		return act;

            case SAVE_ANNOTATED:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Save")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.save(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		return act;

            case SAVE_AS_ANNOTATED:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Save_As")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.saveAs(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Save_the_current_file_with_a_new_name."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		return act;
                
             case CONNECTIVE_TYPE:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Connective_Type")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.setconType(e);
//                        this.currentAnnotationJPanel.connectiveType(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Set_Connective_Type"));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;
                
            case PART_OF_CONNECTIVE:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Part_of_Connective")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.partofConnective(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Sets_Part_of_Connective."));
		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		return act;    
           
//             case IMPLICIT_CASE:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Implicit Connective") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.setconType(e, "Implicit");
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Implicit Connective Case.");
//		//act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
//		return act;   
//                
//            case ENTREL_CASE:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Entity Relation Connective") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.setconType(e,"EntRel");
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "EntRel Connective Case.");
//		//act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
//		return act;
//                
//            case NOREL_CASE:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "No Relation Connective") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.setconType(e,"NoRel");
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "NoRel Connective Case.");
//		//act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
//		return act;    
//                
//            case SOURCE_CON:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Source for Connective") {
//                    String selectedoption = "SourceCon";
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.source(e, selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set source for connective.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//            case FACTUALITY_CON:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Factuality for Connective") {
//                    String selectedoption ="FactualityCon";
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.factuality(e,selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set factuality for connective.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//           case POLARITY_CON:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Polarity for Connective") {
//                    String selectedoption = "PolarityCon";
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.polarity(e,selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set polarity for connective.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act;
            
            case HEAD_CON:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Head_for_Connective")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.setHead(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Head_for_Connective."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		return act;   
            
            case ADD_INFO_CONNECTIVE:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Add_Info_Connective")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.addInfoConnective(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_information_about_the_connective."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;    
           
        }
        
        return act;
    }
     
    
    public static Action createarg1Action(DiscourseArgOptionalJPanel jpanel, int mode)
    {
	Action act = null;
        String selection;
	
	switch(mode)
	{
	    
            case SET_ARG1:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Set_Arg_1_1")) {
                 public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.setArg1(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Set_argument_1."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;
        
            
           case PART_OF_ARG1:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Set_Arg_1_2")) {
                  public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.partofArg1(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Set_part_of_argument_1."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;     
                
//            case SET_OPT1:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Set Option 1") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.setOption1(e);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set option 1.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
//		return act;

         
            case ADD_INFO_ARG1:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Add_Info_Arg1")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.addInfoArg1(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_information_about_the_argument_1."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

          

            case ADD_SUP1:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Add_Sup1")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.addSup1(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_information_about_the_supplement_1."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

           
                
//           case SOURCE_ARG1:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Source for Arg1") {
//		    String selectedoption = "SourceArg1";
//                    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.source(e ,selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set source for Arguemnt1.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//            case FACTUALITY_ARG1:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Factuality for Arg1") {
//		    String selectedoption ="FactualityArg1";
//                    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.factuality(e, selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set factuality for Arguement1.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//           case POLARITY_ARG1:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Polarity for Arg1") {
//                    String selectedoption = "PolarityArg1"; 
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.polarity(e,selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set polarity for Arguement1.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act;
         
        }
        
        return act;
    }
    
     public static Action createarg2Action(DiscourseArgOptionalJPanel jpanel, int mode)
    {
	Action act = null;
        String selection;
	
	switch(mode)
	{
	    
            case SET_ARG2:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Set_Arg_2_1")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.setArg2(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Set_argument_2."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

           case PART_OF_ARG2:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Set_Arg_2_2")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.partofArg2(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Set_part_of_argument_2."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;  

//            case SET_OPT2:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Set Option 2") {
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.setOption2(e);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set option 2.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
//		return act;

            case ADD_INFO_ARG2:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Add_Info_Arg2")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.addInfoArg2(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_information_about_the_argument_2."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;

            
            case ADD_SUP2:
		act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Add_Sup2")) {
		    public void actionPerformed(ActionEvent e) {
			this.currentAnnotationJPanel.addSup2(e);
		    }
		};
		
		act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Add_information_about_the_supplement_2."));
//		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		return act;
                
           
//            case SOURCE_ARG2:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Source for Arg2") {
//		    String selection = "SourceArg2";
//                    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.source(e, selection);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set source for Arguement2.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//            case FACTUALITY_ARG2:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Factuality for Arg2") {
//		   String selectedoption ="FactualityArg2";
//                    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.factuality(e, selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set factuality for Arguement2.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act; 
//                
//           case POLARITY_ARG2:
//		act = new DiscourseAnnotationInterfaceAction(jpanel, "Polarity for Arg2") {
//                    String selectedoption = "PolarityArg2";
//		    public void actionPerformed(ActionEvent e) {
//			this.currentAnnotationJPanel.polarity(e, selectedoption);
//		    }
//		};
//		
//		act.putValue(SHORT_DESCRIPTION, "Set polarity for Arguement2.");
////		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		return act;

        }
        
        return act;
    }
    
    
    
    
    public static Action createMoreAction(DiscourseArgOptionalJPanel jpanel, int mode)
    {
        Action act = null;

        switch(mode)
        {
           
            case REMOVE_CONNECTIVE:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Connective")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeConnective(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_Connective"));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;
                
            case REMOVE_ARG1:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Arg_1")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeArg1(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_argument_1."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case REMOVE_ARG2:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Arg_2")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeArg2(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_argument_2."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

//            case REMOVE_OPT1:
//                act = new DiscourseAnnotationInterfaceAction(jpanel, "Remove Option 1") {
//                    public void actionPerformed(ActionEvent e) {
//                        this.currentAnnotationJPanel.removeOption1(e);
//                    }
//                };
//
//                act.putValue(SHORT_DESCRIPTION, "Remove option 1.");
//    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
//                return act;
//
//            case REMOVE_OPT2:
//                act = new DiscourseAnnotationInterfaceAction(jpanel, "Remove Option 2") {
//                    public void actionPerformed(ActionEvent e) {
//                        this.currentAnnotationJPanel.removeOption2(e);
//                    }
//                };
//
//                act.putValue(SHORT_DESCRIPTION, "Remove option 2.");
//    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
//                return act;

            case REMOVE_INFO_CONNECTIVE:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Info_Connective")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeInfoConnective(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_information_about_the_connective."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case REMOVE_INFO_ARG1:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Info_Arg1")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeInfoArg1(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_information_about_the_argument_1."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case REMOVE_INFO_ARG2:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Info_Arg2")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeInfoArg2(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_information_about_the_argument_2."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case REMOVE_SUP1:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Info_Sup1")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeInfoSup1(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_information_about_the_supplement_1."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;

            case REMOVE_SUP2:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Info_Sup2")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeInfoSup2(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_information_about_the_supplement_2."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;
                
            case REMOVE_INSTANCE:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_Instance")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removeConnective(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_instance."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act;    
                
             case REMOVE_PART_CONNECTIVE:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_part_of_connective")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removepartConnective(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_part_of_the_connective."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act; 
                
            case REMOVE_PART_ARG1:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_part_of_arg1")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removepartArg1(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_part_of_the_arg1."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act; 
                
            case REMOVE_PART_ARG2:
                act = new DiscourseAnnotationInterfaceAction(jpanel, GlobalProperties.getIntlString("Remove_part_of_arg2")) {
                    public void actionPerformed(ActionEvent e) {
                        this.currentAnnotationJPanel.removepartArg2(e);
                    }
                };

                act.putValue(SHORT_DESCRIPTION, GlobalProperties.getIntlString("Remove_part_of_the_arg2."));
    //		act.putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
                return act; 
               
        }
        
        return act;
    }
}
