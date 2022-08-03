package com.energymonitor.extension;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.auth.SimpleAuthenticator;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthInput;
import com.hivemq.extension.sdk.api.auth.parameter.SimpleAuthOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EnergyMonitorAuthenticator implements SimpleAuthenticator {
    private static final @NotNull Logger log = LoggerFactory.getLogger(EnergyMonitorAuthenticator.class);

    private final String DEVICE_VERIFY_TOKEN_URL = System.getenv("DEVICE_VERIFY_TOKEN_URL");

    @Override
    public void onConnect(@NotNull SimpleAuthInput simpleAuthInput, @NotNull SimpleAuthOutput simpleAuthOutput) {

        String clientId = simpleAuthInput.getClientInformation().getClientId();
        if (clientId.startsWith("srv")) {
            log.info("Service authentication attempt" + ": " + clientId);
            simpleAuthOutput.authenticateSuccessfully();
            return;
        }

        Optional<String> optionalUsername = simpleAuthInput.getConnectPacket().getUserName();
        Optional<ByteBuffer> optionalPasswordBytes = simpleAuthInput.getConnectPacket().getPassword();
        if (optionalUsername.isEmpty() || optionalPasswordBytes.isEmpty()) {
            log.error("Device authentication attempt without credentials" + ": " + clientId);
            simpleAuthOutput.failAuthentication();
            return;
        }
        String username = optionalUsername.get();
        String password = StandardCharsets.UTF_8.decode(optionalPasswordBytes.get()).toString();

        log.info(
                String.format(
                        "Device connect attempt %s | username: %s | password: %s",
                        clientId,
                        username, password.length() > 0 ? "***" : "-"
                )
        );

        if (verifyDeviceCredentials(username, password)) {
            simpleAuthOutput.authenticateSuccessfully();
        } else {
            simpleAuthOutput.failAuthentication();
        }
    }

    public boolean verifyDeviceCredentials(String username, String password) {
        URI uri = URI.create(DEVICE_VERIFY_TOKEN_URL);
        String body = String.format("{ \"access_token\": \"%s\" }", password) ;

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info(String.format("Device verified with status %s", response.statusCode()));
            log.info(String.format("Device verified with response %s", response.toString()));
            if (response.statusCode() != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(String.format("Error verify device credentials: %s", e.getMessage()));
            return false;
        }

    }

}
