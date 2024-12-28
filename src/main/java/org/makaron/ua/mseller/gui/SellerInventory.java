package org.makaron.ua.mseller.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.makaron.ua.mseller.gui.utils.HexUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SellerInventory {

    private final Inventory inventory;
    private final JavaPlugin plugin;
    private long updateTime1;
    private long updateTime2;
    private boolean usingDefaultItems1 = true;
    private boolean usingDefaultItems2 = true;
    private final Map<Integer, ItemStack> defaultItems = new HashMap<>();
    private final Map<Integer, ItemStack> updateItems1 = new HashMap<>();
    private final Map<Integer, ItemStack> updateItems2 = new HashMap<>();

    public SellerInventory(JavaPlugin plugin) {
        this.plugin = plugin;
        String title = HexUtil.color(plugin.getConfig().getString("title"));
        this.inventory = Bukkit.createInventory(null, 45, title);
        loadItemsFromConfig();
        setupUpdateTimers();
    }

    private void loadItemsFromConfig() {
        for (String key : plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
            int slot = Integer.parseInt(key);
            ItemStack item = getItemFromConfig("items." + key);
            defaultItems.put(slot, item);
            inventory.setItem(slot, item);
        }


        for (String key : plugin.getConfig().getConfigurationSection("item-update").getKeys(false)) {
            int slot = Integer.parseInt(key);
            ItemStack item = getItemFromConfig("item-update." + key);
            updateItems1.put(slot, item);
        }

        for (String key : plugin.getConfig().getConfigurationSection("item-update-1").getKeys(false)) {
            int slot = Integer.parseInt(key);
            ItemStack item = getItemFromConfig("item-update-1." + key);
            updateItems2.put(slot, item);
        }

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(HexUtil.color(plugin.getConfig().getString("close.name")));
            closeMeta.setLore(plugin.getConfig().getStringList("close.lore").stream().map(HexUtil::color).collect(Collectors.toList()));
            closeButton.setItemMeta(closeMeta);
        }
        inventory.setItem(40, closeButton);
    }

    private ItemStack getItemFromConfig(String path) {
        String materialName = plugin.getConfig().getString(path + ".material");
        Material material = Material.valueOf(materialName);
        String name = HexUtil.color(plugin.getConfig().getString(path + ".name"));
        List<String> lore = plugin.getConfig().getStringList(path + ".lore")
                .stream()
                .map(line -> HexUtil.color(line))
                .collect(Collectors.toList());

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null && !name.isEmpty()) {
                meta.setDisplayName(name);
            }
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void setupUpdateTimers() {
        updateTime1 = System.currentTimeMillis() + plugin.getConfig().getInt("time-to-updates") * 60000L;
        updateTime2 = System.currentTimeMillis() + plugin.getConfig().getInt("time-to-updates-1") * 60000L;

        new BukkitRunnable() {
            @Override
            public void run() {
                long timeLeft1 = updateTime1 - System.currentTimeMillis();
                if (timeLeft1 <= 0) {
                    updateItems(updateItems1, usingDefaultItems1);
                    usingDefaultItems1 = !usingDefaultItems1;
                    updateTime1 = System.currentTimeMillis() + plugin.getConfig().getInt("time-to-updates") * 60000L;
                    timeLeft1 = updateTime1 - System.currentTimeMillis();
                }

                long timeLeft2 = updateTime2 - System.currentTimeMillis();
                if (timeLeft2 <= 0) {
                    updateItems(updateItems2, usingDefaultItems2);
                    usingDefaultItems2 = !usingDefaultItems2;
                    updateTime2 = System.currentTimeMillis() + plugin.getConfig().getInt("time-to-updates-1") * 60000L;
                    timeLeft2 = updateTime2 - System.currentTimeMillis();
                }

                setTimerItem(timeLeft1, timeLeft2);
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void setTimerItem(long timeLeft1, long timeLeft2) {
        ItemStack timerItem = new ItemStack(Material.CLOCK);
        ItemMeta meta = timerItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HexUtil.color(plugin.getConfig().getString("updates.name")));
            List<String> lore = plugin.getConfig().getStringList("updates.lore").stream()
                    .map(line -> line.replace("{time-1}", formatTime(timeLeft1)).replace("{time-2}", formatTime(timeLeft2)))
                    .map(HexUtil::color)
                    .collect(Collectors.toList());
            meta.setLore(lore);
            timerItem.setItemMeta(meta);
        }
        inventory.setItem(plugin.getConfig().getInt("updates.slot"), timerItem);
    }

    private String formatTime(long time) {
        long minutes = (time / 1000) / 60;
        long seconds = (time / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateItems(Map<Integer, ItemStack> updateItems, boolean usingDefaultItems) {
        if (usingDefaultItems) {
            for (Map.Entry<Integer, ItemStack> entry : updateItems.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        } else {
            for (Map.Entry<Integer, ItemStack> entry : defaultItems.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setupCloseButtonAnimation() {
        int[] slots = {41, 42, 43, 44, 39, 38, 37, 36};
        for (int slot : slots) {
            inventory.setItem(slot, null);
        }

        new BukkitRunnable() {
            final int[][] slotPairs = {{41, 39}, {42, 38}, {43, 37}, {44, 36}};
            int index = 0;

            @Override
            public void run() {
                if (index < slotPairs.length) {
                    int[] slotPair = slotPairs[index];
                    ItemStack blackPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    ItemMeta meta = blackPane.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.BLACK + " ");
                        blackPane.setItemMeta(meta);
                    }
                    inventory.setItem(slotPair[0], blackPane);
                    inventory.setItem(slotPair[1], blackPane);
                    index++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
}