package me.lxc.thesieutoc.internal;

import me.lxc.artxeapi.utils.ArtxeNumber;
import me.lxc.thesieutoc.TheSieuToc;
import me.lxc.thesieutoc.handlers.InputCardHandler;
import me.lxc.thesieutoc.tasks.CardCheckTask;
import me.lxc.thesieutoc.utils.CalculateTop;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Commands extends BukkitCommand {

    public Commands(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        final Messages msg = TheSieuToc.getInstance().getMessages();
        final Ui ui = TheSieuToc.getInstance().getUi();
        final boolean isPlayer = sender instanceof Player;
        final Player player = isPlayer ? (Player) sender : null;
        final boolean hasAPIInfo = TheSieuToc.getInstance().hasAPIInfo;

        switch (args.length){
            case 0:
                if(isPlayer){
                    if(hasAPIInfo) {
                        chooseCard(player, ui);
                        return true;
                    } else return false;
                } else {
                    sender.sendMessage(msg.onlyPlayer);
                    return false;
                }
            case 1:
                switch (args[0].toLowerCase()){
                    case "reload":
                        return reload(sender, args.length, msg);
                    case "choose":
                        if(hasAPIInfo) {
                            if (isPlayer) {
                                chooseCard(player, ui);
                                return true;
                            } else {
                                sender.sendMessage(msg.onlyPlayer);
                                return false;
                            }
                        } else return false;
                    case "check":
                        if(hasAPIInfo) {
                            return check(sender, args.length, msg);
                        } else return false;
                    case "top":
                        try {
                            CalculateTop.printTop(sender, CalculateTop.execute("TOTAL"), 10);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        sender.sendMessage(msg.invalidCommand);
                        return false;
                }
            case 2:
                switch (args[0].toLowerCase()){
                    case "choose":
                        if(isPlayer) {
                            if(hasAPIInfo) {
                                if (isValidCard(args[1])) {
                                    String type = args[1];
                                    chooseAmount(player, type, ui);
                                    return true;
                                } else {
                                    sender.sendMessage(msg.invalidCardType);
                                    return false;
                                }
                            } else return false;
                        } else {
                            sender.sendMessage(msg.onlyPlayer);
                            return false;
                        }
                    case "reload":
                        return reload(sender, args.length, msg);
                    case "check":
                        if(hasAPIInfo) {
                            return check(sender, args.length, msg);
                        } else return false;
                    case "top":
                        try {
                            if (ArtxeNumber.isInteger(args[1])) {
                                CalculateTop.printTop(sender, CalculateTop.execute("TOTAL"), Integer.parseInt(args[1]));
                                return true;
                            } else {
                                sender.sendMessage(msg.notNumber.replaceAll("(?ium)[{]0[}]", args[1]));
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        sender.sendMessage(msg.invalidCommand);
                        return false;
                }
            case 3:
                switch (args[0].toLowerCase()){
                    case "choose":
                        if(isPlayer) {
                            if(hasAPIInfo) {
                                if (isValidCard(args[1])) {
                                    if (ArtxeNumber.isInteger(args[2])) {
                                        if (isValidAmount(Integer.parseInt(args[2]))) {
                                            InputCardHandler.triggerStepOne(player, args[1], Integer.parseInt(args[2]));
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        sender.sendMessage(msg.notNumber.replaceAll("(?ium)[{]0[}]", args[2]));
                                        return false;
                                    }
                                } else {
                                    sender.sendMessage(msg.invalidCardType);
                                    return false;
                                }
                            } else return false;
                        } else {
                            sender.sendMessage(msg.onlyPlayer);
                            return false;
                        }
                    case "reload":
                        return reload(sender, args.length, msg);
                    case "check":
                        if(hasAPIInfo) {
                            return check(sender, args.length, msg);
                        } else return false;
                    case "top":
                        try {
                            if (ArtxeNumber.isInteger(args[1])) {
                                if (Stream.of("TOTAL", "DAY", "MONTH", "YEAR").anyMatch(args[2]::equalsIgnoreCase)) {
                                    CalculateTop.printTop(sender, CalculateTop.execute(args[2]), Integer.parseInt(args[1]));
                                    return true;
                                } else return false;
                            } else {
                                sender.sendMessage(msg.notNumber.replaceAll("(?ium)[{]0[}]", args[1]));
                                return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        sender.sendMessage(msg.invalidCommand);
                        return false;
                }
            default:
                sender.sendMessage(msg.tooManyArgs);
                return false;
        }
    }

    private boolean reload(CommandSender sender, int arg, Messages msg){
        if(sender.hasPermission("napthe.admin.reload")){
            if(arg == 1){
                TheSieuToc.getInstance().reload((short) 0);
                sender.sendMessage(msg.reloaded);
                return true;
            } else {
                // TODO: add more reload type
                sender.sendMessage(msg.tooManyArgs);
                return false;
            }
        } else {
            sender.sendMessage(msg.noPermission);
            return false;
        }
    }

    private boolean check(CommandSender sender, int arg, Messages msg){
        if(sender.hasPermission("napthe.admin.check")){
            if(arg == 1){
                CardCheckTask.checkAll();
                sender.sendMessage(msg.checked);
                return true;
            } else {
                sender.sendMessage(msg.tooManyArgs);
                return false;
            }
        } else {
            sender.sendMessage(msg.noPermission);
            return false;
        }
    }

    private void chooseCard(Player player, Ui ui){
        for(String card : TheSieuToc.getInstance().getSettings().cardEnable){
            String text = ui.cardTypeText.replaceAll("(?ium)[{]Card_Type[}]", card);
            String hover = splitListToLine(ui.cardTypeHover).replaceAll("(?ium)[{]Card_Type[}]", card);
            BaseComponent[] message = new ComponentBuilder(text)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, MessageFormat.format("/napthe choose {0}", card)))
                    .create();
            player.spigot().sendMessage(message);
        }
    }

    private void chooseAmount(Player player, String type, Ui ui){
        for(Integer amount : TheSieuToc.getInstance().getSettings().amountList){
            String text = ui.cardAmountText.replaceAll("(?ium)[{]Card_Amount[}]", amount.toString());
            String hover = splitListToLine(ui.cardAmountHover).replaceAll("(?ium)[{]Card_Amount[}]", amount.toString());
            BaseComponent[] message = new ComponentBuilder(text)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, MessageFormat.format("/napthe choose {0} {1}", type, amount.toString())))
                    .create();
            player.spigot().sendMessage(message);
        }
    }

    private String splitListToLine(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<list.size() ; i++) {
            String line = list.get(i);
            sb.append(ChatColor.translateAlternateColorCodes('&', line));
            if(i<list.size()-1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }

    static boolean isValidCard(String type){
        return TheSieuToc.getInstance().getSettings().cardEnable.stream().anyMatch(type::equalsIgnoreCase);
    }

    static boolean isValidAmount(int a){
        for (int amount : TheSieuToc.getInstance().getSettings().amountList) {
            if (amount == a) {
                return true;
            }
        }
        return false;
    }

}