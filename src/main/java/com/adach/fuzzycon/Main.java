package com.adach.fuzzycon;

public class Main {

  private WorldSimulator worldSimulator;
  private boolean randomBcChange;
  private String inputMembershipType;
  private String outputMembershipType;
  private boolean logToFile;
  private int executionSpeed;
  private String stateToLoadFilename = null;

  public static void main(String[] args) {
    Main main = new Main();
    main.parseArgs(args);
    main.run(args);
  }

  private void run(String args[]) {

    worldSimulator = new WorldSimulator(randomBcChange, inputMembershipType, outputMembershipType,
        logToFile, executionSpeed);
    if (stateToLoadFilename == null) {
      worldSimulator.randomizeCarsState();
    } else {
      worldSimulator.loadCarsStateFromFile(stateToLoadFilename);
    }
    worldSimulator.startSimulation();
  }

  private void parseArgs(String args[]) {
    if (args.length < 5) {
      throw new RuntimeException("too few arguments");
    } else {
      if (args[0].equals("true")) {
        randomBcChange = true;
      } else if (args[0].equals("false")) {
        randomBcChange = false;
      } else {
        throw new RuntimeException("invalid arguments");
      }

      if (args[1].equals("trapezoidal")) {
        inputMembershipType = args[1];
      } else if (args[1].equals("triangular")) {
        inputMembershipType = args[1];
      } else {
        throw new RuntimeException("invalid arguments");
      }

      if (args[2].equals("trapezoidal")) {
        outputMembershipType = args[2];
      } else if (args[2].equals("triangular")) {
        outputMembershipType = args[2];
      } else {
        throw new RuntimeException("invalid arguments");
      }

      if (args[3].equals("true")) {
        logToFile = true;
      } else if (args[3].equals("false")) {
        logToFile = false;
      } else {
        throw new RuntimeException("invalid arguments");
      }

      executionSpeed = Integer.parseInt(args[4]);
    }

    if (args.length == 6) {
      stateToLoadFilename = args[5];
    }
  }
}