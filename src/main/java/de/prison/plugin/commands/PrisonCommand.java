package de.prison.plugin.commands;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.gui.PrisonMainGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrisonCommand implements CommandExecutor {

    private final PrisonPlugin plugin;
    private final PrisonMainGUI mainGUI;

    public PrisonCommand(PrisonPlugin plugin, PrisonMainGUI mainGUI) {
        this.plugin = plugin;
        this.mainGUI = mainGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(PrisonPlugin.color("&cDieser Befehl ist nur für Spieler."));
            return true;
        }
        if (!player.hasPermission("prison.use")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        mainGUI.open(player);
        return true;
    }
}
