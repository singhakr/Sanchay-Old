/*created by Kinshul
  used to store information for connective-instances and their arguments 
  the annotation information of a story can be stored in the form of a tree of these AnnotationInfo nodes   
 */
package sanchay.corpus.discourse;
import java.util.Vector;
public class AnnotationInfo
{
	
        private String name;           
	private int part[];      //stores part of a connective
//        private String source;         //source for connective and its arguements
//        private String factuality;     //factuality for connective and its arguements
//        private String polarity;       //polarity for connective and its arguements
        private String head;
        private int pos[];             //starting and ending characters numbers are stored as adjacent integer pairs in the array 
        private int attribSpan[];             //starting and ending characters numbers are stored as adjacent integer pairs in the array 
	private AnnotationInfo parent; //points to the parent 
	private Vector children;       //array of children
	private String optInfo;        //stores optional tags for connective-instances, their arguments and their supplements
	private int supInfo[];         //starting and ending character numbers for supplements, stored as adjacent integer pairs in the array
        private int flag;              //flag value set to -1 by default : is set to 0 if the object represents argument 1 and to 1 if it represents argument 2
	private String type;           //stores the type of connective instances
        AnnotationInfo()
	{
		name=null;
                part=null;
//                source=null;
//                factuality=null;
//                polarity=null;
                head=null;
		pos=null;
                attribSpan = null;
		parent=null;
		children=new Vector();
		optInfo=null;
		supInfo=null;
                flag=-1;
                type="null";
        }
        AnnotationInfo(int f)
	{
		name=null;
                part=null;
//                source=null;
//                factuality=null;
//                polarity=null;
                head=null;
		pos=null;
                attribSpan = null;
		parent=null;
		children=new Vector();
		optInfo=null;
		supInfo=null;
                if(f==0 || f==1)
                    flag=f;
                else
                    flag=-1;
                type="null";

	}
	AnnotationInfo(String n)
	{
		name=n;
                part=null;
//                source=null;
//                factuality=null;
//                polarity=null;
                head=null;
		pos=null;
                attribSpan = null;
		parent=null;
		children=new Vector();
		optInfo=null;
		supInfo=null;
                flag=-1;
                type="null";

	}
        AnnotationInfo(String n,int f)
	{
		name=n;
                part=null;
//                source=null;
//                factuality=null;
//                polarity=null;
                head=null;
		pos=null;
                attribSpan = null;
		parent=null;
		children=new Vector();
		optInfo=null;
		supInfo=null;
                if(f==0 || f==1)
                    flag=f;
                else
                    flag=-1;
                type=null;

	}
	public int setName(String n)
	{
		if(n==null)
			return 0;
		name=n;
		return 1;
	}
	public String getName()
	{
		return name;
	}
        public int setPart(int index[])
	{
		if(index==null)
                {      
                    part=null;
	            return 0;
                 }
		int l=index.length;
		if((l%2)!=0)
			return 0;
		part=index;
		return 1;
	}
	public int [] getPart()
	{
		return part;
	}
//        public void setSource(String s)
//	{
//		if(s==null)
//                source=null;
//            else
//		source=s;
//	}
//	public String getSource()
//	{
//		return source;
//	}
//        public void setPolarity(String s)
//	{
//		if(s==null)
//                polarity=null;
//            else
//		polarity=s;
//	}
//	public String getPolarity()
//	{
//		return polarity;
//	}
//        public void setFactuality(String s)
//	{
//		if(s==null)
//                factuality=null;
//            else
//		factuality=s;
//	}
//	public String getFactuality()
//	{
//		return factuality;
//	}
        public void setHead(String s)
	{
		if(s==null)
                head=null;
            else
		head=s;
	}
	public String getHead()
	{
		return head;
	}
	public int setPos(int index[])
	{
		if(index==null)
			return 0;
		int l=index.length;
		if((l%2)!=0)
			return 0;
		pos=index;
		return 1;
	}
	public int [] getPos()
	{
		return pos;
	}
	
        public int [] getAttribSpan()
	{
		return attribSpan;
	}

        public int setAttribSpan(int index[])
	{
		if(index==null)
			return 0;
		int l=index.length;
		if((l%2)!=0)
			return 0;
		attribSpan=index;
		return 1;
	}
        
        public int setParent(AnnotationInfo par)
        {
            if(par==null)
                return 0;
            parent=par;
            return 1;
        }
        public AnnotationInfo getParent()
        {
            return parent;
        }
	public int NumOfChildren()
	{
		if(children==null)
			return 0;
		else
			return children.size();
	}
	public int addChild(AnnotationInfo child) 
	{
		if(child == null)
			return 0;
		children.add(child);
		return 1;
	}
        public int getFlag()
        {
            return flag;
        }
        public int setFlag(int f)
        {
            if(f==0 || f==1)
            {
                flag=f;
                return 1;
            }
            else
            {
                return 0;
            }
        }
	public AnnotationInfo getChild(int index)
	{
		if(children == null || index>=children.size() || index<0)
			return null;
		else
			return (AnnotationInfo)(children.elementAt(index));
	}
	public int removeChild(int index)  //removes child at 'index' in the children vector
	{
		if(children==null || index>=children.size() || index<0)
			return 0;
		children.removeElementAt(index);
		return 1;
	}
       
	public void setoptInfo(String info)
	{
            if(info==null)
            {
                optInfo=null;
                attribSpan = null;
            }
            else
		optInfo=info;
	}
	public String getoptInfo()
	{
		return optInfo;
	}
	public int setsupInfo(int index[])
	{
                if(index==null)
                {
                    supInfo=null;
                    return 0;
                }
                int l=index.length;
                if((l%2)!=0)
                        return 0;
                supInfo=index;
                return 1;

	}
	public int [] getsupInfo()
	{
		return supInfo;
	}
        public void setType(String t)
	{
            type=t;
        }
        public String getType()
        {
            return type;
        }

/*	public static void main(String args[])
	{
		AnnotationInfo root=new AnnotationInfo("Story"),node=new AnnotationInfo("Kinshul"),child=new AnnotationInfo("Instance1");
		System.out.println(node.getName());
		root.addChild(node);
		node.addChild(child);
		child=new AnnotationInfo("Instance2");
		node.addChild(child);
		child=new AnnotationInfo("Instance3");
		node.addChild(child);
		node=new AnnotationInfo();
		node.setName("I");
		int pos[]={2,5};
		node.setPos(pos);
		root.addChild(node);
		child=new AnnotationInfo("Instance1");
		node.addChild(child);
		child=new AnnotationInfo("Instance2");
		node.addChild(child);
		System.out.println("Number of connective = "+String.valueOf(root.NumOfChildren())+"\n");	
	}*/
}

