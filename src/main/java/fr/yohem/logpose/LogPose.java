package fr.yohem.logpose;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class LogPose extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("[LogPose] Enable");
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onClick(PlayerInteractEvent playerInteractEvent){

                if (playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_AIR) || playerInteractEvent.getAction().equals(Action.RIGHT_CLICK_AIR)){
                    Player p = playerInteractEvent.getPlayer();
                    if (playerInteractEvent.getItem().getItemMeta().getDisplayName().equals(getVirginCompass().getItemMeta().getDisplayName())){
                        p.sendMessage(""+getVirginCompass().getItemMeta().getDisplayName()+" "+ getVirginCompass().getItemMeta().getLocalizedName());
                    }else if (playerInteractEvent.getItem().equals(getBousole())){
                        p.sendMessage("Longitude : "+p.getLocation().getX()+"\nLatitude : "+p.getLocation().getZ());
                    }
                }
            }
            @EventHandler
            public void onChange(PlayerChangedMainHandEvent event){
                if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(getVirginCompass().getItemMeta().getDisplayName())){
                    ItemStack compass = event.getPlayer().getInventory().getItemInMainHand();
                    focusCompass(event.getPlayer(), compass);
                }
            }
        },this);
        getCommand("bousole").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player){
                ((Player)sender).getInventory().addItem(getBousole());
                return true;
            }
            return false;
        });
        getCommand("logPose").setExecutor((sender, command, label, args) -> {
            if (sender.hasPermission("useCompass")) {
                sender.sendMessage("setCompass : " + getCommand("setCompass").getUsage());
                sender.sendMessage("focusCompass : " + getCommand("focusCompass").getUsage());
            }
            if (sender.hasPermission("giveCompass")) {
                sender.sendMessage("compass : " + getCommand("compass").getUsage());
            }
            if (sender.hasPermission("giveBousole")) {
                sender.sendMessage("bousole : " + getCommand("bousole").getUsage());
            }
            return false;
        });
        getCommand("compass").setExecutor((sender, command, label, args) -> {
            ((Player)sender).getInventory().addItem(getVirginCompass());
            return true;
        });
        getCommand("focusCompass").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player){
                Player player = (Player)sender;
                return focusCompass(player, player.getInventory().getItemInMainHand());
            }
            return true;
        });
        getCommand("setCompass").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getInventory().getItemInMainHand().getData().equals(Material.COMPASS) && (player.getInventory().getItemInMainHand().equals(getVirginCompass())|| (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equals("Compas de navigation configurer")))) {
                    if (args.length >= 2) {
                        try {
                            Location l = player.getLocation().clone();
                            l.setX(Double.parseDouble(args[0]));
                            l.setZ(Double.parseDouble(args[1]));
                            player.getInventory().setItemInMainHand(getEnableCompass(l));
                            player.setCompassTarget(l);
                            return true;
                        } catch (NumberFormatException e) {
                            System.out.println(e);
                        }
                    } else {
                        Location l = player.getLocation().clone();
                        player.getInventory().setItemInMainHand(getEnableCompass(l));
                        player.setCompassTarget(l);
                    }
                }else{
                    player.sendMessage("Ce n'est pas un compass de navigation");
                }
            }
            return false;
        });

    }

    @Override
    public void onDisable() {
        System.out.println("[LogPose] Disable");
    }
    public static ItemStack getBousole(){
        ItemStack is = new ItemStack(Material.IRON_NUGGET);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§4bousole de navigation");
        im.setLocalizedName("bousole de navigation");
        im.setLore(Arrays.asList("Malheuresement le compas est vierge", "'/setCompas' pour le configurer"));
        is.setItemMeta(im);
        return is;
    }
    public static ItemStack getVirginCompass(){
        ItemStack is = new ItemStack(Material.COMPASS);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("§4Compas de navigation");
        im.setLocalizedName("Compas de navigation configurer");
        im.setLore(Arrays.asList("Malheuresement le compas est vierge", "'/setCompas' pour le configurer"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getEnableCompass(Location l){
        ItemStack is = getVirginCompass();
        ItemMeta im = is.getItemMeta();
        im.setLore(Arrays.asList("Compas de navigation configurer", "Longitudes : "+l.getX()+ " Latitudes : "+l.getZ()));
        is.setItemMeta(im);
        return is;
    }

    public boolean focusCompass(Player player, ItemStack compass){
        if (compass.getItemMeta().getLocalizedName().equals("Compas de navigation configurer")){
            System.out.println("a");
            String[] strs = compass.getItemMeta().getLore().get(1).replace("Longitudes : ", "").split(" Latitudes : ");
            try {
                Location l = player.getLocation().clone();
                l.setZ(Double.parseDouble(strs[1]));
                l.setX(Double.parseDouble(strs[0]));
                player.setCompassTarget(l);
                System.out.println("z");
                return true;

            }catch (NumberFormatException e){
                System.out.println(e);
            }
        }else System.out.println("b");
        return false;
    }
}
