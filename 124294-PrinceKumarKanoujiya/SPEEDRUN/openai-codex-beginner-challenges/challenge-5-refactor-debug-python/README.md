# Challenge 5 - Refactor and Debug

## Objective
Refactor messy quiz engine into class-based, testable Python code.

## Problems in Original
Global mutable state, fragile error handling, tight I/O coupling.

## Improvements
Class design, validation, safe score retrieval, pure score calculation method.

## Run tests
```bash
pytest test_quiz_engine.py -v
```

## Codex Workflow Used
Analyze smells -> redesign API -> write tests -> verify behavior.
