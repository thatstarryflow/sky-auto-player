package App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jsons.getMyNeed;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class App {
    public static void play(JadbDevice device) {

       
       
        JsonArray keysArr = getMyNeed.getJson("keys.json").getAsJsonArray();

        JsonArray song = getMyNeed.getJson("huahia.json")
                        .getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("songNotes").getAsJsonArray();
        
        int songL = song.size();

        JsonObject KeyNowObj , KeyNextObj;

        int fastTime = 100;

        JsonObject keyNow;
        int keyId  , keyx , keyy ,sleep;
        StringBuffer commond = new StringBuffer();
        for (int i = 0; i < songL ; i++) {

            KeyNowObj =  song.get(i).getAsJsonObject();

            keyId = Byte.parseByte(
                    KeyNowObj.get("key").getAsString()
                    .substring(4)
                    );

            keyNow = keysArr.get(keyId).getAsJsonObject();


            if(i < (songL-1) ){
                KeyNextObj = song.get(i+1).getAsJsonObject();
                sleep = KeyNextObj.get("time").getAsInt() - KeyNowObj.get("time").getAsInt();
            }else sleep = 0;
            
            
           if(sleep > fastTime) sleep = sleep - fastTime;


            keyx = keyNow.get("x").getAsInt();
            keyy = keyNow.get("y").getAsInt();
            
            commond.append("input tap ");
            commond.append(keyx);
            commond.append(" ");
            commond.append(keyy);

            System.out.println(i + "  (Key"+ keyId + ") -> " + commond + "  sleep: " + sleep+"ms ");
            try {
                Thread.sleep(sleep);
                getIs(device.executeShell(commond.toString()),0);
            } catch (Exception e) { throw new RuntimeException(e);}
         
            commond.setLength(0);    
           
        }

    }


    public static void main(String[] args) {
        List<JadbDevice> devices = getMyNeed.getDevices();
        JadbDevice device;
        if (devices.size() == 0 ) {
            System.out.println("No devices !");
            return;
        }else{
            device = devices.get(0);
            System.out.println(device);
        }

        play(device);
        // getMyNeed.getxy(device);

        

    }

    //int line -> 0不限制读取行数，其他为限制行数
     public static ArrayList<String> getIs(InputStream is,int line){
        ArrayList<String> strL = new ArrayList<>();
        int lineLock = 0;
        BufferedReader br; 
        String readline;
        try (InputStreamReader isr = new InputStreamReader(is,"utf-8")) {
            br = new BufferedReader(isr);
            while ((br.read())!= -1) {
                readline = br.readLine();
                strL.add(readline); 
                System.out.println(readline);
                if(line != 0 && ++lineLock == line) break;
            }
        } catch (Exception e) { e.printStackTrace(); } 
 
        return strL;
    }

}
