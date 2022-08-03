package com.energymonitor.extension;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.*;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Optional;

public class EnergyMonitorListeners implements ClientLifecycleEventListener {
    private static final @NotNull Logger log = LoggerFactory.getLogger(EnergyMonitorListeners.class);

    @Override
    public void onMqttConnectionStart(final @NotNull ConnectionStartInput connectionStartInput) {
        ConnectPacket connectPacket = connectionStartInput.getConnectPacket();

        Optional<String> username = connectPacket.getUserName();
        if (username.isEmpty()) {
            return;
        }

        if (!username.get().startsWith("srv")) {
            String payload  = String.format("{ \"device\": \"%s\" }", username.get());

            Publish message = Builders.publish()
                    .topic("device/connection/connected")
                    .qos(Qos.AT_LEAST_ONCE)
                    .payload(Charset.forName("UTF-8").encode(payload))
                    .build();
            Services.publishService().publish(message);
        }
    }

    @Override
    public void onAuthenticationSuccessful(final @NotNull AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        String clientId = authenticationSuccessfulInput.getClientInformation().getClientId();

        if (!clientId.startsWith("srv")) {
            String payload  = String.format("{ \"device\": \"%s\" }", clientId);

            Publish message = Builders.publish()
                    .topic("device/authentication/success")
                    .qos(Qos.AT_LEAST_ONCE)
                    .payload(Charset.forName("UTF-8").encode(payload))
                    .build();
            Services.publishService().publish(message);
        }
    }

    @Override
    public void onAuthenticationFailedDisconnect(@NotNull AuthenticationFailedInput authenticationFailedInput) {
        String clientId = authenticationFailedInput.getClientInformation().getClientId();

        if (!clientId.startsWith("srv")) {
            String payload  = String.format("{ \"device\": \"%s\" }", clientId);

            Publish message = Builders.publish()
                    .topic("device/authentication/failed")
                    .qos(Qos.AT_LEAST_ONCE)
                    .payload(Charset.forName("UTF-8").encode(payload))
                    .build();
            Services.publishService().publish(message);
        }
    }

    @Override
    public void onConnectionLost(@NotNull ConnectionLostInput connectionLostInput) {
        String clientId = connectionLostInput.getClientInformation().getClientId();

        if (!clientId.startsWith("srv")) {
            String payload  = String.format("{ \"device\": \"%s\" }", clientId);

            Publish message = Builders.publish()
                    .topic("device/connection/lost")
                    .qos(Qos.AT_LEAST_ONCE)
                    .payload(Charset.forName("UTF-8").encode(payload))
                    .build();
            Services.publishService().publish(message);
        }
    }

    @Override
    public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {
        log.info("Client disconnected with id: {} ", disconnectEventInput.getClientInformation().getClientId());
        String clientId = disconnectEventInput.getClientInformation().getClientId();

        if (!clientId.startsWith("srv")) {
            String payload  = String.format("{ \"device\": \"%s\" }", clientId);

            Publish message = Builders.publish()
                    .topic("device/connection/disconnected")
                    .qos(Qos.AT_LEAST_ONCE)
                    .payload(Charset.forName("UTF-8").encode(payload))
                    .build();
            Services.publishService().publish(message);
        }
    }


}
