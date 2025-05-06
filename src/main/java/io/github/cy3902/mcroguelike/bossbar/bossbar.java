package io.github.cy3902.mcroguelike.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import io.github.cy3902.mcroguelike.MCRogueLike;
import io.github.cy3902.mcroguelike.config.Lang;

public class bossbar {
    private final MCRogueLike mcroguelike  = MCRogueLike.getInstance();
    private final Lang lang = mcroguelike.getLang();
    private BossBar bossBar;
    private String title;
    private double health = 0;
    private double maxHealth = 0;

    public bossbar(String title, double health, double maxHealth) {
        this.title = title;
        this.health = health;
        this.maxHealth = maxHealth;
    }

    public void createBossBar() {
        bossBar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1);
    }

    public void setBossBar(double health, double maxHealth) {
        bossBar.setProgress(health / maxHealth);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }       
    
    public void bindPlayer(Player player) {
        bossBar.addPlayer(player);
    }

    public void unbindPlayer(Player player) {
        bossBar.removePlayer(player);
    }


}
