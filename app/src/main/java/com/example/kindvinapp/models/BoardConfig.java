package com.example.kindvinapp.models;

import java.io.Serializable;

public class BoardConfig implements Serializable {
    private int challengeCount;
    private int attackCount;
    private int teleportForward5Count;
    private int teleportForward7Count;
    private int teleportBackward5Count;
    private int teleportBackward7Count;

    public BoardConfig() {}

    public int getChallengeCount() { return challengeCount; }
    public int getAttackCount() { return attackCount; }
    public int getTeleportForward5Count() { return teleportForward5Count; }
    public int getTeleportForward7Count() { return teleportForward7Count; }
    public int getTeleportBackward5Count() { return teleportBackward5Count; }
    public int getTeleportBackward7Count() { return teleportBackward7Count; }

    public void setChallengeCount(int challengeCount) { this.challengeCount = challengeCount; }
    public void setAttackCount(int attackCount) { this.attackCount = attackCount; }
    public void setTeleportForward5Count(int teleportForward5Count) { this.teleportForward5Count = teleportForward5Count; }
    public void setTeleportForward7Count(int teleportForward7Count) { this.teleportForward7Count = teleportForward7Count; }
    public void setTeleportBackward5Count(int teleportBackward5Count) { this.teleportBackward5Count = teleportBackward5Count; }
    public void setTeleportBackward7Count(int teleportBackward7Count) { this.teleportBackward7Count = teleportBackward7Count; }
}

