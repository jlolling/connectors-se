package org.talend.components.netsuite.runtime.v2016_2.client;

import com.netsuite.webservices.v2016_2.platform.NetSuitePortType;
import org.talend.components.netsuite.runtime.client.NetSuiteClientFactory;
import org.talend.components.netsuite.runtime.client.NetSuiteClientService;
import org.talend.components.netsuite.runtime.client.NetSuiteException;
import org.talend.components.netsuite.runtime.client.NetSuiteVersion;

/**
 *
 */
public class NetSuiteClientFactoryImpl implements NetSuiteClientFactory<NetSuitePortType> {

    public static final NetSuiteClientFactoryImpl INSTANCE = new NetSuiteClientFactoryImpl();

    public static NetSuiteClientFactory<NetSuitePortType> getFactory() {
        return INSTANCE;
    }

    @Override
    public NetSuiteClientService<NetSuitePortType> createClient() throws NetSuiteException {
        return new NetSuiteClientServiceImpl();
    }

    @Override
    public NetSuiteVersion getApiVersion() {
        return new NetSuiteVersion(2016, 2);
    }
}
