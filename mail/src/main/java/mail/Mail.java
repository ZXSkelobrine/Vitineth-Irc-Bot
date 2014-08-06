package mail;

import mail.system.MailItem;
import mail.system.MailManager;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.utilities.PluginManager;
import com.gituhub.zxskelobrine.plugins.manager.api.events.PluginEvent;
import com.gituhub.zxskelobrine.plugins.manager.api.listeners.PluginListener;

import core.events.BotCnJoin;
import core.events.BotGnMessage;
import core.events.BotPmMessage;

public class Mail implements PluginListener {

    private PircBotX bot;
    public static String channel;
    public static String hostname;
    private PluginManager manager;

    public boolean pluginInit(PircBotX bot, String hostname, String channel,
	    PluginManager manager) {
	this.bot = bot;
	this.channel = channel;
	this.hostname = hostname;
	this.manager = manager;
	bot.sendIRC().message(channel, "Mail system initialized!");
	manager.addPluginListener(this);
	return true;
    }

    public void onGenericMessage(GenericMessageEvent<PircBotX> event) {
	event.respond("Event recieved");
    }

    public void onEvent(PluginEvent e) {
	if (e instanceof BotGnMessage) {
	    BotGnMessage message = (BotGnMessage) e;
	    checkMail(message.getEvent().getUser());
	    if (message.isCommand()) {
		mailCommand(message.getEvent());
	    }
	}
	if (e instanceof BotPmMessage) {
	    BotPmMessage message = (BotPmMessage) e;
	    checkMail(message.getEvent().getUser());
	    if (message.isCommand()) {
		mailCommand(message.getEvent());
	    }
	}
	if (e instanceof BotCnJoin) {
	    BotCnJoin join = (BotCnJoin) e;
	    checkMail(join.getUser());
	}
    }

    private void mailCommand(GenericMessageEvent<PircBotX> event) {
	String[] spaceSplit = event.getMessage().split("\\s+");
	if (spaceSplit[0].substring(1).equalsIgnoreCase("mail")) {
	    if (spaceSplit.length >= 2) {
		String to = spaceSplit[1];
		String from = event.getUser().getNick();
		String message;
		StringBuilder builder = new StringBuilder();
		for (int i = 2; i < spaceSplit.length; i++) {
		    builder.append(spaceSplit[i] + " ");
		}
		message = builder.toString();
		MailItem item = new MailItem(from, message, to);
		MailManager.addMail(item);
		event.respond("Your mail has been sent.");
	    } else {
		event.respond("Invalid arguments. Use the help command for help.");
	    }
	} else {
	    event.respond("Unknown command.");
	}
    }

    public void checkMail(User user) {
	if (MailManager.hasMail(user.getNick())) {
	    MailManager.sendMail(user);
	}
    }
}
