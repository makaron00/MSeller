package org.makaron.ua.mseller.gui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.makaron.ua.mseller.MSeller;
import org.makaron.ua.mseller.gui.utils.HexUtil;

public class Seller implements Listener {

    private final SellerInventory sellerInventory;
    private final JavaPlugin plugin;

    public Seller(JavaPlugin plugin) {
        this.plugin = plugin;
        this.sellerInventory = new SellerInventory(plugin);
    }

    public SellerInventory getSellerInventory() {
        return sellerInventory;
    }

    public void openInventory(Player player) {
        player.openInventory(sellerInventory.getInventory());
        sellerInventory.setupCloseButtonAnimation();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(sellerInventory.getInventory())) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                int slot = event.getSlot();
                ItemStack clickedItem = event.getCurrentItem();

                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    if ((slot >= 36 && slot <= 44) && clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                        return;
                    }

                    if (slot == 40 && clickedItem.getType() == Material.BARRIER) {
                        player.closeInventory();
                        return;
                    }

                    String key = String.valueOf(slot);
                    if (plugin.getConfig().getConfigurationSection("items").contains(key)) {
                        int price = plugin.getConfig().getInt("items." + key + ".price");
                        Material material = clickedItem.getType();

                        if (player.getInventory().contains(material)) {
                            player.getInventory().removeItem(new ItemStack(material, 1));
                            Economy econ = MSeller.getEconomy();
                            econ.depositPlayer(player, price);
                            player.sendMessage(HexUtil.color(plugin.getConfig().getString("sell-message").replace("{price}", String.valueOf(price))));
                        } else {
                            player.sendMessage(HexUtil.color(plugin.getConfig().getString("no-sell-message")));
                        }
                    }
                }
            }
        }
    }
}