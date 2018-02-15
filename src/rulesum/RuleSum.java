/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rulesum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 *
 * @author Yulius
 */
public class RuleSum {

    /**
     * @param args the command line arguments
     * RuleSum: first step program for AI in text summarization
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        System.out.println("Input string: ");
        while (true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String st = br.readLine();
            IndonesianSentenceFormalization form = new IndonesianSentenceFormalization();
            String out = form.formalizeSentence(st);
            form.initStopword();
            String g = form.deleteStopword(out);
            System.out.println(g);
        }
    }
    
}
