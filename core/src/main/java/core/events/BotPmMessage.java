package core.events;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.gituhub.zxskelobrine.plugins.manager.api.events.PluginEvent;

public interface BotPmMessage extends PluginEvent {

    public PrivateMessageEvent<PircBotX> getEvent();
    
    public boolean isCommand();
    
    public boolean isOp();

}
