package fr.ariloxe.arena.kits.offensive;

import fr.ariloxe.arena.kits.ArenaKit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Ariloxe
 */
public class Werewolf extends ArenaKit {

    public Werewolf(Player player) {
        super("Loup-Garou", player);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(entityDamageByEntityEvent.getDamager() == getPlayer() && entityDamageByEntityEvent.getEntity() instanceof Player){
            Player victim = (Player) entityDamageByEntityEvent.getEntity();

            if(victim.getHealth() >= 16)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage());
            else if(victim.getHealth() >= 10)
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.35);
            else
                entityDamageByEntityEvent.setDamage(entityDamageByEntityEvent.getDamage()*1.7);
        }
    }


}
