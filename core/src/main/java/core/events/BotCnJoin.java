package core.events;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;

import com.gituhub.zxskelobrine.plugins.manager.api.events.PluginEvent;

public interface BotCnJoin extends PluginEvent{
    
    public JoinEvent<PircBotX> getEvent();

    public Channel getChannel();

    public User getUser();
    
    public boolean isOp();
}
