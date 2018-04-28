package com.adach.fuzzycon.fis;

import static java.lang.Math.max;

import com.adach.fuzzycon.fis.data.FuzzyInputData;
import com.adach.fuzzycon.fis.data.FuzzyOutputData;
import com.adach.fuzzycon.fis.partitioning.LinguisticValue;
import com.adach.fuzzycon.fis.partitioning.Point;
import com.adach.fuzzycon.fis.partitioning.Universe;
import com.adach.fuzzycon.fis.rules.Rule;
import com.adach.fuzzycon.fis.rules.RuleBase;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class InferenceEngine {

  private Map<Rule, Double> rules = new HashMap<>();
  private Map<String, Universe> universes = new HashMap<>();

  public InferenceEngine(String outputMembershipType) {
    RuleBase ruleBase = new RuleBase();
    ruleBase.readRules(new File("./config/ruleBase.xml"));
    for (Rule rule : ruleBase.getRules()) {
      rules.put(rule, 0.0);
    }

    loadOutputDataSpacePartitioningFromXml(
        new File("./config/outputDataSpacePartitioning/" + outputMembershipType + ".xml"));
  }

  public FuzzyOutputData generateFuzzyOutput(FuzzyInputData fuzzyInput) {
    FuzzyOutputData fuzzyOutputData = new FuzzyOutputData();
    Map<String, Double> maxCuts = new HashMap<>();
    calcRuleStrengthAndMaxCuts(fuzzyInput, maxCuts);
    fuzzyOutputData.acceleration = new Universe(universes.get("acceleration"));
    fuzzyOutputData.cutTo("positive", maxCuts.get("positive"));
    fuzzyOutputData.cutTo("negative", maxCuts.get("negative"));
    return fuzzyOutputData;
  }

  private void calcRuleStrengthAndMaxCuts(FuzzyInputData fuzzyInput, Map<String, Double> maxCuts) {

    for (Map.Entry<Rule, Double> entry : rules.entrySet()) {
      double ruleStrength = 1.0;
      for (Map.Entry<String, String> antecedent : entry.getKey().antecedents.entrySet()) {
        double membershipStrength = fuzzyInput.fuzzyValues.get(antecedent.getKey())
            .get(antecedent.getValue());
        if (ruleStrength > membershipStrength) {
          ruleStrength = membershipStrength;
        }
      }
      entry.setValue(ruleStrength);

      String name = entry.getKey().consequent.getValue();
      if (maxCuts.containsKey(name)) {
        maxCuts.put(name, max(maxCuts.get(name), ruleStrength));
      } else {
        maxCuts.put(name, ruleStrength);
      }
    }
  }

  private void loadOutputDataSpacePartitioningFromXml(File inputFile) {
    SAXBuilder saxBuilder = new SAXBuilder();
    try {
      Document document = saxBuilder.build(inputFile);
      Element rootXML = document.getRootElement();

      List<Element> universesXML = rootXML.getChildren();
      for (Element universeXML : universesXML) {
        Universe universeProg = new Universe();
        List<Element> linguisticValuesXML = universeXML.getChildren();
        for (Element linguisticValueXML : linguisticValuesXML) {
          LinguisticValue linguisticValueProg = new LinguisticValue();
          List<Element> pointsXML = linguisticValueXML.getChildren();
          for (Element pointXML : pointsXML) {
            Point pointProg = new Point(Double.parseDouble(pointXML.getAttributeValue("x")),
                Double.parseDouble(pointXML.getAttributeValue("y")));
            linguisticValueProg.points.put(pointXML.getAttributeValue("name").charAt(0), pointProg);
          }
          universeProg.linguisticValues
              .put(linguisticValueXML.getAttributeValue("name"), linguisticValueProg);
        }
        this.universes.put(universeXML.getAttributeValue("name"), universeProg);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}