package com.adach.fuzzycon.fis;

import com.adach.fuzzycon.fis.data.CrispInputData;
import com.adach.fuzzycon.fis.data.FuzzyInputData;
import com.adach.fuzzycon.fis.partitioning.LinguisticValue;
import com.adach.fuzzycon.fis.partitioning.Point;
import com.adach.fuzzycon.fis.partitioning.Universe;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class Fuzzifier {

  String inputMembershipType;
  Map<String, Universe> universes = new HashMap<>();

  public Fuzzifier(String inputMembershipType) {
    this.inputMembershipType = inputMembershipType;
    loadInputDataSpacePartitioningFromXml(
        new File("./config/inputDataSpacePartitioning/" + this.inputMembershipType + ".xml"));
  }

  public FuzzyInputData fuzzify(CrispInputData crispInput) {

    FuzzyInputData fuzzyInputData = new FuzzyInputData();
    fuzzifySpecificInputValue("velocity_A", crispInput.velocityA,
        fuzzyInputData.fuzzyValues.get("velocity_A"));
    fuzzifySpecificInputValue("velocity_B", crispInput.velocityB,
        fuzzyInputData.fuzzyValues.get("velocity_B"));
    fuzzifySpecificInputValue("velocity_C", crispInput.velocityC,
        fuzzyInputData.fuzzyValues.get("velocity_C"));
    fuzzifySpecificInputValue("distance_AB", crispInput.distanceAB,
        fuzzyInputData.fuzzyValues.get("distance_AB"));
    fuzzifySpecificInputValue("distance_AC", crispInput.distanceAC,
        fuzzyInputData.fuzzyValues.get("distance_AC"));
    fuzzifySpecificInputValue("distance_AtoEnd", crispInput.distanceAtoEnd,
        fuzzyInputData.fuzzyValues.get("distance_AtoEnd"));
    return fuzzyInputData;
  }

  private void fuzzifySpecificInputValue(String universeKey, double crispInputValue,
      Map<String, Double> destination) {
    Universe universe = universes.get(universeKey);
    for (Entry<String, LinguisticValue> linguisticValue : universe.linguisticValues.entrySet()) {
      double fuzzyValue = membershipFunction(linguisticValue.getValue().points, crispInputValue);
      destination.put(linguisticValue.getKey(), fuzzyValue);
    }
  }

  private double membershipFunction(Map<Character, Point> points, double crispValue) {
    switch (inputMembershipType) {
      case "trapezoidal":
        return trapezoidalMembershipFunction(points, crispValue);
      case "triangular":
        return triangularMembershipFunction(points, crispValue);
      default:
        throw new RuntimeException("No such membership function!");
    }
  }

  private double trapezoidalMembershipFunction(Map<Character, Point> points, double crispValue) {
    if (points.containsKey('a') && points.containsKey('b')) {
      if (points.containsKey('c') && points.containsKey('d')) {
        double a = points.get('a').x;
        double b = points.get('b').x;
        double c = points.get('c').x;
        double d = points.get('d').x;
        if (crispValue <= a) {
          return 0;
        } else if (a < crispValue && crispValue <= b) {
          return (crispValue - a) / (b - a);
        } else if (b < crispValue && crispValue <= c) {
          return 1;
        } else if (c < crispValue && crispValue <= d) {
          return (d - crispValue) / (d - c);
        } else { // if (crispValue > d)
          return 0;
        }
      } else {
        double a = points.get('a').x;
        double b = points.get('b').x;
        if (crispValue <= a) {
          return 0;
        } else if (a < crispValue && crispValue <= b) {
          return (crispValue - a) / (b - a);
        } else { // if (b < crispValue)
          return 1;
        }
      }
    } else if (points.containsKey('c') && points.containsKey('d')) {
      double c = points.get('c').x;
      double d = points.get('d').x;
      if (crispValue <= c) {
        return 1;
      } else if (c < crispValue && crispValue <= d) {
        return (d - crispValue) / (d - c);
      } else { // if (crispValue > d)
        return 0;
      }
    } else {
      throw new RuntimeException(
          "trapezoidal.xml not prepared correctly (it should contain 'abcd', 'ab' or 'cd' points combinations only");
    }
  }

  private double triangularMembershipFunction(Map<Character, Point> points, double crispValue) {
    if (points.containsKey('a') && points.containsKey('b') && points.containsKey('c')) {
      double a = points.get('a').x;
      double b = points.get('b').x;
      double c = points.get('c').x;
      if (crispValue <= a) {
        return 0;
      } else if (a < crispValue && crispValue <= b) {
        return (crispValue - a) / (b - a);
      } else if (b < crispValue && crispValue <= c) {
        return (c - crispValue) / (c - b);
      } else { // if (crispValue > c)
        return 0;
      }
    } else if (points.containsKey('a') && points.containsKey('b') && !points.containsKey('c')) {
      double a = points.get('a').x;
      double b = points.get('b').x;
      if (crispValue <= a) {
        return 0;
      } else if (a < crispValue && crispValue <= b) {
        return (crispValue - a) / (b - a);
      } else { // if (crispValue > b)
        return 0;
      }
    } else if (!points.containsKey('a') && points.containsKey('b') && points.containsKey('c')) {
      double b = points.get('b').x;
      double c = points.get('c').x;
      if (crispValue < b) {
        return 0;
      } else if (b <= crispValue && crispValue <= c) {
        return (c - crispValue) / (c - b);
      } else { // if (crispValue > c)
        return 0;
      }
    }
    throw new RuntimeException(
        "triangular.xml not prepared correctly (it should contain 'abc', 'ab' or 'cd' points combinations only");
  }

  private void loadInputDataSpacePartitioningFromXml(File inputFile) {
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