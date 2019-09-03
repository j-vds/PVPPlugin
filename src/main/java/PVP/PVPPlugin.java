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
    private int playerLimit = -1; //on starup, dus ongelimiteerd

    public PVPPlugin(){

    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("playerlimit", "[new_limit]", "shows the limit or set the player limit (this will disable NEW connections)", args -> {
            if (args.length == 0) {
                if (this.playerLimit != -1) {
                    Log.info("Current player limit: {0}", this.playerLimit);
                } else {
                    Log.info("Player limit disabled. This will only affect NEW connections");
                }
            } else {
                if (!Strings.canParseInt(args[0])) return;
                int newLimit = Strings.parseInt(args[0]);
                this.playerLimit = (newLimit < 0) ? -1 : newLimit;
                Log.info("Player limit changed.");
            }
        });
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){

        handler.<Player>register("team", "", "Switch Teams", (args, player) ->{
            if (!Vars.state.rules.pvp){
                player.sendMessage("[scarlet]Only available in pvp.");
                return;
            } else if (!player.isAdmin && AdminOnly){
                player.sendMessage("[accent]/team[][scarlet]disabled");
                return;
            }
            //change team
            changeTeam(player);

        });

        handler.<Player>register("ateam", "[playerId]", "[scarlet]AdminOnly[] Change the team of a player", (args, player) ->{
           if (!player.isAdmin){
               player.sendMessage("[scarlet]Admin only.");
               return;
           } else if (!Vars.state.rules.pvp){
               player.sendMessage("[scarlet]Only available in pvp.");
               return;
           }
            //TODO cleanup
           if (args.length == 0){
               //show player list
               StringBuilder builder = new StringBuilder();
               builder.append("[accent]ID:[]  name");
               int ID = 1;
               for (Player p : Vars.playerGroup.all()){
                    if (p.con== null ) continue;
                    builder.append("\n").append("[accent]").append(ID).append(": []").append(p.name);
               }
               player.sendMessage(builder.append("\n\nTo disable/enable [accent]/team[] use /ateam -1").toString());
           } else {
               try {
                   int ID = Integer.parseInt(args[0]);
                   if (ID == -1){
                       this.AdminOnly = (this.AdminOnly) ? false : true;  //change state adminonly
                       String msg = (!this.AdminOnly) ? "[geen]enabled[]" : "[scarlet]disabled[]";
                       player.sendMessage("[accent]/team[] " + msg);
                       return;
                   }
                   Player other = Vars.playerGroup.all().get(ID-1);
                   changeTeam(other);
                   player.sendMessage("[accent]changed the team of " + other.name);
               } catch (Exception e){
                   player.sendMessage("[scarlet]invalid ID");
               }
           }
        });

        handler.<Player>register("playerlimit", "[new_Limit]", "[scarlet]AdminOnly[] shows the limit or set a player limit (this will disable [accent]NEW[] connections)",
                (args, player) ->{
           if (!player.isAdmin){
               player.sendMessage("[scarlet]Admin only!");
               return;
           }
            if (args.length == 0) {
                if (this.playerLimit != -1) {
                    player.sendMessage("[accent]Current player limit: []" + this.playerLimit);
                } else {
                    player.sendMessage("[accent]Player limit disabled.[]");
                }
            } else {
                if (!Strings.canParseInt(args[0])) return;
                int newLimit = Strings.parseInt(args[0]);
                this.playerLimit = (newLimit < 0) ? -1 : newLimit;
                player.sendMessage("Player limit changed. This will only affect [accent]NEW[] connections");
            }
        });
    }

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
}
