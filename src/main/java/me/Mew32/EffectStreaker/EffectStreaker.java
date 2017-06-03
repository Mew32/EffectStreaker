package me.Mew32.EffectStreaker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;

public final class EffectStreaker extends JavaPlugin {

    private PlayerItemEventListener eventListener;
    public YamlConfiguration yc;
    File cf = new File(getDataFolder(), "config.yml");

    public EffectStreaker(){
        eventListener = new PlayerItemEventListener(Material.FLOWER_POT_ITEM, "streaker","Message: Kills needed is %kills%.");
    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public void onDisable() {
        //saveMaterials();
        savePotionEffects();
        saveMessageToConfig();
        saveNameToConfig();
        saveKillsNeededToConfig();
        eventListener.getPotions().clear();
        eventListener = null;
        saveData();
    }

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        loadData();
        eventListener.setMessage(getMessageFromConfig());
        eventListener.setName(getNameFromConfig());
        eventListener.setKillsNeeded(getKillsNeededFromConfig());
    }

    @Override
    public void onEnable() {
        loadPotionEffects();
        Bukkit.getPluginManager().registerEvents(eventListener, this);
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equalsIgnoreCase("es")){
            if(strings.length >= 1){
                switch (strings[0].toLowerCase()) {
                    case "reload":
                        eventListener.getPotions().clear();
                        loadPotionEffects();
                        break;
                    case "list":
                        for (PotionEffect p : eventListener.getPotions()) {
                            commandSender.sendMessage(p.toString());
                        }
                        break;
                    case "clear":
                        eventListener.getPotions().clear();
                        break;
                    case "potion":
                        try {
                            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
                            eventListener.addPotion(effect);
                        } catch (Exception e) {
                            commandSender.sendMessage("Wrong Arguments!");
                        }
                        break;
                    case "name":
                        try{
                            eventListener.setName(strings[1]);
                        }catch (Exception e){
                            commandSender.sendMessage("Wrong number of Arguments.");
                        }
                        break;
                }
            }
        }

        return false;
    }

    public void loadPotionEffects() {
        if(yc == null){
            return;
        }
        ConfigurationSection section = yc.getConfigurationSection("potions");
        for (String k : section.getKeys(false)) {
            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(yc.getString("potions."+k+".type")), yc.getInt("potions."+k+".duration"), yc.getInt("potions."+k+".amplifier"));
            eventListener.addPotion(effect);
        }
    }

    public void savePotionEffects() {
        int i = 0;
        if(yc == null){
            return;
        }
        Configuration config = yc;

        for (PotionEffect e : eventListener.getPotions()) {
            i += 1;
            System.out.println("potions."+i+".type");
            yc.set("potions."+i+".type", e.getType().getName());
            yc.set("potions."+i+".duration", e.getDuration());
            yc.set("potions."+i+".amplifier", e.getAmplifier());
        }
    }

    public String getMessageFromConfig(){
        if(yc == null) {
            return "Kills needed: %kills%";
        }
        return yc.getString("message");
    }

    public String getNameFromConfig(){
        if(yc == null) {
            return "streaker";
        }
        return yc.getString("itemname");
    }

    public int getKillsNeededFromConfig(){
        if(yc == null){
            return 3;
        }
        return yc.getInt("killsneeded");
    }

    public void saveKillsNeededToConfig(){
        if(yc == null){
            return;
        }
        yc.set("killsneeded", eventListener.getKillsNeeded());
    }

    public void saveMessageToConfig(){
        if(yc == null)
            return;
        yc.set("message", eventListener.getMessage());
    }

    public void saveNameToConfig(){
        if(yc == null)
            return;
        yc.set("itemname", eventListener.getName());
    }


    public void loadData()
    {
        if (cf.exists()) {
            yc = YamlConfiguration.loadConfiguration(cf);
        }else {
            yc = YamlConfiguration.loadConfiguration(getTextResource("config.yml"));
        }
    }

    public void saveData(){
        try {
            yc.save(cf);
        } catch (IOException e) {

        }
    }
}