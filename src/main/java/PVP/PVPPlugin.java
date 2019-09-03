package PVP;

import io.anuke.arc.*;
import io.anuke.arc.util.*;
import io.anuke.mindustry.*;
import io.anuke.mindustry.content.*;
import io.anuke.mindustry.entities.type.*;
import io.anuke.mindustry.game.EventType.*;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.gen.*;
import io.anuke.mindustry.plugin.Plugin;

public class PVPPlugin extends Plugin{
    private boolean AdminOnly = false;
    private int PlayerLimit = -1; //on starup, dus ongelimiteerd

    //change team
    private void changeTeam(Player player){
        int index = player.getTeam().ordinal()+1;
        while (index != player.getTeam().ordinal()){
            if (index >= Team.all.length){
                index = 0;
            }
            if (!Vars.state.teams.get(Team.all[index]).cores.isEmpty()){
                player.setTeam(Team.all[index]);
                break;
            }
            index++;
        }
        //kill player
        Call.onPlayerDeath(player);
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){

        handler.<Player>register("team", "", "Switch Teams", (args, player) ->{
            if (!player.isAdmin && AdminOnly){
                player.sendMessage("[scarlet]Admin only.");
                return;
            }
            //change team
            if (!Vars.state.rules.pvp){
                player.sendMessage("[scarlet]Only available in pvp.");
                return;
            }
            changeTeam(player);

        });

        handler.<Player>register("ateam", "[playerId]", "[blue]AdminOnly[] Change the team of a player", (args, player) ->{
           if (!player.isAdmin){
               player.sendMessage("[scarlet]Admin only.");
               return;
           }
           if (args.length == 0){
               //show player list
               StringBuilder builder = new StringBuilder();
               int ID = 1;
               for (Player p : Vars.playerGroup.all()){
                    if (p.con== null ) continue;
                    builder.append("[accent]").append(ID).append(": []").append(p.name).append("\n");
               }
               player.sendMessage(builder.toString());
           } else {
               if (Strings.canParseInt(args[0])){
                   int index = Strings.parseInt(args[0])-1;
                   Player other = Vars.playerGroup.all().get(index);
                   if (other != null){
                       changeTeam(other);
                   } else {
                       player.sendMessage("[scarlet]Invalid ID");
                   }
               }
           }
        });
    }
}
