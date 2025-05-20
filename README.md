## Tucil3_13523016_13523047
# Rush Hour Puzzle Solver

This program solves the Rush Hour puzzle using three pathfinding algorithms:
1. Uniform Cost Search (UCS)
2. Greedy Best First Search
3. A* Search

## Description

Rush Hour is a sliding block puzzle where the goal is to move a specific car (the "primary piece") out of a grid through an exit by sliding other blocking vehicles out of its way. Each vehicle can only move horizontally or vertically along its orientation.

This solver implements multiple pathfinding algorithms and heuristics to find the optimal solution to any Rush Hour puzzle configuration.

## Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher

### Building from Source
1. Clone this repository
2. Navigate to the project directory
3. Compile Java files:
   ```
   cd src
   javac -d ../bin Main.java 
   ```
## Usage

### Running the Program
1. Navigate to the project directory
2. Run the application:
   ```
   cd bin
   java Main
   ```
3. Follow the prompts to:
   1. Browse to select a puzzle file
   2. Choose an algorithm:
      - Uniform Cost Search (UCS)
      - Greedy Best First Search (GBFS)
      - A* Search
   3. If using GBFS or A*, select a heuristic:
      - Distance to Exit
      - Blocking Vehicles
      - Combined (Distance + Blocking Vehicles)
   4. Click "Solve" to find a solution
   5. Use the controls to:
      - Play/Pause the solution animation
      - Step forward/backward through solution states
      - Save the solution to a file

### Input File Format
The program reads puzzle configurations from text files with the following format:
```
A B
N
konfigurasi_papan
```

Where:
- `A B` - Dimensions of the board (A×B)
- `N` - Number of pieces (excluding the primary piece)
- `konfigurasi_papan` - Board configuration where:
  - `P` - Primary piece that needs to exit
  - `K` - Exit location
  - `.` - Empty cell
  - Other characters - Other vehicles on the board

Example:
```
6 6
11
AAB..F
..BCDF
GPPCDFK
GH.III
GHJ...
LLJMM.
```

## Algorithms

### Uniform Cost Search (UCS)
- Uses cost of moves to find the shortest path
- Complete and optimal for Rush Hour puzzles
- Does not use heuristics

### Greedy Best First Search
- Uses heuristics to guide the search towards the goal
- Not guaranteed to find the optimal solution
- Faster than UCS for many puzzles

### A* Search
- Combines UCS and Greedy approaches
- Uses both path cost and heuristics
- Complete and optimal with admissible heuristics
- More efficient than UCS for many puzzles

## Heuristics

The program implements three heuristics:

1. **Distance to Exit**
   - Measures the distance between the primary piece and the exit

2. **Blocking Vehicles**
   - Counts the number of vehicles blocking the primary piece's path to the exit

3. **Combined**
   - Combines both distance and blocking vehicles heuristics

## Authors

- Clarissa Nethania Tambunan 13523016
- Indah Novita Tangdililing 13523047
