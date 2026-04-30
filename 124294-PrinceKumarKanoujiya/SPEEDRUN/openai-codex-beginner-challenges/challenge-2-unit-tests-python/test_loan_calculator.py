import pytest
from loan_calculator import calculate_monthly_payment

@pytest.mark.parametrize("principal,rate,months,expected", [
    (10000, 5, 24, 438.71),
    (12000, 0, 12, 1000.00),
    (2000, 12, 1, 2020.00),
    (1000000, 1.5, 360, 3451.99),
])
def test_calculate_monthly_payment_positive_cases(principal, rate, months, expected):
    assert calculate_monthly_payment(principal, rate, months) == pytest.approx(expected, rel=1e-2)

def test_principal_zero_raises_value_error():
    with pytest.raises(ValueError): calculate_monthly_payment(0, 5, 12)

def test_negative_principal_raises_value_error():
    with pytest.raises(ValueError): calculate_monthly_payment(-100, 5, 12)

def test_negative_interest_raises_value_error():
    with pytest.raises(ValueError): calculate_monthly_payment(1000, -1, 12)

def test_zero_months_raises_value_error():
    with pytest.raises(ValueError): calculate_monthly_payment(1000, 1, 0)

def test_float_months_raises_value_error():
    with pytest.raises(ValueError): calculate_monthly_payment(1000, 1, 12.5)

def test_non_numeric_argument_raises_type_error():
    with pytest.raises(TypeError): calculate_monthly_payment("1000", 1, 12)
