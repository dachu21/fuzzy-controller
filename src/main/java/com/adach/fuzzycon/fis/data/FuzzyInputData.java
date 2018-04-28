package com.adach.fuzzycon.fis.data;

import java.util.HashMap;
import java.util.Map;

public class FuzzyInputData {

  public Map<String, Map<String, Double>> fuzzyValues = new HashMap<>();

  public FuzzyInputData() {
    fuzzyValues.put("velocity_A", new HashMap<>());
    fuzzyValues.put("velocity_B", new HashMap<>());
    fuzzyValues.put("velocity_C", new HashMap<>());
    fuzzyValues.put("distance_AB", new HashMap<>());
    fuzzyValues.put("distance_AC", new HashMap<>());
    fuzzyValues.put("distance_AtoEnd", new HashMap<>());
  }
}