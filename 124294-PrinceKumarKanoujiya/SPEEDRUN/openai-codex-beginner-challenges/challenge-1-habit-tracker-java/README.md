# Challenge 1 - Habit Tracker (Java)

## Objective
Build a CLI habit tracker with JSON persistence.

## Features
- Add daily/weekly habits
- Log completion by date
- Show summary, streak, list, weekly report
- Loads and saves `habits.json`

## Compile and Run
```bash
javac HabitTrackerApp.java
java HabitTrackerApp
```

## Manual Test Cases
1. Add a habit with valid frequency.
2. Add with invalid frequency -> clear validation error.
3. Log valid date and invalid date.
4. Restart app and confirm data reload.

## Codex Workflow Used
Prompt -> generate methods -> test flows -> finalize docs.
