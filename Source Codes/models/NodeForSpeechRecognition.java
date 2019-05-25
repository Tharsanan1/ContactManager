package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kthar on 09/05/2018.
 * This class is crated to handle speech recognition functionality. this class instance contains relevant tag information and chile node.
 * it is a tree based solution for making decisions.
 */

public class NodeForSpeechRecognition implements Serializable {
    static final long serialVersionUID = 3248534826737538248L;
    static boolean flagForUpdateFromFile = false;
    static ArrayList<NodeForSpeechRecognition> globalNodeList = new ArrayList<>();
    ArrayList<NodeForSpeechRecognition> parentNodes;
    ArrayList<NodeForSpeechRecognition> childrenNodes;
    String tagValueIfEnd;
    String value;
    NodeForSpeechRecognition(String value){
        this.value = value.toUpperCase();
        parentNodes = new ArrayList<>();
        childrenNodes = new ArrayList<>();
        this.tagValueIfEnd = "";
    }

    public static ArrayList<NodeForSpeechRecognition> getGlobalNodeList() {
        return globalNodeList;
    }

    public static void setGlobalNodeList(ArrayList<NodeForSpeechRecognition> globalNodeList) {
        NodeForSpeechRecognition.globalNodeList = globalNodeList;
    }

    public void addParentNode(NodeForSpeechRecognition node){
        parentNodes.add(node);
    }
    public void addChildrenNode(NodeForSpeechRecognition node){
        childrenNodes.add(node);
    }

    public ArrayList<NodeForSpeechRecognition> getChildrenNodes() {
        return childrenNodes;
    }

    public ArrayList<NodeForSpeechRecognition> getParentNodes() {
        return parentNodes;
    }
    public void addToGlobalList(NodeForSpeechRecognition node){
        globalNodeList.add(node);
    }
    public static String extractTagAndValue(String sentence){
        String[] words = sentence.toUpperCase().trim().split(" ");
        int index = -1;
        if(words.length != 0) {
            for (int i = 0; i < globalNodeList.size(); i++) {
                if (globalNodeList.get(i).getValue().equals(words[0])) {
                    return extractTagAndValueWhenStartAt(words,globalNodeList.get(i));
                }
            }
            return "NotFound" + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+addStringToEndFrom(0,words);
        }
        return "NotFound" + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+addStringToEndFrom(0,words);
    }
    public static String extractTagAndValueWhenStartAt(String[] words, NodeForSpeechRecognition nodeParent){
        NodeForSpeechRecognition targetNode = nodeParent;
        if(words.length>1) {
            for (int i = 1; i < words.length; i++) {
                if (targetNode.getChildrenNodes().size() == 0) {
                    String s = addStringToEndFrom(i, words);
                    return targetNode.getTagValueIfEnd() + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER + s;
                }
                boolean flag = false;
                for (int k = 0; k < targetNode.getChildrenNodes().size(); k++) {
                    if (targetNode.getChildrenNodes().get(k).getValue().equals(words[i])) {
                        targetNode = targetNode.getChildrenNodes().get(k);
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    continue;
                }
                return targetNode.getTagValueIfEnd() + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER + addStringToEndFrom(i, words);
            }
            return nodeParent.getTagValueIfEnd() + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER + "";
        }
        else{
            return nodeParent.getTagValueIfEnd() + ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER + "";
        }

    }
    public static String addStringToEndFrom(int indexStart, String[] words){
        String toOut = "";
        for(int i = indexStart; i < words.length; i++){
            toOut+=words[i];
        }
        return  toOut;
    }

    public String getValue() {
        return value;
    }

    public String getTagValueIfEnd() {
        return tagValueIfEnd;
    }


    public void setTagValueIfEnd(String tagValueIfEnd) {
        this.tagValueIfEnd = tagValueIfEnd.toUpperCase();
    }
    public class TagWithValue{
        String tag;
        String value;
        TagWithValue(String tag, String value){
            this.tag = tag;
            this.value = value;
        }
    }
    public static void trainTreeUsing(String sentence, String tag, String value){
        sentence = sentence.toUpperCase();
        tag = tag.toUpperCase();
        value = value.toUpperCase();
        int index = sentence.indexOf(value);
        if(index > -1){
            sentence = sentence.substring(0,index).trim();
            String[] words = sentence.split(" ");
            NodeForSpeechRecognition tempNode = null;
            int indexInWords = -1;
            boolean flagForAdd = false;
            for(int i = 0; i < words.length; i++){
                if(i==0) {
                    indexInWords = i;
                    boolean flag = false;
                    for (int k = 0; k < globalNodeList.size(); k++) {
                        if (globalNodeList.get(k).getValue().equals(words[i])) {
                            tempNode = globalNodeList.get(k);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    } else {
                        flagForAdd = true;
                        break;
                    }
                }
                else{
                    indexInWords = i;
                    boolean flag = false;
                    for (int k = 0; k < tempNode.getChildrenNodes().size(); k++) {
                        if (tempNode.getChildrenNodes().get(k).getValue().equals(words[i])) {
                            tempNode = tempNode.getChildrenNodes().get(k);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        continue;
                    } else {
                        flagForAdd = true;
                        break;
                    }
                }
            }
            if(flagForAdd){
                if(tempNode == null){
                    tempNode = new NodeForSpeechRecognition(words[indexInWords]);
                    globalNodeList.add(tempNode);
                    for(int i = indexInWords+1; i < words.length; i++){
                        NodeForSpeechRecognition nodeForSpeechRecognition = new NodeForSpeechRecognition(words[i]);
                        tempNode.addChildrenNode(nodeForSpeechRecognition);
                        tempNode = nodeForSpeechRecognition;
                        globalNodeList.add(nodeForSpeechRecognition);
                    }
                    tempNode.setTagValueIfEnd(tag);
                }
                else {
                    for (int i = indexInWords; i < words.length; i++) {
                        for (int k = indexInWords; k < words.length; k++) {
                            NodeForSpeechRecognition nodeForSpeechRecognition = new NodeForSpeechRecognition(words[k]);
                            tempNode.addChildrenNode(nodeForSpeechRecognition);
                            tempNode = nodeForSpeechRecognition;
                            globalNodeList.add(nodeForSpeechRecognition);
                        }
                        tempNode.setTagValueIfEnd(tag);

                    }
                }
            }
            else{
                if (tempNode!=null){
                    tempNode.setTagValueIfEnd(tag);
                }
            }
        }
    }

}
