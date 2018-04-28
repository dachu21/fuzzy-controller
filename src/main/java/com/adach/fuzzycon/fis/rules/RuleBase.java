package com.adach.fuzzycon.fis.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class RuleBase {

  private List<Rule> rules = new ArrayList<>();

  public List<Rule> getRules() {
    return rules;
  }

  public void readRules(File inputFile) {
    SAXBuilder saxBuilder = new SAXBuilder();
    try {
      Document document = saxBuilder.build(inputFile);
      Element rootXML = document.getRootElement();

      List<Element> rules = rootXML.getChildren();
      for (Element rule : rules) {
        List<Element> antecedentsAndConsequents = rule.getChildren();
        Map<String, String> antecedents = new HashMap<String, String>();
        Pair<String, String> consequent = null;
        for (Element element : antecedentsAndConsequents) {
          String linguisticVariable = element.getAttributeValue("name");
          String linguisticValue = element.getAttributeValue("value");
          String type = element.getName();
          if (type.equals("antecedent")) {
            antecedents.put(linguisticVariable, linguisticValue);
          } else if (type.equals("consequent")) {
            consequent = new Pair<>(linguisticVariable, linguisticValue);
          }
        }
        Rule ruleProg = new Rule(antecedents, consequent);
        this.rules.add(ruleProg);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}