package com.csc570.rsmith.playerai;

import com.csc570.rsmith.mechanics.player.TTRComputerPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rsmith on 3/21/17.
 */
public class PlayerStatCounter {

    private TTRComputerPlayer player;

    private Map<String, Double> stats = new HashMap<>();

    public void setPlayer(TTRComputerPlayer player) {
        this.player = player;
    }

    public void addStat(String statName, double value) {
        stats.putIfAbsent(statName, 0.0);
        stats.put(statName, stats.get(statName) + value);
    }

    public Map<String, Double> getStats() {
        return stats;
    }

}
