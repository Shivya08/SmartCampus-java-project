package com.smartcampus.exception;

/**
 * Thrown when a student's total credits would exceed the academic cap (e.g. 27 credits).
 */
public class CreditLimitExceededException extends CampusException {
    private final int currentCredits;
    private final int additionalCredits;
    private final int maxLimit;

    public CreditLimitExceededException(int currentCredits, int additionalCredits, int maxLimit) {
        super("CREDIT_LIMIT_EXCEEDED",
                String.format("Credit Limit Exceeded: Current credits (%d) + Course credits (%d) exceeds max allowable (%d).",
                        currentCredits, additionalCredits, maxLimit));
        this.currentCredits = currentCredits;
        this.additionalCredits = additionalCredits;
        this.maxLimit = maxLimit;
    }

    public int getCurrentCredits() {
        return currentCredits;
    }

    public int getAdditionalCredits() {
        return additionalCredits;
    }

    public int getMaxLimit() {
        return maxLimit;
    }
}
