package core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.gituhub.zxskelobrine.plugins.manager.api.central.plugins.utilities.PluginManager;

import core.events.BotCnJoin;
import core.events.BotGnMessage;
import core.events.BotPmMessage;
import core.processors.CommandProcessor;

public class BotListener extends ListenerAdapter<PircBotX> {

    /**
     * This is the bot supplied by the Launcher.
     */
    private PircBotX bot;
    /**
     * This is the plugin manager supplied by the launcher.
     */
    private PluginManager manager;
    /**
     * This is the list of users currently on the server.
     */
    private List<User> users;
    /**
     * This is the channel that the bot is currently operating on.
     */
    private Channel channel;
    /**
     * This is the character to start all commands with.
     */
    public static String commandStartCharacter = "~";

    /**
     * This will set the bot that this class uses to interact with the irc
     * server.
     * 
     * @param bot
     *            - The bot that is connected.
     */
    public void setBot(PircBotX bot) {
	this.bot = bot;
    }

    /**
     * This will set the plugin manager used to broadcast to the client.
     * 
     * @param manager
     *            - The manager that was used to initialise the plugins.
     */
    public void setManager(PluginManager manager) {
	this.manager = manager;
    }

    @Override
    public void onPrivateMessage(final PrivateMessageEvent<PircBotX> event)
	    throws Exception {
	// If the command should be sent to the plugins.
	if (shouldPassToPlugins(event.getMessage())) {
	    // Then broadcast a new PM event.
	    manager.broadcastEvent(new BotPmMessage() {

		@Override
		public PrivateMessageEvent<PircBotX> getEvent() {
		    return event;
		}

		@Override
		public boolean isCommand() {
		    return Character.toString(event.getMessage().charAt(0))
			    .equals(commandStartCharacter);
		}

		@Override
		public boolean isOp() {
		    return Launcher.manager.hasOpAccount(event.getUser()
			    .getUserId().toString());
		}

	    });
	} else {
	    CommandProcessor.processCommand(event.getMessage(), event.getUser()
		    .getNick(), bot);
	}
	super.onPrivateMessage(event);

    }

    @Override
    public void onJoin(final JoinEvent<PircBotX> event) throws Exception {
	// If the user joining is not the bot. Welcome them.
	if (!event.getUser().getNick().equals(Launcher.BOT_NAME)) {
	    bot.sendIRC().message(Launcher.CHANNEL,
		    "Hello " + event.getUser().getNick());
	} else {// Otherwise of it is the bot joining
	    bot.sendIRC().message(Launcher.CHANNEL,
		    "VitCore enabled with plugins: " + Launcher.BOT_MASK);
	}
	// If the user joining is the author of this bot - op them if they are
	// not already.
	if (event.getUser().getNick().equals("Vitineth")) {
	    if (!Launcher.manager.hasOpAccount(event.getUser().getUserId()
		    .toString())) {
		Launcher.manager.addOperator(event.getUser().getUserId()
			.toString());
	    }
	}
	// Then broadcast the join event.
	manager.broadcastEvent(new BotCnJoin() {

	    @Override
	    public boolean isOp() {
		return Launcher.manager.hasOpAccount(event.getUser()
			.getUserId().toString());
	    }

	    @Override
	    public User getUser() {
		return event.getUser();
	    }

	    @Override
	    public JoinEvent<PircBotX> getEvent() {
		return event;
	    }

	    @Override
	    public Channel getChannel() {
		return event.getChannel();
	    }
	});
	super.onJoin(event);
    }

    @Override
    public void onMessage(final MessageEvent<PircBotX> event) throws Exception {
	// Set the channel to the one the message was recieved on.
	channel = event.getChannel();
	// Then update the users list.
	updateUsers();
	// Then if it should be sent to plugins.
	if (shouldPassToPlugins(event.getMessage())) {
	    // Broadcast the chat event.
	    manager.broadcastEvent(new BotGnMessage() {

		@Override
		public GenericMessageEvent<PircBotX> getEvent() {
		    return event;
		}

		@Override
		public List<User> getUsers() {
		    return users;
		}

		@Override
		public Channel getChannel() {
		    return channel;
		}

		@Override
		public boolean isCommand() {
		    return Character.toString(event.getMessage().charAt(0))
			    .equals(commandStartCharacter);
		}

		@Override
		public boolean isOp() {
		    return Launcher.manager.hasOpAccount(event.getUser()
			    .getUserId().toString());
		}
	    });
	} else {
	    // Otherwise deal with it ourlselfs.
	    CommandProcessor.processCommand(event.getMessage(), event.getUser()
		    .getNick(), bot);
	}
	super.onMessage(event);
    }

    /**
     * This checks if the message starts with [sc]core.
     * 
     * @param message
     *            - The message.
     * @return Boolean - if it should transmit the message to the client.
     */
    public boolean shouldPassToPlugins(String message) {
	return !message.toLowerCase()
		.startsWith(commandStartCharacter + "core");
    }

    /**
     * This will get a users UUID from their nickname
     * 
     * @param nick
     *            - The nickname of the user to search for.
     * @return String - their uuid.
     */
    public static String getUserUUID(String nick) {
	// Create a copy of the users list.
	List<User> list = new CopyOnWriteArrayList<>(Launcher.botListener.users);
	// Then for every user in that list.
	for (User user : list) {
	    // If their nick matched the one being supplied
	    if (user.getNick().equalsIgnoreCase(nick)) {
		// Return their uuid.
		return user.getUserId().toString();
	    }
	}
	return null;
    }

    /**
     * This will update the user list from the current set channel.
     */
    public void updateUsers() {
	users = channel.getUsers().asList();
    }

    /**
     * This will return a copy of the users list.
     * 
     * @return
     */
    public List<User> getUserList() {
	return new CopyOnWriteArrayList<>(users);
    }
}
