# DigitalOcean API - Embedded Client Implementation

Part of the [DigitalOcean Droplets](https://github.com/CodexCoder21Organization/ProjectDocumentation/blob/main/projects/DigitalOceanDroplets.md) project.

Provides the `DigitalOceanClient` implementation that makes HTTP requests to the DigitalOcean REST API v2. This module depends on [digitalocean-api-jvm-api](https://github.com/CodexCoder21Organization/digitalocean-api-jvm-api) for all interfaces and data types.

## Maven Coordinates

```
community.kotlin.contrib.digitalocean:embedded:0.0.1
```

## Building

```bash
scripts/build.bash community.kotlin.contrib.digitalocean.embedded.buildMaven()
```

To build a fat JAR with all dependencies:

```bash
scripts/build.bash community.kotlin.contrib.digitalocean.embedded.buildFatJar()
```

## Usage

```java
import community.kotlin.contrib.digitalocean.api.DigitalOcean;
import community.kotlin.contrib.digitalocean.api.pojo.Droplet;
import community.kotlin.contrib.digitalocean.api.pojo.Droplets;
import community.kotlin.contrib.digitalocean.embedded.DigitalOceanClient;

// Create a client (pass your DigitalOcean API token)
DigitalOcean client = new DigitalOceanClient(authToken);

// Or with explicit API version and custom HTTP client
DigitalOcean client = new DigitalOceanClient("v2", authToken, httpClient);

// Use the client through the DigitalOcean interface
Droplets droplets = client.getAvailableDroplets(1, 20);
Droplet droplet = client.getDropletInfo(dropletId);
```

## Architecture

End users can work entirely in terms of the `DigitalOcean` interface (from the `api` module) without depending on this implementation module directly. This allows for testing with mock implementations or swapping in alternative API client implementations.

## Dependencies

- **[digitalocean-api-jvm-api](https://github.com/CodexCoder21Organization/digitalocean-api-jvm-api)** — Interfaces, POJOs, enums, and exceptions
- Apache HttpClient 4.5.12
- Gson 2.8.6
- Apache Commons Lang3 3.10
- SLF4J 1.7.30

## License

MIT License — see upstream [digitalocean-api-java](https://github.com/jeevatkm/digitalocean-api-java).
