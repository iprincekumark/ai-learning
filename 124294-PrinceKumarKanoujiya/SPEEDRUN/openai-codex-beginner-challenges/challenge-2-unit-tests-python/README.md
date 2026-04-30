# Challenge 2 - Unit Tests (Python)

## Objective
Validate loan calculator behavior with pytest positive and negative tests.

## Files
- `loan_calculator.py`
- `test_loan_calculator.py`

## Test Coverage
Covers normal loans, zero interest, one-month, large principal, and all specified invalid inputs.

## Run
```bash
pytest test_loan_calculator.py -v
```

## Codex Workflow Used
Define expected values -> parametrize positive tests -> add exception tests.
