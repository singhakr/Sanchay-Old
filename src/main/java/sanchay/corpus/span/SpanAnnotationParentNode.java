/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.corpus.span;

import javax.swing.tree.MutableTreeNode;
import sanchay.properties.PropertiesManager;
import sanchay.corpus.ssf.tree.SSFPhrase;
import sanchay.properties.KeyValueProperties;
import sanchay.properties.PropertyTokens;
import sanchay.common.types.PropertyType;
import java.util.*;
import java.io.*;
import sanchay.GlobalProperties;

/**
 *
 * @author Anil Kumar Singh
 */
public class SpanAnnotationParentNode extends SSFPhrase
        implements MutableTreeNode, Serializable {
    
    protected SpanAnnotationInfo spanAnnotationInfo;
    protected PropertiesManager propman;
    private Vector children; 
    protected SpanAnnotationParentNode Parent;
    int pos[];
    
    public SpanAnnotationParentNode(String id, String lexdata, String name, String stringFS, Object userObject) throws Exception
    {
        super(id, lexdata, name, stringFS, userObject);
         propman = new PropertiesManager();
        spanAnnotationInfo = new SpanAnnotationInfo();
        children=new Vector();
        pos=null;
        parent=null;
    }
    
    
    

    SpanAnnotationParentNode() {
         propman = new PropertiesManager();
        spanAnnotationInfo = new SpanAnnotationInfo();
        children=new Vector();
         pos=null;
        parent=null;
    }

    SpanAnnotationParentNode(int f) {
         propman = new PropertiesManager();
        spanAnnotationInfo = new SpanAnnotationInfo();
        children=new Vector();
         pos=null;
        parent=null;
    }

    SpanAnnotationParentNode(String n) {
        try
        {
            propman = new PropertiesManager("workspace/span-annotation/values/propman.txt",GlobalProperties.getIntlString("UTF8"));
        }
        catch(Exception e){
            
         System.out.print(e);   
            
        }
       
        KeyValueProperties temp=new KeyValueProperties();
        temp=(KeyValueProperties)propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        temp.addProperty("name", n);
        propman.addPropertyContainer("keyvalue-props",temp,PropertyType.KEY_VALUE_PROPERTIES);
        spanAnnotationInfo = new SpanAnnotationInfo();
        children=new Vector();
        pos=null;
        parent=null;
    }
    SpanAnnotationParentNode(String n, int f) {
        try
        {
            propman = new PropertiesManager("workspace/span-annotation/values/propman.txt",GlobalProperties.getIntlString("UTF8"));
        }
        catch(Exception e){
            
         System.out.print(e);   
            
        }
        
        KeyValueProperties temp=new KeyValueProperties();
        temp=(KeyValueProperties)propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
        temp.addProperty("name",n);
        String flag;
        if(f==0 || f==1)
                    flag=""+f;
                else
                    flag="-1";
        temp.addProperty("flag",flag);
        
        propman.addPropertyContainer("keyvalue-props",temp,PropertyType.KEY_VALUE_PROPERTIES);
        spanAnnotationInfo = new SpanAnnotationInfo();
        children=new Vector();
        pos=null;
        parent=null;
    }

    public SpanAnnotationInfo getSpanAnnotationInfo() {
        return spanAnnotationInfo;
    }

    public void setSpanAnnotationInfo(SpanAnnotationInfo spanAnnotationInfo) {
        this.spanAnnotationInfo = spanAnnotationInfo;
    }

    public PropertiesManager getPropman() {
        return propman;
    }

    public void setPropman(PropertiesManager propman) {
        this.propman = propman;
    }
    public int addChild1(SpanAnnotationParentNode child) 
    {
		if(child == null)
			return 0;
		children.add(child);
		return 1;
    }
    public int setParent(SpanAnnotationParentNode par)
    {
            if(par==null)
                return 0;
            parent=par;
            return 1;
     }
        public SpanAnnotationParentNode getChild(int index)
	{
		if(children == null || index>=children.size() || index<0)
			return null;
		else
			return (SpanAnnotationParentNode)(children.elementAt(index));
	}
    
        public int setPos(int index[])
	{
		if(index==null)
			return 0;
		int l=index.length;
		if((l%2)!=0)
			return 0;
                
                
                PropertyTokens temp=new PropertyTokens();
                
                temp=(PropertyTokens)propman.getPropertyContainer("tokenvalue-props",PropertyType.PROPERTY_TOKENS);
              
                for(int i=0;i<index.length;i++)
                {
                    String str=""+index[i];
                    temp.addToken(str);
                }
               
                propman.addPropertyContainer("tokenvalue-props",temp,PropertyType.PROPERTY_TOKENS);
                
                
                pos=index;
		return 1;
                
	}
        public int [] getPos()
	{
		return pos;
	}
        
        public int NumOfChildren()
	{
		if(children==null)
			return 0;
		else
			return children.size();
	}
        public String getProp(String name,PropertyType pt)
        {
            String out=null;
            
            if(pt == PropertyType.KEY_VALUE_PROPERTIES)
            {
              KeyValueProperties temp=new KeyValueProperties();
              temp=(KeyValueProperties)propman.getPropertyContainer("keyvalue-props",PropertyType.KEY_VALUE_PROPERTIES);
              out=temp.getPropertyValue(name);
                
                
            }
            return out;
            
        }
        public int removeChild1(int index)  //removes child at 'index' in the children vector
	{
		if(children==null || index>=children.size() || index<0)
			return 0;
		children.removeElementAt(index);
		return 1;
	}
}
