/*
 * Copyright 2006 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.logging.slf4j;

import java.util.Map;

import org.ops4j.pax.logging.PaxContext;
import org.ops4j.pax.logging.PaxLoggingManager;
import org.ops4j.pax.logging.PaxLoggingService;
import org.slf4j.spi.MDCAdapter;

/**
 * <p>pax-logging specific {@link MDCAdapter} returned from {@link org.slf4j.impl.StaticMDCBinder}</p>
 */
public class Slf4jMDCAdapter implements MDCAdapter {

    /** {@link PaxContext} used when {@link org.ops4j.pax.logging.PaxLoggingService} is not available */
    private static PaxContext m_defaultContext = new PaxContext();
    /** {@link PaxContext} obtained from {@link org.ops4j.pax.logging.PaxLoggingService} */
    private static PaxContext m_context;

    /**
     * <p>For all the methods that use the context, default, static, {@link PaxContext} may be used (tied to pax-logging-api
     * bundle) if there's no available {@link PaxLoggingManager} or {@link PaxLoggingService}. If the service is
     * available, it is <strong>always</strong> used to get service specific {@link PaxContext}.</p>
     * <p>Refering <strong>always</strong> to {@link PaxLoggingService#getPaxContext()} is cheap operation, as it's
     * only reference to fields.</p>
     *
     * <p>See: https://ops4j1.jira.com/browse/PAXLOGGING-247</p>
     *
     * @return m_context if the MDC should use the PaxContext object from the PaxLoggingManager,
     *      or m_defaultContext if the logging manager is not set, or does not have its context available yet.
     */
    private static PaxContext getContext() {
        PaxLoggingManager manager = Slf4jLoggerFactory.m_paxLogging;
        if (manager != null) {
            synchronized (Slf4jMDCAdapter.class) {
                PaxLoggingService service = manager.getPaxLoggingService();
                m_context = service != null ? service.getPaxContext() : null;
            }
        }
        return m_context != null ? m_context : m_defaultContext;
    }

    @Override
    public void put(String key, String val) {
        getContext().put(key, val);
    }

    @Override
    public String get(String key) {
        Object value = getContext().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void remove(String key) {
        getContext().remove(key);
    }

    @Override
    public void clear() {
        getContext().clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map getCopyOfContextMap() {
        return getContext().getCopyOfContextMap();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setContextMap(Map contextMap) {
        getContext().setContextMap(contextMap);
    }

}
