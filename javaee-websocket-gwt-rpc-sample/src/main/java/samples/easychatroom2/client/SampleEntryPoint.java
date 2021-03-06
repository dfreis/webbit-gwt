package samples.easychatroom2.client;

import com.colinalworth.gwt.websockets.client.ConnectionClosedEvent;
import com.colinalworth.gwt.websockets.client.ConnectionOpenedEvent;
import com.colinalworth.gwt.websockets.client.ConnectionOpenedEvent.ConnectionOpenedHandler;
import com.colinalworth.gwt.websockets.client.ServerBuilder;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import samples.easychatroom2.shared.ChatServer;

public class SampleEntryPoint implements EntryPoint {
	interface ChatServerBuilder extends ServerBuilder<ChatServer> {}

	@Override
	public void onModuleLoad() {
		// We can't GWT.create the server itself, instead, we need a builder
		//final ChatServer server = GWT.create(ChatServer.class);
		final ChatServerBuilder builder = GWT.create(ChatServerBuilder.class);

		// Set the url directly, or use the setHost, setPort, etc calls and
		//use the @RemoteServiceRelativePath given on the interface
//		builder.setUrl("ws://" + Window.Location.getHost() + "/chat");
//		builder.setHostname(Window.Location.getHostName());
//		builder.setPath("chat");

		// Because this is just a demo, we're using Window.prompt to get a username
		final String username = Window.prompt("Select a username", "");

		// Start up the server connection, then plug into it so you get the callbacks
		final ChatServer server = builder.start();

		final ChatClientWidget impl = new ChatClientWidget();
		server.setClient(impl);

		// This listens for the connection to start, so we can log in with the username
		// we already picked.
		// Remember that you don't need to build this here, it could be in your client
		// impl's onOpen method (and the next block in onClose).
		impl.addConnectionOpenedHandler(new ConnectionOpenedHandler() {
			@Override
			public void onConnectionOpened(ConnectionOpenedEvent event) {
				server.login(username, new Callback<Void, String>() {
					@Override
					public void onFailure(String reason) {
						Window.alert(reason);
						final String username = Window.prompt("Select a username", "");
						server.login(username, this);
					}

					@Override
					public void onSuccess(Void result) {
						RootLayoutPanel.get().add(impl);
					}
				});
			}
		});

		// Then listen for close too, so we can do something about the lost connection.
		impl.addConnectionClosedHandler(new ConnectionClosedEvent.ConnectionClosedHandler() {
			@Override
			public void onConnectionClosed(ConnectionClosedEvent event) {
				// Take the easy/stupid way out and restart the page!
				// This will stop at the prompt and wait for a username before connecting
				Window.Location.reload();
			}
		});

		impl.send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				server.say(impl.message.getValue());
				impl.message.setValue("");
			}
		});
	}

}
