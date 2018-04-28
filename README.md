# Fuzzy Controller (2017)
Java application implementing fuzzy controller used in road simulator (cars overtaking).

-------------------

### Introduction

There are three cars on the road (2000m): A, B, C. Cars A and B are driving north, while car C is driving south. In the beginning, car A is behind car B and then tries to safely overtake it.

### Instruction

##### CLI arguments

You need to provide at least 5 arguments.

CLI arguments pattern:
[random_BC] [input_data_func] [output_data_func] [log_to_file] [execution_speed] (optional)[input_file]

CLI arguments description:
- random_BC - random velocity changes (cars B and C) (true/false)
- input_data_func - membership function for input data (triangular/trapezoidal)
- output_data_func - membership function for output data (triangular/trapezoidal)
- log_to_file - flag indicating whether to log the whole simulation to file (true/false)
- execution_speed - execution speed multiplier ('1' means real-time)
- input_file - initial simulation state (if not present, initial state is random)

##### Configuration files

Rule base and membership functions can be modified in *.xml* files in *config* directory.

##### Input file
Input file is a text file and should look as follows:

```
A posA laneA va aA
B posB laneB vb aB
C posC laneC vc aC
```
e.g.
```
A 0.0 RIGHT 6.0 0.0
B 100.0 RIGHT 15.0 0.0
C 500.0 LEFT 25.0 0.0
```
where:
- pos - initial position (most south point)
- lane - car's lane (RIGHT/LEFT)
- v - initial velocity
- a - initial acceleration
