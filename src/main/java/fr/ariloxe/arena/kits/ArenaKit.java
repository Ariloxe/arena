package fr.ariloxe.arena.kits;

import fr.ariloxe.arena.ArenaAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public abstract class ArenaKit implements Listener {

    private final String name;
    private Player player;

    public ArenaKit(String name, Player player) {
        this.name = name;
        this.player = player;
    }

    public void equip(){
        Bukkit.getPluginManager().registerEvents(this, ArenaAPI.getInstance());
        ArenaAPI.getInstance().getArenaKitMap().put(player.getUniqueId(), this);
        onEquip();
    }

    public void onEquip(){
    }

    public void dead(){
        HandlerList.unregisterAll(this);
        ArenaAPI.getInstance().getArenaKitMap().remove(player.getUniqueId());

        onDeath();
    }

    public void deletePlayer(){
        this.player = null;
    }

    public void onDeath(){
    }

    public void onRightClick(ItemStack itemStack){

    }

    public void onLeftclick(ItemStack itemStack){

    }

    public void onDrop(ItemStack itemStack){

    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }
}
