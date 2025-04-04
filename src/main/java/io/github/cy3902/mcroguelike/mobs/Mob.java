package io.github.cy3902.mcroguelike.mobs;

import io.github.cy3902.mcroguelike.abstracts.AbstractsMob;

public class Mob extends AbstractsMob {
    public Mob(String mobId, double healthMultiplier, double damageMultiplier, 
               double speedMultiplier, boolean isKeyMob, boolean isGuardTarget) {
        super(mobId, healthMultiplier, damageMultiplier, speedMultiplier, isKeyMob, isGuardTarget);
    }
} 