/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sanchay.util.query;

import java.util.List;
import java.util.Vector;
import sanchay.corpus.ssf.tree.SSFNode;

/**
 *
 * @author ambati
 */
public class ProcessQuery {

    SyntacticCorpusContextQueryOptions contextOptions;
    SSFNode newNode;
    public String featureDetails;

    public ProcessQuery() {
        contextOptions = new SyntacticCorpusContextQueryOptions();
        newNode = new SSFNode();
        featureDetails = new String();
    }

    public void modifyRHS(String rhs, List nodes) {
        rhs = rhs.trim();
        String attr, val, val1 = "", val2 = "";
        String parts[] = rhs.split("=");
        attr = parts[0];
        val = parts[1];
        if (val.contains(":")) {
            String[] parts2 = val.split(":");
            val1 = parts2[0];
            val2 = parts2[1];
        } else {
            val1 = val;
            val2 = "";
        }
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                SSFNode node = (SSFNode) nodes.get(i);
                modifyNode(node, attr, val1, val2);
            }
        }
    }

    public String nodeNvalue(SSFNode node, String attr, boolean flag) {
        SSFNode tmpNode = new SSFNode();
        String[] parts = attr.split("\\.");
        if (parts.length == 1) {
            return attr;
        }

        String atrib = "";
        for (int i = 0; i < parts.length; i++) {
            String cnode = parts[i];
            if (cnode.equals("C")) {
                tmpNode = node;
            } else if (cnode.startsWith("A")) {
                //System.out.println(attr+"\t"+val1+"\t"+val2);
                String dist = cnode.replace("A(", "");
                dist = dist.replace(")", "");
                for (int j = 0; j < Integer.parseInt(dist); j++) {
                    tmpNode = (SSFNode) tmpNode.getParent();
                }
                //newNode.print(System.out);
            } else if (cnode.startsWith("D")) {
                String dist = cnode.replace("D(", "");
                dist = dist.replace(")", "");
                for (int j = 0; j < Integer.parseInt(dist); j++) {
                    tmpNode = (SSFNode) tmpNode.getChildAt(j);
                }
                //newNode.print(System.out);
            } else if (cnode.startsWith("P")) {

                String dist = cnode.replace("P(", "");
                dist = dist.replace(")", "");
                for (int j = 0; j < Integer.parseInt(dist); j++) {
                    tmpNode = (SSFNode) tmpNode.getPreviousSibling();
                }
                //newNode.print(System.out);
            } else if (cnode.startsWith("N")) {

                String dist = cnode.replace("N(", "");
                dist = dist.replace(")", "");
                for (int j = 0; j < Integer.parseInt(dist); j++) {
                    tmpNode = (SSFNode) tmpNode.getNextSibling();
                }
                //newNode.print(System.out);
            } else if (cnode.startsWith("a")) {
                atrib = cnode.replace("a('", "");
                atrib = atrib.replace("')", "");
                if (flag) {
                    atrib = tmpNode.getAttributeValue(atrib);
                }
            } else if (cnode.equals("t")) {
                if (flag) {
                    atrib = tmpNode.getName();
                } else {
                    atrib = "t";
                }
            } else if (cnode.equals("l")) {
                if (flag) {
                    atrib = tmpNode.getLexData();
                } else {
                    atrib = "l";
                }
            }
        }

        if (!flag) {
            newNode = tmpNode;
        }
        return atrib;
    }

    public String nodeNvalueWithFeatureDetails(SSFNode node, String attr, boolean flag) {
        SSFNode tmpNode = new SSFNode();
        String atrib = "";
        String result_atrib = "";
        String[] parts1 = attr.split("&");
        featureDetails = "";
        for (int k = 0; k < parts1.length; k++) {
            if (k != 0) {
                featureDetails = featureDetails + "&";
            }
            String[] parts = parts1[k].split("\\.");
            if (parts.length == 1) {
                System.err.println("PQuery.nodeNvalue(), error returning...");
                return attr;
            }
            try {
                for (int i = 0; i < parts.length; i++) {
                    String cnode = parts[i];
                    if (cnode.equals("C")) {
                        //featureDetails.concat("_CurrentWord(=");
                        featureDetails = featureDetails + "C";
                        tmpNode = node;
                        //featureDetails.concat(tmpNode.getLexData()+")_");
                    } else if (cnode.startsWith("A")) {
                        //System.out.println(attr+"\t"+val1+"\t"+val2);
                        String dist = cnode.replace("A(", "");
                        dist = dist.replace(")", "");
                        featureDetails = featureDetails + "A";
                        for (int j = 0; j < Integer.parseInt(dist); j++) {
                            tmpNode = (SSFNode) tmpNode.getParent();
                        }
                        //newNode.print(System.out);
                    } else if (cnode.startsWith("D")) {
                        String dist = cnode.replace("D(", "");
                        dist = dist.replace(")", "");
                        featureDetails = featureDetails + "D";
                        for (int j = 0; j < Integer.parseInt(dist); j++) {
                            tmpNode = (SSFNode) tmpNode.getChildAt(j);
                        }
                        //newNode.print(System.out);
                    } else if (cnode.startsWith("P")) {
                        String dist = cnode.replace("P(", "");
                        dist = dist.replace(")", "");
                        for (int j = 0; j < Integer.parseInt(dist); j++) {
                            featureDetails = featureDetails + "P";
                            tmpNode = (SSFNode) tmpNode.getPreviousSibling();
                        }
                        //newNode.print(System.out);
                    } else if (cnode.startsWith("N")) {
                        String dist = cnode.replace("N(", "");
                        dist = dist.replace(")", "");
                        featureDetails = featureDetails + "N";
                        for (int j = 0; j < Integer.parseInt(dist); j++) {
                            tmpNode = (SSFNode) tmpNode.getNextSibling();
                        }
                        //newNode.print(System.out);
                    } else if (cnode.startsWith("a")) {
                        atrib = cnode.replace("a('", "");
                        atrib = atrib.replace("')", "");
                        featureDetails = featureDetails + "A";
                        if (flag) {
                            atrib = tmpNode.getAttributeValue(atrib);
                        }
                    } else if (cnode.equals("t")) {
                        if (flag) {
                            atrib = tmpNode.getName();
                        } else {
                            atrib = "t";
                        }
                        featureDetails = featureDetails + "T";
                    } else if (cnode.equals("l")) {
                        if (flag) {
                            atrib = tmpNode.getLexData();
                        } else {
                            atrib = "l";
                        }
                        featureDetails = featureDetails + "l";
                    } else if (cnode.startsWith("s")) {
                        String dist = cnode.replace("s(", "");
                        dist = dist.replace(")", "");
                        int d = Integer.parseInt(dist);
                        if (flag) {
                            atrib = tmpNode.getLexData();
                            int len = atrib.length();
                            if (len > d) {
                                atrib = tmpNode.getLexData().substring(len - d);
                            }
                        } else {
                            atrib = "s";
                        }
                        featureDetails = featureDetails + "s";
                    } else if (cnode.startsWith("p")) {
                        String dist = cnode.replace("p(", "");
                        dist = dist.replace(")", "");
                        int d = Integer.parseInt(dist);
                        if (flag) {
                            atrib = tmpNode.getLexData();
                            int len = atrib.length();
                            if (len > d) {
                                atrib = tmpNode.getLexData().substring(0, d);
                            }
                        } else {
                            atrib = "p";
                        }
                        featureDetails = featureDetails + "p";
                    }
                }
            } catch (Exception e) {
                //System.err.println("No such item(" + attr + ") present");
                featureDetails = "null";
                return null;
            }
            if (!atrib.equals("")) {
                result_atrib = result_atrib + atrib + "_";
            }
        }
        //System.out.println("Query finished("+featureDetails+")");
        if (!flag) {
            newNode = tmpNode;
        }
        return result_atrib;
    }

    public void modifyNode(SSFNode node, String attr, String val1, String val2) {
        String atrib = nodeNvalue(node, attr, false);
        String value1 = nodeNvalue(node, val1, true);
        if (!val2.isEmpty()) {
            String value2 = nodeNvalue(node, val2, true);
            newNode.setAttributeValue(atrib, value1 + ":" + value2);
        } else {
            if (atrib.equals("l")) {
                newNode.setLexData(value1);
            } else if (atrib.equals("t")) {
                newNode.setName(value1);
            } else {
                newNode.setAttributeValue(atrib, value1);
            }
        }

        //System.out.println(atrib + "\t"+ value1);
        //newNode.setAttributeValue(atrib, value1);
        newNode.print(System.out);
        SSFNode root = (SSFNode) newNode.getRoot();
        root.print(System.out);
    }

    public SyntacticCorpusContextQueryOptions analyseLHS(String lhs) {
        SyntacticCorpusContextQueryOptions options = new SyntacticCorpusContextQueryOptions();
        lhs = lhs.trim();
        String parts[] = lhs.split(" ");
        String option = "";
        boolean mand = true;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("AND")) {
                mand = true;
            } else if (parts[i].equals("OR")) {
                mand = false;
            } else {
                option = parts[i];
                processOtions(option, mand);
            }
        }

        return contextOptions;
    }

    public void processOtions(String option, boolean mand) {
//        System.out.println("options is:\t"+option+"\targ is:\t"+mand);
        String key = "", value = "", node = "", attr = "";
        String[] parts = option.split("=");
        key = parts[0];
        value = parts[1];
        value = value.substring(1, value.length() - 1);
        value = "^" + value + "$";
        String[] parts2 = key.split("\\.");
        node = parts2[0];
        attr = parts2[1];

        fillOptions(node, attr, value, mand);

    }

    public void fillOptions(String node, String attr, String value, boolean mand) {
        if (node.equals("C")) {
            SyntacticCorpusQueryOptions nodeOptions = contextOptions.thisNodeOptions;
            fillNodeOptions(nodeOptions, attr, value, mand);
        }

        if (node.startsWith("A") || node.startsWith("D") || node.startsWith("P") || node.startsWith("N")) {
            String dist = node.replace(node.charAt(0) + "(", "");
//            String dist = node.replace("A(", "");
            dist = dist.replace(")", "");

            List ansOptions = null;

            if (node.startsWith("A")) {
                ansOptions = contextOptions.parentNodeOptions;
            } else if (node.startsWith("D")) {
                ansOptions = contextOptions.childNodeOptions;
            } else if (node.startsWith("P")) {
                ansOptions = contextOptions.prevNodeOptions;
            } else if (node.startsWith("N")) {
                ansOptions = contextOptions.nextNodeOptions;
            }

            if (ansOptions.isEmpty()) {
                SyntacticCorpusQueryOptions nodeOptions = new SyntacticCorpusQueryOptions();
                fillNodeOptions(nodeOptions, attr, value, mand);
                ansOptions.add(nodeOptions);
            } else {
                int flag = 0;
                for (int i = 0; i < ansOptions.size(); i++) {
                    SyntacticCorpusQueryOptions nodeOptions = (SyntacticCorpusQueryOptions) ansOptions.get(i);
                    if (nodeOptions.getDisWin().equals(dist)) {
                        flag = 1;
                        fillNodeOptions(nodeOptions, attr, value, mand);
                        ansOptions.add(nodeOptions);
                        break;
                    }
                }

                if (flag == 1) {
                    SyntacticCorpusQueryOptions nodeOptions = new SyntacticCorpusQueryOptions();
                    fillNodeOptions(nodeOptions, attr, value, mand);
                    ansOptions.add(nodeOptions);
                }
            }
        }
    }

    public void fillNodeOptions(SyntacticCorpusQueryOptions nodeOptions, String attr, String value, boolean mand) {
        String nattr = "";

        if (attr.equals("t")) {
            nodeOptions.setTag(value, mand);
        } else if (attr.equals("l")) {
            nodeOptions.setLexData(value, mand);
        } else if (attr.equals("a")) {
            nattr = value;
        } else if (attr.equals("v")) {
            nodeOptions.setAttrVal(nattr, value, mand);
        } else if (attr.equals("d")) {
            nodeOptions.setDisWin(value, mand);
        }
    }
}
