package de.prison.plugin.commands;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.data.PlayerProgress;
import de.prison.plugin.data.PrisonBlockType;
import de.prison.plugin.pickaxe.PickaxeEnchantType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PrisonAdminCommand implements CommandExecutor {

    private final PrisonPlugin plugin;

    public PrisonAdminCommand(PrisonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("prison.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setshades" -> {
                if (args.length < 3) {
                    sender.sendMessage(PrisonPlugin.color("&cNutzung: /prisonadmin setshades <spieler> <betrag>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-not-found"));
                    return true;
                }
                long amount;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PrisonPlugin.color("&cUngültige Zahl."));
                    return true;
                }
                plugin.getPlayerDataManager().setShades(target.getUniqueId(), amount);
                plugin.getPlayerDataManager().save();
                sender.sendMessage(PrisonPlugin.color("&aShades von " + target.getName() + " auf " + amount + " gesetzt."));
            }
            case "addshades" -> {
                if (args.length < 3) {
                    sender.sendMessage(PrisonPlugin.color("&cNutzung: /prisonadmin addshades <spieler> <betrag>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-not-found"));
                    return true;
                }
                long amount;
                try {
                    amount = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PrisonPlugin.color("&cUngültige Zahl."));
                    return true;
                }
                plugin.getPlayerDataManager().addShades(target.getUniqueId(), amount);
                plugin.getPlayerDataManager().save();
                sender.sendMessage(PrisonPlugin.color("&a" + amount + " Shades zu " + target.getName() + " hinzugefügt."));
            }
            case "setprogress" -> {
                if (args.length < 4) {
                    sender.sendMessage(PrisonPlugin.color("&cNutzung: /prisonadmin setprogress <spieler> <blockNr 1-20> <stufe 1-10>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-not-found"));
                    return true;
                }
                int blockNr, stage;
                try {
                    blockNr = Integer.parseInt(args[2]);
                    stage = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PrisonPlugin.color("&cUngültige Zahl."));
                    return true;
                }
                if (blockNr < 1 || blockNr > plugin.getBlockTypeRegistry().size() || stage < 1 || stage > 10) {
                    sender.sendMessage(PrisonPlugin.color("&cBlockNr muss 1-" + plugin.getBlockTypeRegistry().size() + " und Stufe 1-10 sein."));
                    return true;
                }

                PlayerProgress progress = plugin.getPlayerDataManager().getProgress(target.getUniqueId());
                progress.setBlockTypeIndex(blockNr - 1);
                progress.setStageNumber(stage);
                progress.setBlocksMinedInStage(0);
                plugin.getPlayerDataManager().save();

                PrisonBlockType blockType = plugin.getBlockTypeRegistry().get(blockNr - 1);
                plugin.getWorldGenerator().generateOrLoad(blockType);
                sender.sendMessage(PrisonPlugin.color("&aFortschritt von " + target.getName() + " gesetzt: Block " + blockNr + ", Stufe " + stage));
            }
            case "givepickaxe" -> {
                if (args.length < 2) {
                    sender.sendMessage(PrisonPlugin.color("&cNutzung: /prisonadmin givepickaxe <spieler>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-not-found"));
                    return true;
                }
                ItemStack pickaxe = plugin.getPickaxeManager().createPickaxe();
                target.getInventory().addItem(pickaxe);
                sender.sendMessage(PrisonPlugin.color("&aPrison-Spitzhacke an " + target.getName() + " gegeben."));
            }
            case "setenchant" -> {
                if (args.length < 4) {
                    sender.sendMessage(PrisonPlugin.color("&cNutzung: /prisonadmin setenchant <spieler> <typ> <level>"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-not-found"));
                    return true;
                }
                PickaxeEnchantType type;
                try {
                    type = PickaxeEnchantType.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(PrisonPlugin.color("&cUnbekannter Typ. Erlaubt: EFFICIENCY, HASTE, EXPLOSION, LAYER, MONEY_BOOSTER, SHADES_BOOSTER"));
                    return true;
                }
                int level;
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(PrisonPlugin.color("&cUngültige Zahl."));
                    return true;
                }
                ItemStack hand = target.getInventory().getItemInMainHand();
                if (!plugin.getPickaxeManager().isPrisonPickaxe(hand)) {
                    sender.sendMessage(PrisonPlugin.color("&cDas Ziel muss die Prison-Spitzhacke in der Hand halten."));
                    return true;
                }
                plugin.getPickaxeManager().setLevel(hand, type, Math.min(level, type.getMaxLevel()));
                sender.sendMessage(PrisonPlugin.color("&a" + type.getDisplayName() + " von " + target.getName() + " auf Stufe " + level + " gesetzt."));
            }
            case "listblocks" -> {
                sender.sendMessage(PrisonPlugin.color("&8&m----- &r&6Alle Blockarten &8&m-----"));
                for (PrisonBlockType type : plugin.getBlockTypeRegistry().getAll()) {
                    sender.sendMessage(PrisonPlugin.color("&7#" + type.getBlockNumber() + " &e" + type.getDisplayName()
                            + " &7(" + type.getMaterial().name() + ")"));
                }
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(PrisonPlugin.color("&8&m--------------------------------"));
        sender.sendMessage(PrisonPlugin.color("&6&lPrison Admin"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin setshades <spieler> <betrag>"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin addshades <spieler> <betrag>"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin setprogress <spieler> <blockNr 1-20> <stufe 1-10>"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin givepickaxe <spieler>"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin setenchant <spieler> <typ> <level>"));
        sender.sendMessage(PrisonPlugin.color("&7/prisonadmin listblocks"));
        sender.sendMessage(PrisonPlugin.color("&8&m--------------------------------"));
    }
}
