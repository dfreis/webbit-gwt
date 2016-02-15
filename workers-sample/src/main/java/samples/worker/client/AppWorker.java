package samples.worker.client;

import com.colinalworth.gwt.worker.client.WorkerFactory;
import com.colinalworth.gwt.worker.client.worker.MessagePort;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import samples.shared.client.MyHost;
import samples.shared.client.MyWorker;

/**
 * Created by colin on 1/21/16.
 */
public class AppWorker implements EntryPoint {
	public interface Factory extends WorkerFactory<MyHost, MyWorker> {}

	@Override
	public void onModuleLoad() {

		Factory factory = GWT.create(Factory.class);//new GeneratedWorkerFactory();

		factory.wrapRemoteMessagePort(self(), new MyWorker() {
			@Override
			public void ping() {
				getRemote().pong();
			}

			private MyHost remote;

			@Override
			public void setRemote(MyHost myHost) {
				remote = myHost;
			}

			@Override
			public MyHost getRemote() {
				return remote;
			}
		});
	}

	private native MessagePort self() /*-{
		return $wnd;
	}-*/;

}
