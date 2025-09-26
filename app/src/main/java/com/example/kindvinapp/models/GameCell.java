package com.example.kindvinapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameCell implements Serializable {
    private final int number;
    private CellType type;
    private final List<Team> teamsOnCell;

    public GameCell(int number) {
        this.number = number;
        this.type = CellType.EMPTY;
        this.teamsOnCell = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public List<Team> getTeamsOnCell() {
        return teamsOnCell;
    }

    public void addTeam(Team team) {
        if (!teamsOnCell.contains(team)) {
            teamsOnCell.add(team);
        }
    }

    public void removeTeam(Team team) {
        teamsOnCell.remove(team);
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }
}

