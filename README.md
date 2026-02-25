# Sudoku Generator & Solver

A Java Swing desktop application that generates and validates Sudoku puzzles with multiple difficulty levels.

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![JUnit](https://img.shields.io/badge/Tests-JUnit%205-green) ![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue)

## Features

- **Three difficulty levels** — Easy (36 removed), Medium (46 removed), Hard (52 removed)
- **Solution checker** — highlights incorrect cells in red and correct cells in green
- **Show solution** — reveals the full solution on demand
- **Live timer** — tracks how long you've been solving
- **Input validation** — only accepts digits 1–9
- **Visual 3×3 box borders** — proper Sudoku grid styling
- **Unit tested** — 8 JUnit 5 tests covering generation, validation, and difficulty scaling

## Screenshots

> _Add a screenshot here by dragging an image into this section on GitHub_

## Getting Started

### Prerequisites
- Java 17 or higher
- JUnit 5 (for running tests) — add via Maven or Gradle

### Run the app
```bash
javac *.java
java SudokuApp
```

### Run the tests
If using Maven, add this to your `pom.xml`:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```
Then run:
```bash
mvn test
```

## Project Structure

```
├── SudokuApp.java            # Swing UI — grid, buttons, timer
├── SudokuGenerator.java      # Puzzle generation, solution storage, validation
└── SudokuGeneratorTest.java  # JUnit 5 unit tests
```

## How It Works

1. **Generation** — fills the three diagonal 3×3 boxes randomly, then uses backtracking to complete the rest of the board
2. **Difficulty** — removes a set number of cells based on selected difficulty
3. **Validation** — compares the user's input against the stored solution cell by cell

## Tech Stack

- Java 17
- Java Swing
- JUnit 5
