package jsons;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import App.App;

public class getMyNeed {

   public static JsonElement getJson(String file) {
      String jsonString = null;
      try {
      jsonString = new String(Files.readAllBytes(Paths.get(new File(file).getPath())));
      }catch(Exception e){ e.printStackTrace(); }
      return JsonParser.parseString(jsonString);
   }

   public static List<JadbDevice> getDevices(){
       JadbConnection jadb = new JadbConnection();
        List<JadbDevice> devices = null;
        try {
            devices = jadb.getDevices();
        } catch (Exception e) { throw new RuntimeException(e);}

        return devices;
   }   

   public static void getxy(JadbDevice device){
        int E0035 = 0 , E0036 = 1 ;
        float[] EWH;

        int W = 0 ,H =1;
        float[] WH;

        float rateW ,rateH;
        float[] EXYT;
        int scrx ,scry;
        JsonArray XYJson = new JsonArray();

        try {
           EWH = getEWH(device.executeShell("getevent -p | grep -e \"0035\" -e \"0036\""));
           WH = getWH(device.executeShell("wm size"));
           rateW = WH[W] / EWH[E0035];
           rateH = WH[H] / EWH[E0036];
       
           for (int i = 0; i < 15; i++) {
              if( ( EXYT = getTap(device.executeShell("getevent | grep -e \"0035\" -e \"0036\""))) == null ){
                System.out.println(i + " -> "+"tap again");
                i--;
                continue;
              }else{
                
                scrx = (int)(EXYT[0]*rateW);
                scry = (int)(EXYT[1]*rateH);
                
                // XYJson.add(JsonParser.parseString("{\"x\":" +scrx +",\"y\":"+ scry+"}"));
                System.out.println(i +" - >" +"{\"x\":" + scrx +",\"y\":"+ scry+"}");
              }
           }



        } catch (Exception e) { e.printStackTrace();}

    }


    public static float[] getEWH(InputStream is) {
        ArrayList<String> temp = App.getIs(is, 0);
        float[] evenWH = new float[2];
        for(int i = 0 ; i < temp.size() ; i++){
            for (String string : (temp.get(i).replaceAll("\\s*", "")).split(",")) {
                if (string.startsWith("max")){
                    evenWH[i] = Float.parseFloat(string.substring(3,string.length()));
                }
            }    
        }
    
        return evenWH;
    }

    public static float[] getWH(InputStream is){

        String temp = App.getIs(is, 0).get(0).replaceAll("\\s*", "");

        temp = temp.substring(temp.indexOf(":")+1,temp.length());
        String[] arr = temp.split("x");
        float[] ret = new float[2];
        for (int i = 0; i < arr.length; i++){ 
            ret[i] = Float.parseFloat(arr[i]);
        }
        return ret;
    }


    public static float[] getTap(InputStream is){
        float[] EXY = new float[2];
        String[] sa;
        ArrayList<String> temp = App.getIs(is, 2);
        for (int i = 0 ; i < temp.size() ; i++) {
           sa = temp.get(i).split("\\s");
            if(("0035".equals(sa[sa.length-2]) && i == 0) || ("0036".equals(sa[sa.length-2]) && i == 1 )){
               EXY[i] = (float)Integer.parseInt(sa[sa.length-1],16);
            }else{
                return null; 
            }
        }
        return EXY;
    }
}
