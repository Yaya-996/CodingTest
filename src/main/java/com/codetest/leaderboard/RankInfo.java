package com.codetest.leaderboard;

public class RankInfo {
    private String playerId;
    private int score;
    private int rank;

    public RankInfo() {
    }

    public RankInfo(String playerId, int score, int rank) {
        this.playerId = playerId;
        this.score = score;
        this.rank = rank;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
