package com.adach.fuzzycon;

import com.adach.fuzzycon.fis.FuzzyInferenceSystem;
import com.adach.fuzzycon.fis.data.CrispInputData;
import com.adach.fuzzycon.fis.data.CrispOutputData;
import com.adach.fuzzycon.model.Car;
import com.adach.fuzzycon.model.Lane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class WorldSimulator {

  //region SIMULATION PARAMETERS
  private final double CYCLE_LENGTH_SECONDS = 1;
  private final int ROAD_LENGTH_METERS = 2000;
  private final int MIN_RANDOM_VELOCITY_MS = 6;
  private final int MAX_RANDOM_VELOCITY_MS = 25;
  private final int RANDOM_BC_CHANGE_PROP = 10;
  private final double RANDOM_BC_CHANGE_VALUE = 0.5;

  private final boolean RANDOM_BC_CHANGE;
  private final boolean LOG_TO_FILE;
  private final int EXECUTION_SPEED;
  //endregion

  //region PRIVATE FIELDS
  private Timer timer;
  private TimerTask timerTask;
  private FuzzyInferenceSystem fis;
  private Controller controller;
  private Map<Character, Car> cars;

  private File logFile;
  private PrintWriter logWriter;

  private String simulationResult;
  private Lane previousLaneA;
  //endregion

  //region CONSTRUCTOR
  public WorldSimulator(boolean RANDOM_BC_CHANGE, String inputMembershipType,
      String outputMembershipType, boolean logToFile, int executionSpeed) {
    this.RANDOM_BC_CHANGE = RANDOM_BC_CHANGE;
    this.LOG_TO_FILE = logToFile;
    this.EXECUTION_SPEED = executionSpeed;
    if (logToFile) {
      initializeLogFile();
    }

    this.timer = new Timer();
    this.fis = new FuzzyInferenceSystem(inputMembershipType, outputMembershipType);
    this.controller = new Controller();
    this.cars = new HashMap<>();
  }
  //endregion

  //region RANDOMIZE OR LOAD STATE
  public void randomizeCarsState() {
    this.cars.put('A',
        new Car(getRandomVelocity(), 0, 0, Lane.RIGHT));
    this.cars.put('B',
        new Car(getRandomVelocity(), getRandomPosition(1000, 0), 0, Lane.RIGHT));
    this.cars.put('C',
        new Car(getRandomVelocity(), getRandomPosition(2000, 1000) - Car.CAR_LENGTH_METERS, 0,
            Lane.LEFT));
  }

  private double getRandomVelocity() {
    return new Random().nextInt(MAX_RANDOM_VELOCITY_MS + 1 - MIN_RANDOM_VELOCITY_MS)
        + MIN_RANDOM_VELOCITY_MS;
  }

  private double getRandomPosition(int max, int min) {
    return new Random().nextInt(max + 1 - min) + min;
  }

  public void loadCarsStateFromFile(String path) {
    File file = new File(path);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      String[] attributes;
      while ((line = reader.readLine()) != null) {
        attributes = line.split(" ");
        Character letter = attributes[0].charAt(0);
        double upperPosition = Double.parseDouble(attributes[1]);
        Lane lane = convertStringToLane(attributes[2]);
        double velocity = Double.parseDouble(attributes[3]);
        double acceleration = Double.parseDouble(attributes[4]);
        cars.put(letter, new Car(velocity, upperPosition, acceleration, lane));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Lane convertStringToLane(String text) {
    for (Lane b : Lane.values()) {
      if (b.name().equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
  //endregion

  //region SIMULATION START AND MAIN LOOP
  public void startSimulation() {
    printCarsData();
    if (LOG_TO_FILE) {
      logCarsData();
    }
    timerTask = new Cycle();
    this.timer.schedule(timerTask, 0, (int) (CYCLE_LENGTH_SECONDS * 1000 / EXECUTION_SPEED));
    Thread saveKeyListener = new Thread(new SaveKeyListener());
    saveKeyListener.start();
  }

  private class Cycle extends TimerTask {

    @Override
    public void run() {
      CrispInputData crispInput = new CrispInputData(
          cars.get('A').getVelocityMS(),
          cars.get('B').getVelocityMS(),
          cars.get('C').getVelocityMS(),
          cars.get('B').getLowerPositionM() - cars.get('A').getUpperPositionM(),
          cars.get('C').getLowerPositionM() - cars.get('A').getUpperPositionM(),
          ROAD_LENGTH_METERS - cars.get('A').getUpperPositionM(),
          cars.get('A').getLane());
      CrispOutputData crispOutput = fis.infer(crispInput);

      previousLaneA = cars.get('A').getLane();
      controller.modifyCarState(cars.get('A'), crispOutput);
      if (RANDOM_BC_CHANGE) {
        randomBCVelocityChange();
      }
      updateCarsByTime();

      printCarsData();
      if (LOG_TO_FILE) {
        logCarsData();
      }

      if (checkStopConditions()) {
        stopSimulation();
      }
    }
  }
  //endregion

  //region SIMULATION PRIVATE METHODS
  private void randomBCVelocityChange() {
    if (new Random().nextInt(100) + 1 <= RANDOM_BC_CHANGE_PROP) {
      double velocityChangeValue = RANDOM_BC_CHANGE_VALUE;
      if (new Random().nextInt(10) + 1 <= 5) {
        velocityChangeValue = -velocityChangeValue;
      }
      cars.get('B').changeVelocityByValue(velocityChangeValue);
      cars.get('C').changeVelocityByValue(velocityChangeValue);
    }
  }

  private void updateCarsByTime() {
    cars.get('A').changeVelocityByTime(CYCLE_LENGTH_SECONDS);

    cars.get('A').changePositionByTime(CYCLE_LENGTH_SECONDS, true);
    cars.get('B').changePositionByTime(CYCLE_LENGTH_SECONDS, true);
    cars.get('C').changePositionByTime(CYCLE_LENGTH_SECONDS, false);
  }

  private boolean checkStopConditions() {

    if (cars.get('A').getLane() == cars.get('B').getLane() &&
        cars.get('A').getLowerPositionM() > cars.get('B').getUpperPositionM()) {
      if (cars.get('A').getLane() != previousLaneA) {
        simulationResult = "A had successfully overtaken B!";
      } else {
        simulationResult = "A crashed with B!";
      }
      return true;
    }

    if (cars.get('A').getLane() == cars.get('B').getLane() &&
        cars.get('A').getUpperPositionM() >= cars.get('B').getLowerPositionM() &&
        cars.get('A').getLowerPositionM() <= cars.get('B').getUpperPositionM()) {
      simulationResult = "A crashed with B!";
      return true;
    }

    if (cars.get('A').getLane() == cars.get('C').getLane() &&
        cars.get('A').getUpperPositionM() >= cars.get('C').getLowerPositionM() &&
        cars.get('A').getLowerPositionM() <= cars.get('C').getUpperPositionM()) {
      simulationResult = "A crashed with C!";
      return true;
    }

    if (cars.get('A').getUpperPositionM() >= ROAD_LENGTH_METERS) {
      simulationResult = "A reached end of the road!";
      return true;
    }

    if (cars.get('B').getUpperPositionM() >= ROAD_LENGTH_METERS) {
      simulationResult = "B reached end of the road!";
      return true;
    }

    return false;
  }

  private void stopSimulation() {
    timerTask.cancel();
    timer.cancel();
    timer.purge();
    printSimulationResult();
    Date stopDate = new Date();
    saveSimulationResultToFile(stopDate);
    //saveCarsStateToFile(true, stopDate);
    if (LOG_TO_FILE) {
      finalizeLogFile(stopDate);
    }
    System.exit(0);
  }
  //endregion

  //region SAVING CARS STATE
  private class SaveKeyListener implements Runnable {

    Scanner sc = new Scanner(System.in);

    @Override
    public void run() {
      while (true) {
        if (sc.hasNext()) {
          String input = sc.next();
          if (input.equals("s")) {
            saveCarsStateToFile(false, new Date());
            System.out.println("Cars state saved!");
            System.out.println();
          }
        }
      }
    }
  }

  private void saveCarsStateToFile(boolean simulationFinished, Date date) {
    String path;
    if (simulationFinished) {
      path = "state-" + new SimpleDateFormat("yyyyMMddHHmmssSSS'-final.txt'").format(date);
    } else {
      path = "state-" + new SimpleDateFormat("yyyyMMddHHmmssSSS'.txt'").format(date);
    }
    File file = new File(path);
    try (PrintWriter writer = new PrintWriter(file)) {
      for (Map.Entry<Character, Car> entry : cars.entrySet()) {
        Car car = entry.getValue();
        writer.println(entry.getKey() + " " + car.getUpperPositionM() + " " +
            car.getLane() + " " + car.getVelocityMS() + " " + car.getAccelerationMS2());
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  //endregion

  //region PRINTING/SAVING SIMULATION RESULTS
  private void printSimulationResult() {
    System.out.println(simulationResult);
  }

  private void saveSimulationResultToFile(Date date) {
    String path;
    path = "result-" + new SimpleDateFormat("yyyyMMddHHmmssSSS'.txt'").format(date);
    File file = new File(path);
    try (PrintWriter writer = new PrintWriter(file)) {
      writer.println(simulationResult);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  //endregion

  //region LOG FILE
  private void initializeLogFile() {
    String path = "log-tmp.txt";
    logFile = new File(path);
    try {
      logWriter = new PrintWriter(logFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void finalizeLogFile(Date stopDate) {
    logWriter.close();
    Path source = Paths.get("log-tmp.txt");
    try {
      Files.move(source, source.resolveSibling(
          "log-" + new SimpleDateFormat("yyyyMMddHHmmssSSS'.txt'").format(stopDate)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  //endregion

  //region PRINTING/LOGGING CARS DATA
  private void printCarsData() {
    System.out.println(
        "A: || lane: " + cars.get('A').getLane() + " || pos: " + cars.get('A').getUpperPositionM()
            + " || v: " + cars.get('A').getVelocityMS() + " || a: " + cars.get('A')
            .getAccelerationMS2());
    System.out.println(
        "B: || lane: " + cars.get('B').getLane() + " || pos: " + cars.get('B').getUpperPositionM()
            + " || v: " + cars.get('B').getVelocityMS() + " || a: " + cars.get('B')
            .getAccelerationMS2());
    System.out.println(
        "C: || lane: " + cars.get('C').getLane() + " || pos: " + cars.get('C').getUpperPositionM()
            + " || v: " + cars.get('C').getVelocityMS() + " || a: " + cars.get('C')
            .getAccelerationMS2());
    System.out.println();
  }

  private void logCarsData() {
    logWriter.println(
        "A: || lane: " + cars.get('A').getLane() + " || pos: " + cars.get('A').getUpperPositionM()
            + " || v: " + cars.get('A').getVelocityMS() + " || a: " + cars.get('A')
            .getAccelerationMS2());
    logWriter.println(
        "B: || lane: " + cars.get('B').getLane() + " || pos: " + cars.get('B').getUpperPositionM()
            + " || v: " + cars.get('B').getVelocityMS() + " || a: " + cars.get('B')
            .getAccelerationMS2());
    logWriter.println(
        "C: || lane: " + cars.get('C').getLane() + " || pos: " + cars.get('C').getUpperPositionM()
            + " || v: " + cars.get('C').getVelocityMS() + " || a: " + cars.get('C')
            .getAccelerationMS2());
    logWriter.println();
  }
  //endregion
}