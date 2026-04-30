def calculate_monthly_payment(principal, annual_rate_percent, months):
    """
    Calculate the fixed monthly repayment on an amortising loan.
    Formula: M = P * r * (1+r)^n / ((1+r)^n - 1)
    where r = monthly interest rate, n = number of months.
    """
    if not all(isinstance(v, (int, float)) for v in [principal, annual_rate_percent, months]):
        raise TypeError("All arguments must be numeric")
    if principal <= 0:
        raise ValueError("Principal must be greater than zero")
    if annual_rate_percent < 0:
        raise ValueError("Annual rate cannot be negative")
    if months <= 0 or not isinstance(months, int):
        raise ValueError("Months must be a positive integer")

    if annual_rate_percent == 0:
        return round(principal / months, 2)

    r = annual_rate_percent / 100 / 12
    payment = principal * r * (1 + r) ** months / ((1 + r) ** months - 1)
    return round(payment, 2)
