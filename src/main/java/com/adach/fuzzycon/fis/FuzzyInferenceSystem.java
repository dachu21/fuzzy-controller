package com.adach.fuzzycon.fis;

import com.adach.fuzzycon.fis.data.CrispOutputData;
import com.adach.fuzzycon.fis.data.FuzzyInputData;
import com.adach.fuzzycon.fis.data.FuzzyOutputData;
import com.adach.fuzzycon.model.Lane;
import com.adach.fuzzycon.fis.data.CrispInputData;

public class FuzzyInferenceSystem {

  private Fuzzifier fuzzifier;
  private InferenceEngine inferenceEngine;
  private Defuzzifier defuzzifier;
  private LaneSelector laneSelector;

  public FuzzyInferenceSystem(String inputMembershipType, String outputMembershipType) {
    fuzzifier = new Fuzzifier(inputMembershipType);
    inferenceEngine = new InferenceEngine(outputMembershipType);
    defuzzifier = new Defuzzifier();
    laneSelector = new LaneSelector();
  }

  public CrispOutputData infer(CrispInputData crispInput) {
    FuzzyInputData fuzzyInput = fuzzifier.fuzzify(crispInput);
    FuzzyOutputData fuzzyOutput = inferenceEngine.generateFuzzyOutput(fuzzyInput);
    Lane newLaneA = laneSelector.selectLane(crispInput);
    return defuzzifier.defuzzify(fuzzyOutput, newLaneA);
  }
}