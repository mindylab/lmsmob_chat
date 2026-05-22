# LM Studio Chat for Android

A native Android chat client for LM Studio's OpenAI-compatible local server, built with Kotlin and Jetpack Compose.

## Run It

1. Open this folder in Android Studio.
2. Start LM Studio, load a model, and start the local server.
3. Run the app on an emulator or Android device.
4. Open the app settings and choose the API URL/model if needed.
5. If LM Studio has **Require Authentication** enabled, paste your API token into the app settings.

The emulator default is:

```text
http://10.0.2.2:1234/v1
```

For a physical Android device, use your computer's LAN address instead:

```text
http://YOUR_PC_LAN_IP:1234/v1
```

## Server-Side Tools

LM Studio 0.4.0+ can call MCP/plugin tools through its native `/api/v1/chat` endpoint. In the app, open settings, enable **Server tools**, and add the LM Studio integration IDs you want to allow.

Server-side tools require an LM Studio API token. In LM Studio, create a permission token from **Developer > Server Settings > Manage Tokens**, then paste it into the app's **API token** field. The token must allow the MCP/tool permissions you want to use.

When **Server tools** is enabled, tap the refresh icon beside the model field. The app will load model ids from `/api/v1/models` and prefer currently loaded LLM instances, which is what `/api/v1/chat` expects.

The app also re-checks `/api/v1/models` immediately before each server-tools request. If the saved model name is stale, it automatically swaps to the first loaded LLM instance returned by LM Studio.

The app does not force a `context_length` in server-tools requests. LM Studio can reject a native `/api/v1/chat` request when the requested context length does not match an already loaded instance and JIT loading is disabled.

For an MCP server already configured in LM Studio's `mcp.json`, add one plugin ID per line:

```text
mcp/local-web
mcp/gemma4-audio-python
```

Bare server labels are normalized by the app. For example, `local-web` becomes `mcp/local-web`.

For broad local tools, fill **Allowed tools** in the app settings with one tool name per line. The app will send:

```json
{
  "type": "plugin",
  "id": "mcp/local-web",
  "allowed_tools": ["web_fetch"]
}
```

Marketplace/plugin integrations also need their exact LM Studio plugin id, for example `publisher/plugin-name`.

LM Studio does not currently expose an official REST endpoint for listing active marketplace/plugin integrations, so the app keeps this as a manual selection field with common preset chips.

LM Studio Hub plugins run with the LM Studio plugin runtime. If a tool is only running through `lms dev` or another command-line plugin process, it is not automatically available to `/api/v1/chat` as `mcp/<name>`. To expose a command-line tool over the API safely, run it as a real MCP stdio server from LM Studio's `mcp.json`, then reference it from the app as `mcp/<server_label>`.

Against the tested server, `mcp/local-web` accepted these allowed tools:

```text
web_search
web_fetch
web_search_and_fetch
```

For an ephemeral remote MCP, paste a JSON object or JSON array:

```json
{
  "type": "ephemeral_mcp",
  "server_label": "huggingface",
  "server_url": "https://huggingface.co/mcp",
  "allowed_tools": ["model_search"]
}
```

When server tools are enabled, the app sends chat requests to:

```text
http://HOST:1234/api/v1/chat
```

LM Studio server settings must allow the relevant MCP/plugin type.

## Chat History

The app stores chat sessions locally. The side drawer includes chat search and session switching.

User messages include copy and edit buttons. Editing a user message trims the conversation back to that point and puts the old text in the composer so it can be resent.

Settings includes JSON export/import for chat history.

## Build

With Android Studio's JDK and SDK available:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK will be generated under:

```text
app/build/outputs/apk/debug/
```

## License

LMSMOB Chat is proprietary software owned by MindyLab. See
[LICENSE.md](LICENSE.md) for details.
