/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rulesum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.rmi.runtime.Log;

/**
 *
 * @author Yulius
 */
public class MData {
    final String TAG = "MData";
    HashMap dictUser = new HashMap();
    
    private void connectDB(){
        try{
        
        } catch (Exception e){
        
        }
        
    }
       
    public String formalizeString(String s){
        String res = "";
        try{
            IndonesianSentenceFormalization form = new IndonesianSentenceFormalization();
            res = form.formalizeSentence(s);
            form.initStopword();
            res = form.deleteStopword(res);
        } catch (Exception e){
        }
        return res;
    }
    
    public void readAndParseChat(){
        try{
            //String s = "2018020107_CU.log.gz:INF 07:38:27.761   |CLXIncQueueProcessor5|10007:Ahmad|   MtS [1517445514433-3533150802903103d32510931b23729] [type:][version:][code:S0][id:0bb99f8f1517442836385][f_pin:0bb99f8f][l_pin:][{Bb:null}{BC:72}{A00:0bb99f8f}{B10:3d32510931b23729}{Bd:8.3.4.34}{A01:50}{Bf:4}{A15:1}{A06:4}{A18:0bb99f8f1517442836383}{A07:Pak+%400bb95886++tidak+follow+forum+SUC+ini...}{A19:1517445506399}{A118:0}{BQ:1517445514433-3533150802903103d32510931b23729}{B8:0bb99f8f}{BA:DF_40}][]";
            String data;
            int i = 0;
            File textInput = new File("DF_CHAT_FEBRUARI.txt");
            FileReader fileReader = new FileReader(textInput);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            final Pattern PAT = Pattern.compile("\\[id:(.*)\\]\\[f_pin:(.*)\\]\\[.*:.*\\]\\[(?:\\{.*:.*\\}){3,4}\\{A01:([^}]*)\\}(?:\\{.*:.*\\}){4,5}\\{A07:([^(}{)]*)\\}(?:\\{.*:.*\\}){2,5}\\]\\[\\]");
            while (((data=bufferedReader.readLine())!= null) && (i<30)) {
                 Matcher nameMtchr = PAT.matcher(data);
                 while (nameMtchr.find()) {
                    String temp = formatLine(nameMtchr.group(4));
                    temp = temp.replaceAll("\n", "");
                    temp = formalizeString(temp);
    //                String[] split = temp.split(" ");
    //                temp = URLDecoder.decode(temp, "UTF-8");
    //                Pattern PATTERN = Pattern.compile(".*@([^ ]+).*");
    
                    System.out.println(temp);
                    i++;
                }
            }
        } catch (IOException e){
        
        }
    }
    
    public void fillUserMap(){
        try{
            File input = new File("UserMap.txt");
            FileReader fileReader = new FileReader(input);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String data;
            while ((data = bufferedReader.readLine()) != null ){
                String[] con = data.split("\\|");
                dictUser.put(con[1].replaceAll(" ", ""), con[2].replaceAll("-", "").trim());
            };            
        } catch (Exception e){
            Log.getLog(TAG, "FillUserMap", true);
        }
    }
    
    private void convertUser(){
    
    }
    
    public String formatLine(String s){
        String k = "";
        String temp = "";
        try{
            temp = URLDecoder.decode(s, "UTF-8");
            Iterator it = dictUser.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry) it.next();
                if (temp.contains((CharSequence) pair.getKey())){
                    temp = temp.replaceAll("@"+pair.getKey().toString(), pair.getValue().toString().trim());
                }
            }
        } catch (Exception e){
        }
        return temp;
    }
    
    public static void main (String[] args){
        MData m = new MData();
        m.fillUserMap();
        m.readAndParseChat();
    }
}
