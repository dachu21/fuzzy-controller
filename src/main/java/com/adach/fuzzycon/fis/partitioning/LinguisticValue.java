package com.adach.fuzzycon.fis.partitioning;

import java.util.HashMap;
import java.util.Map;

public class LinguisticValue {

  public Map<Character, Point> points = new HashMap<>();

  public LinguisticValue() {

  }

  public LinguisticValue(LinguisticValue other) {
    for (Map.Entry<Character, Point> pointEntry : other.points.entrySet()) {
      points.put(pointEntry.getKey(), new Point(pointEntry.getValue().x, pointEntry.getValue().y));
    }
  }
}
