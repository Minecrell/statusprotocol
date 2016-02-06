# statusprotocol
statusprotocol is a simple Sponge plugin extending the status ping API with internal protocol versions. If you want to implement advanced features that use the internal protocol versions you can use this as an API to access them. In that case only this plugin needs to be updated with Minecraft version upgrades.

## Download
You can download the plugin from [GitHub releases](https://github.com/Minecrell/statusprotocol/releases).

## Usage
1. Add statusprotocol as a Maven dependency, e.g. with Gradle:

    ```gradle
    repositories {
        maven {
            name = 'minecrell'
            url = 'http://repo.minecrell.net/releases'
        }
    }
    
    dependencies {
        compile 'net.minecrell:statusprotocol:0.1'
    }
    ```
2. Add an (optional) dependency on statusprotocol.
3. Access the protocol version on status ping or set it:

    ```java
    @Listener
    public void onClientPingServer(ClientPingServerEvent event) {
        int clientProtocolVersion = StatusProtocol.getProtocolVersion(event.getClient().getVersion());
        // Set custom version
        StatusProtocol.setVersion(event.getResponse(), "Just another Minecraft version", protocolVersion);
    }
    ```
