package domain;

import java.util.List;

public class PasswordEvaluationResult {
    private final String strength;
    private final int score;
    private final List<String> feedback;

    public PasswordEvaluationResult(String strength, int score, List<String> feedback) {
        this.strength = strength;
        this.score = score;
        this.feedback = feedback;
    }

    public String getStrength() {
        return strength;
    }

    public int getScore() {
        return score;
    }

    public List<String> getFeedback() {
        return feedback;
    }
}