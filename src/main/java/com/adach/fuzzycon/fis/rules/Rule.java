package com.adach.fuzzycon.fis.rules;

import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;

public class Rule {

  public Map<String, String> antecedents = new HashMap<>();
  public Pair<String, String> consequent;

  public Rule(Map<String, String> antecedents, Pair<String, String> consequent) {
    this.antecedents = antecedents;
    this.consequent = consequent;
  }
}