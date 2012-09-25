/*
 * Copyright 2012 Jean-Francois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.handler;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;

import java.io.IOException;

/**
 * Simple {@link AtmosphereHandler} that must be used with the {@link org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor}
 * and {@link org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor} to reduce the handling of the suspend/resume/disconnect and
 * broadcast operation.
 *
 * @author Jeanfrancois Arcand
 */
public abstract class OnMessage<T> extends AbstractReflectorAtmosphereHandler {
    @Override
    public final void onRequest(AtmosphereResource resource) throws IOException {
    }

    @Override
    public final void onStateChange(AtmosphereResourceEvent event) throws IOException {
        AtmosphereResponse response = event.getResource().getResponse();
        if (event.isSuspended()) {
            onMessage(response, (T) event.getMessage());
        } else if (event.isResuming()) {
            onResume(response);
        } else if (event.isResumedOnTimeout()) {
            onTimeout(response);
        } else if (event.isCancelled()) {
            onDisconnect(response);
        }
        postStateChange(event);
    }

    @Override
    public final void destroy() {
    }

    /**
     * Implement this method to get invoked every time a new {@link org.atmosphere.cpr.Broadcaster#broadcast(Object)}
     * occurs.
     *
     * @param response an {@link AtmosphereResponse}
     * @param message a message of type T
     */
    abstract public void onMessage(AtmosphereResponse response, T message) throws IOException;

    /**
     * This method will be invoked during the process of resuming a connection. By default this method does nothing.
     * @param response an {@link AtmosphereResponse}.
     * @throws IOException
     */
    public void onResume(AtmosphereResponse response) throws IOException{
    }

    /**
     * This method will be invoked when a suspended connection times out, e.g no activity has occurred for the
     * specified time used when suspending. By default this method does nothing.
     *
     * @param response an {@link AtmosphereResponse}.
     * @throws IOException
     */
    public void onTimeout(AtmosphereResponse response) throws IOException{
    }

    /**
     * This method will be invoked when the underlying WebServer detects a connection has been closed. Please
     * note that not all WebServer supports that features (see Atmosphere's WIKI for help). By default this method does nothing.
     *
     * @param response an {@link AtmosphereResponse}.
     * @throws IOException
     */
    public void onDisconnect(AtmosphereResponse response) throws IOException{
    }
}