package com.codetest.service.leaderboard;

public enum LeaderboardEnum {
    LEADERBOARD(new LeaderboardServiceImpl()),
    RANK_REPEATED_LEADERBOARD(new RankRepeatedLeaderboardServiceImpl()),
    ;

    private final LeaderBoardService service;

    LeaderboardEnum(LeaderBoardService service) {
        this.service = service;
    }

    public LeaderBoardService getService() {
        return service;
    }
}
