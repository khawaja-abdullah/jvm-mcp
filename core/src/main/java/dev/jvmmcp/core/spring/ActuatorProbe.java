package dev.jvmmcp.core.spring;

import com.sun.tools.attach.VirtualMachine;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Probes a running Spring Boot application for exposed Actuator HTTP endpoints via discovered system properties.
 */
public class ActuatorProbe {

    private final HttpClient httpClient;

    public ActuatorProbe() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(1500))
            .build();
    }

    public ActuatorProbe(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Optional<ProbeResult> probe(VirtualMachine vm) {
        if (vm == null) {
            return Optional.empty();
        }

        Set<Integer> candidatePorts = extractCandidatePorts(vm);
        for (int port : candidatePorts) {
            Optional<String> json = probePort(port);
            if (json.isPresent()) {
                return Optional.of(new ProbeResult("http://localhost:" + port + "/actuator/beans", json.get()));
            }
        }

        return Optional.empty();
    }

    public Optional<String> probePort(int port) {
        String[] candidatePaths = {
            "/actuator/beans",
            "/beans"
        };

        for (String path : candidatePaths) {
            String url = "http://localhost:" + port + path;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(1500))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 && response.body() != null && response.body().contains("beans")) {
                    return Optional.of(response.body());
                }
            } catch (Exception ignored) {
                // Connection refused or timeout means port/endpoint is not accessible
            }
        }

        return Optional.empty();
    }

    public Optional<String> fetchFromUrl(String baseUrl) {
        String targetUrl = baseUrl.endsWith("/actuator/beans") || baseUrl.endsWith("/beans") 
            ? baseUrl 
            : (baseUrl.endsWith("/") ? baseUrl + "actuator/beans" : baseUrl + "/actuator/beans");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .timeout(Duration.ofSeconds(3))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && response.body() != null) {
                return Optional.of(response.body());
            }
        } catch (Exception ignored) {}

        return Optional.empty();
    }

    private Set<Integer> extractCandidatePorts(VirtualMachine vm) {
        Set<Integer> ports = new LinkedHashSet<>();
        try {
            Properties sysProps = vm.getSystemProperties();
            Properties agentProps = vm.getAgentProperties();

            addPortFromProperty(ports, sysProps.getProperty("management.server.port"));
            addPortFromProperty(ports, sysProps.getProperty("server.port"));
            addPortFromProperty(ports, sysProps.getProperty("local.management.port"));
            addPortFromProperty(ports, sysProps.getProperty("local.server.port"));

            addPortFromProperty(ports, agentProps.getProperty("management.server.port"));
            addPortFromProperty(ports, agentProps.getProperty("server.port"));
        } catch (Exception ignored) {}

        // Common default ports to test as fallback
        ports.add(8080);
        ports.add(8081);
        ports.add(9090);

        return ports;
    }

    private void addPortFromProperty(Set<Integer> ports, String portStr) {
        if (portStr != null && !portStr.isBlank()) {
            try {
                int port = Integer.parseInt(portStr.trim());
                if (port > 0 && port <= 65535) {
                    ports.add(port);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    public record ProbeResult(String url, String rawJson) {}
}
