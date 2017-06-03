package me.Mew32.EffectStreaker;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerItemEventListener implements Listener{

    private final Collection<PotionEffect> effects;
    private Material material;
    private Map<Player, Integer> kills;
    private String name;
    private String message;
    private int killsneeded;

    public PlayerItemEventListener(Material material, String name, String message){
        this.effects = new ArrayList<PotionEffect>();
        this.material = material;
        this.kills = new HashMap<Player, Integer>();
        this.name = name;
        this.killsneeded = 3;
        setMessage(message);
    }

    @EventHandler
    public void onItemRightClick (PlayerInteractEvent event) {
        if(event.hasItem() && isMaterial(event.getMaterial()) && nameIs(event.getItem())){//&& nameIs(event.getItem()) && kills.get(event.getPlayer()) == 3
            event.setCancelled(true);
            if(!kills.containsKey(event.getPlayer())){
                kills.put(event.getPlayer(),0);
            }
            if(kills.get(event.getPlayer()) >= killsneeded ) {
                event.getPlayer().addPotionEffects(effects);
                kills.put(event.getPlayer(), kills.get(event.getPlayer()) - killsneeded);
            }else{
                event.getPlayer().sendMessage(message.replaceAll("%kills%",""+(killsneeded - kills.get(event.getPlayer()))));
            }
        }
    }

    private boolean nameIs(ItemStack item) {
        if(item.getItemMeta() == null || name == null){
            return false;
        }
        return name.equalsIgnoreCase(item.getItemMeta().getDisplayName());
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event){
        if(event.getEntity().getKiller() != null){
            Player killer = event.getEntity().getKiller();
            if(kills.containsKey(killer)){
                kills.put(killer, kills.get(killer)+1);
            }else{
                kills.put(killer, 1);
            }
        }
    }

    private boolean isMaterial(Material material) {
        return this.material == material;
    }

    void addPotion(PotionEffect eff){
        effects.add(eff);
    }

    Collection<PotionEffect> getPotions(){
        return effects;
    }

    void setName(String newName){
        this.name = newName;
    }

    void setMessage(String message){
        this.message = message;
    }

    void setKillsNeeded(int kills){
        this.killsneeded = kills;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public int getKillsNeeded() {
        return killsneeded;
    }
}
