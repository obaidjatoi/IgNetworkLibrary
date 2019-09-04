package net.intigral.core.http.agent;

/**
 * Created by Farhan_a on 12/01/2016.
 * <p>
 *
 */
public class TransportAgentFactory {

    private TransportAgentFactory() {

    }

    public static TransportAgentBase getTransportAgent() {
        return HttpTransportAgent.instance();
    }
}
