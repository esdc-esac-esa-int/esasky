package esac.archive.esasky.cl.web.client.callback;

import com.google.gwt.user.client.Timer;

public abstract class Promise<T>  {

    private final Timer timeoutTimer = new Timer() {
        @Override
        public void run() {
            failure();
        }
    };

    protected Promise() {
        timeoutTimer.schedule(5000);
    }

    protected Promise(int timeoutMs) {
        if (timeoutMs > 0) {
            timeoutTimer.schedule(timeoutMs);
        }
    }

    public final void fulfill(T data) {
        timeoutTimer.cancel();
        success(data);
        whenComplete();
    }

    public final void error() {
        timeoutTimer.cancel();
        failure();
        whenComplete();
    }

    protected abstract void success(T data);

    protected void failure() {}

    protected void whenComplete() {}
}
