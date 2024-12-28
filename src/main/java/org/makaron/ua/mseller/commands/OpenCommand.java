package org.makaron.ua.mseller.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.makaron.ua.mseller.gui.Seller;

public class OpenCommand implements CommandExecutor {

    private final Seller seller;

    public OpenCommand(Seller seller) {
        this.seller = seller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            seller.openInventory(player);
            return true;
        } else {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }
    }
}