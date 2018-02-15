/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rulesum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author easysoft
 */
public class IndonesianSentenceFormalization {
  public HashMap<String, String> listUnformal;
  public ArrayList<String> listRule;
  public ArrayList<String> listStopword;
  private ArrayList<String> numbers;
  public static final String[] ortographies = { "null", "NumStr", "AllLowerCase", "FirstCaps", "AllCaps", "MixedCase", "Digit", "CharDigit", "TimeFormat", "DateFormat", "DigitSlash", "MoneyFormat", "Numeric" };
  



  public static final String[] tokenKinds = { "null", "PUNC", "NUM", "WORD" };
  




  public IndonesianSentenceFormalization()
  {
    listUnformal = new HashMap();
    listRule = new ArrayList();
    try {
      BufferedReader reader = new BufferedReader(new FileReader("formalizationDict.txt"));
      
      String line = null;
      while ((line = reader.readLine()) != null) {
        String[] entry = line.split("\t");
        listUnformal.put(entry[0], entry[1]);
      }
      reader.close();
      
      reader = new BufferedReader(new FileReader("formalizationRule.txt"));
      line = null;
      while ((line = reader.readLine()) != null) {
        listRule.add(line);
      }
      reader.close();
    }
    catch (FileNotFoundException ex) {
      Logger.getLogger(IndonesianSentenceFormalization.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(IndonesianSentenceFormalization.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void initStopword() {
    listStopword = new ArrayList();
    try {
      BufferedReader reader = new BufferedReader(new FileReader("stopwordslist.txt"));
      String line = null;
      while ((line = reader.readLine()) != null) {
        listStopword.add(line);
      }
      reader.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(IndonesianSentenceFormalization.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(IndonesianSentenceFormalization.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public String deleteStopword(String sentence) {
    IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
    ArrayList<String> listToken = tokenizer.tokenizeSentence(sentence);
    String noSw = "";
    for (int i = 0; i < listToken.size(); i++) {
      String word = (String)listToken.get(i);
      if (!listStopword.contains(word)) {
        noSw = noSw + word + " ";
      }
    }
    return noSw;
  }
  







  public String formalizeSentence(String sentence)
  {
    IndonesianSentenceTokenizer tokenizer = new IndonesianSentenceTokenizer();
    
    ArrayList<String> listToken = tokenizer.tokenizeSentence(sentence);
    
    String formalSentence = "";
    
    for (int i = 0; i < listToken.size(); i++) {
      String word = (String)listToken.get(i);
      String formalWord = formalizeWord(word);
      formalSentence = formalSentence + formalWord + " ";
    }
    
    return formalSentence;
  }
  

  public String formalizeWord(String word)
  {
    String ruleName = "";
    String ruleCondition = "";
    String[] rule = new String[2];
    
    int i = 0;
    while (i < listRule.size()) {
      ruleName = (String)listRule.get(i);
      
      if (ruleName.startsWith("@@")) {
        i++;
        ruleCondition = (String)listRule.get(i);
        
        if (ruleCondition.startsWith("##")) {
          boolean match = false;
          boolean newRule = false;
          
          while ((!match) && (!newRule) && (i < listRule.size())) {
            ruleCondition = (String)listRule.get(i);
            
            if (ruleCondition.startsWith("##")) {
              ruleCondition = ruleCondition.substring(2);
              match = word.matches(ruleCondition);
              
              if (!match) {
                i++;
              }
            }
            else if (ruleCondition.startsWith("@@")) {
              newRule = true;
            }
            else {
              i++;
            }
          }
          
          if (match) {
            i++;
            while ((i < listRule.size()) && (!((String)listRule.get(i)).startsWith("@@")) && (!((String)listRule.get(i)).startsWith("##"))) {
              rule = ((String)listRule.get(i)).split(">>>");
              word = word.replaceAll(rule[0], rule[1]);
              
              i++;
            }
            
            if ((i < listRule.size()) && 
              (((String)listRule.get(i)).startsWith("##"))) {
              i++;
              while ((i < listRule.size()) && (!((String)listRule.get(i)).startsWith("@@"))) {
                i++;
              }
            }
          }
        }
        else
        {
          while ((i < listRule.size()) && (!((String)listRule.get(i)).startsWith("@@"))) {
            rule = ((String)listRule.get(i)).split(">>>");
            word = word.replaceAll(rule[0], rule[1]);
            
            i++;
          }
        }
      }
    }
    
    if (listUnformal.containsKey(word)) {
      word = (String)listUnformal.get(word);
    }
    
    return word;
  }
  
  public boolean isLink(String token) {
    return token.matches("(https?://)?(www\\.)?[\\w\\-\\.~]+\\.[a-z]{2,6}(/[\\w\\-\\.~]*)*");
  }
  
  public boolean isMention(String token) {
    return token.matches("(RT)?@\\w+\\W*");
  }
  
  public boolean isTime(String token) {
    if (token.matches("([12])?\\d([\\:\\.])\\d{2}(\\2\\d{2})?([aApP][mM])?")) {
      return true;
    }
    if (token.matches("\\d{1,2}[/\\-\\.]\\d{1,2}[/\\-\\.]\\d{2,4}")) {
      return true;
    }
    return token.matches("\\d{2,4}[/\\-\\.]\\d{1,2}[/\\-\\.]\\d{1,2}");
  }
  
  public String extractOrtography(String token) {
    if (numbers.contains(token.toLowerCase())) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("NumStr")];
    }
    if (token.matches("[\\W_]*[a-z]+(\\-[a-z]+)?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("AllLowerCase")];
    }
    if (token.matches("[\\W_]*[A-Z][a-z]+(\\-[A-Za-z][a-z]+)?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("FirstCaps")];
    }
    if (token.matches("[\\W_]*[A-Z]+(\\-[A-Z]+)?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("AllCaps")];
    }
    if (token.matches("[\\W_]*[A-Za-z]+(\\-[A-Za-z]+)?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("MixedCase")];
    }
    if (token.matches("[\\W_]*[0-9]+[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("Digit")];
    }
    if (token.matches("[\\W_]*[A-Za-z0-9]+[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("CharDigit")];
    }
    if (token.matches("[\\W_]*([12])?\\d([\\:\\.])\\d{2}(\\2\\d{2})?([aApP][mM])?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("TimeFormat")];
    }
    if (token.matches("[\\W_]*\\d{1,2}[/\\-\\.]\\d{1,2}[/\\-\\.]\\d{2,4}[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("DateFormat")];
    }
    if (token.matches("[\\W_]*\\d{2,4}[/\\-\\.]\\d{1,2}[/\\-\\.]\\d{1,2}[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("DateFormat")];
    }
    if (token.matches("[\\W_]*[0-9]+/[0-9]+(/[0-9]+)?[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("DigitSlash")];
    }
    if (token.matches("[\\D]+[0-9]+([,\\.][0-9]+)+")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("MoneyFormat")];
    }
    if (token.matches("[\\W_]*[0-9]+([,\\-\\.][0-9]+)+[\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("Numeric")];
    }
    if (token.matches("[\\W_]*[0-9]+\\-[aA][nN][\\W_]*")) {
      return ortographies[java.util.Arrays.asList(ortographies).indexOf("Numeric")];
    }
    return ortographies[java.util.Arrays.asList(ortographies).indexOf("null")];
  }
  
  public String extractTokenKind(String token) {
    if (token.matches("[\\W_]+")) {
      return tokenKinds[java.util.Arrays.asList(tokenKinds).indexOf("PUNC")];
    }
    if (token.matches("[^a-zA-Z]+")) {
      return tokenKinds[java.util.Arrays.asList(tokenKinds).indexOf("NUM")];
    }
    return tokenKinds[java.util.Arrays.asList(tokenKinds).indexOf("WORD")];
  }
  
  public static void main(String[] args)
  {
    IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
    
    String sentence = "kata2nya 4ku donk 12 loecoe bangedh gt(...) lw madesu bgt .";
    sentence = formalizer.formalizeSentence(sentence);
    System.out.println(sentence);
    formalizer.initStopword();
    System.out.println(formalizer.deleteStopword(sentence));
  }
}
