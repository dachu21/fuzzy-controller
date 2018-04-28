package com.adach.fuzzycon.fis.partitioning;

import java.util.HashMap;
import java.util.Map;

public class Universe {

  public Map<String, LinguisticValue> linguisticValues = new HashMap<>();

  public Universe() {

  }

  public Universe(Universe other) {
    for (Map.Entry<String, LinguisticValue> linguisticValueEntry : other.linguisticValues
        .entrySet()) {
      this.linguisticValues
          .put(linguisticValueEntry.getKey(), new LinguisticValue(linguisticValueEntry.getValue()));
    }
  }
}
