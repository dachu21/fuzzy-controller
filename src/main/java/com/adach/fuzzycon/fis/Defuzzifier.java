package com.adach.fuzzycon.fis;

import com.adach.fuzzycon.fis.data.CrispOutputData;
import com.adach.fuzzycon.fis.data.FuzzyOutputData;
import com.adach.fuzzycon.fis.partitioning.LinguisticValue;
import com.adach.fuzzycon.fis.partitioning.Point;
import com.adach.fuzzycon.model.Lane;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Defuzzifier {

  public CrispOutputData defuzzify(FuzzyOutputData fuzzyOutput, Lane newLaneA) {

    double valuesSum = 0;
    double weightsSum = 0;
    for (LinguisticValue lv : fuzzyOutput.acceleration.linguisticValues.values()) {
      Double value = calculateCenterX(lv.points.values());
      Double weight = calculateMaxValue(lv.points.values());
      valuesSum += value * weight;
      weightsSum += weight;
    }

    if (weightsSum == 0) {
      return new CrispOutputData(0, newLaneA);
    } else {
      return new CrispOutputData(valuesSum / weightsSum, newLaneA);
    }
  }

  private double calculateCenterX(Collection<Point> points) {
    List<Point> pointList = new ArrayList<Point>(points);
    Collections.sort(pointList);
    return (pointList.get(0).x + pointList.get(pointList.size() - 1).x) / 2;
  }

  private double calculateMaxValue(Collection<Point> points) {
    double maxValue = 0;
    for (Point point : points) {
      if (point.y > maxValue) {
        maxValue = point.y;
      }
    }
    return maxValue;
  }
}