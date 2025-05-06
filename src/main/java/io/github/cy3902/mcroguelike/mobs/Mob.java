package io.github.cy3902.mcroguelike.mobs;

import io.github.cy3902.mcroguelike.abstracts.AbstractMob;

public class Mob extends AbstractMob {
    public Mob(String mobId, double healthMultiplier, double damageMultiplier, 
               double speedMultiplier, boolean isKeyMob, boolean isGuardTarget, int count) {
        super(mobId, healthMultiplier, damageMultiplier, speedMultiplier, isKeyMob, isGuardTarget, count);
    }
} 