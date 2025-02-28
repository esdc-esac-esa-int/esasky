/*
ESASky
Copyright (C) 2025 European Space Agency

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package esac.archive.esasky.cl.web.client.callback;

import com.google.gwt.user.client.Timer;

public abstract class Promise<T>  {

    private final Timer timeoutTimer = new Timer() {
        @Override
        public void run() {
            failure();
            completed = true;
        }
    };
    private boolean completed = false;

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
        completed = true;
    }

    public final void error() {
        timeoutTimer.cancel();
        failure();
        whenComplete();
        completed = true;
    }

    protected abstract void success(T data);

    protected void failure() {}

    protected void whenComplete() {}

    public boolean isCompleted() {
        return completed;
    }
}
