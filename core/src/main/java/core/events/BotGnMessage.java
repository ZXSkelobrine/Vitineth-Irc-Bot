package core.events;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.gituhub.zxskelobrine.plugins.manager.api.events.PluginEvent;

public interface BotGnMessage extends PluginEvent {

    public GenericMessageEvent<PircBotX> getEvent();

    public List<User> getUsers();

    public Channel getChannel();
    
    public boolean isCommand();
    
    public boolean isOp();

}
