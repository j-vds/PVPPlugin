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

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){

        handler.<Player>register("team", "", "Switch Teams", (args, player) ->{
            //change team
            if (!Vars.state.rules.pvp){
                player.sendMessage("Only available in pvp.");
                return;
            }
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
        });
    }
}
