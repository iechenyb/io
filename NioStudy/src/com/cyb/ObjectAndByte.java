package com.cyb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.cyb.chat.Message;

public class ObjectAndByte {
	/** 
     * 对象转数组 
     * @param obj 
     * @return 
     */  
    public static byte[] toByteArray (Object obj) {     
        byte[] bytes = null;     
        ByteArrayOutputStream bos = new ByteArrayOutputStream();     
        try {       
            ObjectOutputStream oos = new ObjectOutputStream(bos);        
            oos.writeObject(obj);       
            oos.flush();        
            bytes = bos.toByteArray ();     
            oos.close();        
            bos.close();       
        } catch (IOException ex) {       
            ex.printStackTrace();  
        }     
        return bytes;   
    }  
      
    /** 
     * 数组转对象 
     * @param bytes 
     * @return 
     */  
    public static Object toObject (byte[] bytes) {     
        Object obj = null;     
        try {       
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);       
            ObjectInputStream ois = new ObjectInputStream (bis);       
            obj = ois.readObject();     
            ois.close();  
            bis.close();  
        } catch (IOException ex) {       
            ex.printStackTrace();  
        } catch (ClassNotFoundException ex) {       
            ex.printStackTrace();  
        }     
        return obj;   
    }  
    public static Object toObject(String data){
    	return toObject(data.getBytes());
    }
    
    public static void main(String[] args) {
		Message msg = new Message("a","1","c");
		byte[] bts = toByteArray(msg);
		Message msg0 = (Message) toObject(bts);
		System.out.println(msg0.getData());
	}
}
