package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kthar on 09/05/2018.
 * This class created for save static list of NodeForSpeechRecognition class globalList.
 * If you want to save globalList at a certain time first set 'nodeForSpeechRecognitions' static list
 * --in this class and call saveToFile instant method with Context parameter.
 * to retain saved object use readFromFile() instant method with a Context parameter.
 * serialVersionUID set to a constant for the correct match when deserialization.
 * createSerializable.ser is the fie name that the file is going to be saved in.
 */

public class ClassForSavingSerializableObject implements Serializable {
    static final long serialVersionUID = 3248534826737538248L;
    static String fileName = "createSerializable.ser";
    ArrayList<NodeForSpeechRecognition> nodeForSpeechRecognitions;
    public ClassForSavingSerializableObject(ArrayList<NodeForSpeechRecognition> nodeForSpeechRecognitions){
        this.nodeForSpeechRecognitions = nodeForSpeechRecognitions;
    }

    public ArrayList<NodeForSpeechRecognition> getNodeForSpeechRecognitions() {
        return nodeForSpeechRecognitions;
    }

    public void saveToFile(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ClassForSavingSerializableObject readFromFile(Context context) {
        ClassForSavingSerializableObject createResumeForm = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            createResumeForm = (ClassForSavingSerializableObject) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return createResumeForm;
    }
}
