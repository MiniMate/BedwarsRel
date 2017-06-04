package io.github.bedwarsrel.commands;

import com.google.common.collect.ImmutableMap;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.utils.ChatWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetSpawnerCommand extends BaseCommand {

  public SetSpawnerCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    Player player = (Player) sender;
    ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(this.getRessources()));
    String material = args.get(1).toString().toLowerCase();
    Game game = this.getPlugin().getGameManager().getGame(args.get(0));

    if (game == null) {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel
          ._l(player, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
      return false;
    }

    if (game.getState() == GameState.RUNNING) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(sender, "errors.notwhilegamerunning")));
      return false;
    }

    if (!arguments.contains(material)) {
      player
          .sendMessage(
              ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
                  ._l(player, "errors.spawnerargument")));
      return false;
    }

    Object section = BedwarsRel.getInstance().getConfig().get("ressource." + material);
    ItemStack stack = ResourceSpawner.createSpawnerStackByConfig(section);

    Location location = player.getLocation();
    ResourceSpawner spawner = new ResourceSpawner(game, material, location);
    game.addResourceSpawner(spawner);
    player.sendMessage(
        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.spawnerset",
            ImmutableMap.of("name", stack.getItemMeta().getDisplayName() + ChatColor.GREEN))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "ressource"};
  }

  @Override
  public String getCommand() {
    return "setspawner";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.setspawner.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.setspawner.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

  private String[] getRessources() {
    ConfigurationSection section =
        BedwarsRel.getInstance().getConfig().getConfigurationSection("ressource");
    if (section == null) {
      return new String[]{};
    }

    List<String> ressources = new ArrayList<String>();
    for (String key : section.getKeys(false)) {
      ressources.add(key.toLowerCase());
    }

    return ressources.toArray(new String[ressources.size()]);
  }

}
