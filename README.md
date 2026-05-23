# LMSMOB Chat (LM Studio Android App)

[![Release](https://img.shields.io/github/v/release/mindylab/lmsmob_chat?label=latest%20APK)](https://github.com/mindylab/lmsmob_chat/releases/latest)
[![Android](https://img.shields.io/badge/Android-26%2B-3DDC84?logo=android&logoColor=white)](app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-7F52FF?logo=kotlin&logoColor=white)](app/src/main/java/com/mindylab/lmstudiochat/MainActivity.kt)
[![License](https://img.shields.io/badge/license-proprietary-lightgrey)](LICENSE.md)

Android chat app for LM Studio. Connect your phone to an LM Studio server
running on your PC and chat with local LLMs over your LAN. Version 1.28 adds
voice input, read-aloud answers, document sharing, and opt-in phone assistant
tools for maps, drafts, contacts, notifications, local files, and device status.

[Download the latest APK](https://github.com/mindylab/lmsmob_chat/releases/latest)
or open the current
[v1.28 APK asset](https://github.com/mindylab/lmsmob_chat/releases/download/v1.28/lmsmob_chat-v1.28-debug.apk).

## Demo

<p align="center">
  <a href="https://github.com/mindylab/lmsmob_chat/releases/download/v1.28/lmsmob-chat-v1.28-demo.mp4">Watch the LMSMOB Chat Android demo video</a>
</p>

<div align="center">
  <table>
    <tr>
      <td bgcolor="black" align="center">
        <video
          src="https://github.com/user-attachments/assets/2233ddcb-c7a8-4783-b36d-9838fa2aa6b3"
          width="360"
          autoplay
          loop
          muted
          playsinline
          controls>
          DEMO
        </video>
      </td>
    </tr>
  </table>
</div>

## Features

- Native Android app built with Kotlin and Jetpack Compose.
- Works with LM Studio's OpenAI-compatible local server at `/v1`.
- Supports LM Studio's native `/api/v1/chat` endpoint for server tools.
- Connects to LM Studio MCP/plugin integrations such as `mcp/local-web`.
- Refreshes loaded LM Studio models from the app settings.
- Keeps chat sessions locally with search and session switching.
- Supports copy, edit, delete, and cancel actions in chat.
- Imports and exports chat history as JSON.
- Supports image attachments for vision-capable models.
- Shares text, images, PDFs, DOC/DOCX, and XLS/XLSX files into the app from
  Android share sheets.
- Converts supported documents into text, and can render PDFs as image
  attachments for vision-capable models.
- Adds optional voice input, text-to-speech output, and automatic answer
  read-aloud.
- Provides opt-in phone assistant tools for URL opening, map routes, email/SMS
  drafts, phone dialing, calendar/reminder drafts, contact lookup, notification
  digest, local file search, and device status.
- Shows context usage estimates and advanced generation settings including
  temperature, top-p, max tokens, context length, penalties, and seed.
- Includes emulator and physical-device LAN setup paths.

## Roadmap

The 2026 roadmap now builds on the first phone-assistant tools shipped in
v1.28:

- Web tools already work through compatible LM Studio MCP/plugin setup, with a
  packaged LMS/LM Studio web plugin planned to make setup easier.
- Maps route drafts, contact lookup, notification digest, local file search,
  device status, and confirmable phone action drafts are available now.
- Phone location tools are still planned with explicit Android permission
  controls.
- Cron-style scheduled tasks are planned for reminders, notifications, and
  safe phone actions.
- A signed release APK workflow is planned for wider distribution.

See the full [2026 roadmap](docs/roadmap-2026.md).

## Quick Setup

1. Install the APK from the
   [latest release](https://github.com/mindylab/lmsmob_chat/releases/latest).
2. Start LM Studio on your PC.
3. Load a model and start the local server from LM Studio.
4. Open LMSMOB Chat settings.
5. Set the LM Studio API URL and model.
6. If LM Studio has authentication enabled, add your LM Studio API token.

The Android emulator default URL is:

```text
http://10.0.2.2:1234/v1
```

For a physical Android device, use your PC's LAN IP address:

```text
http://YOUR_PC_LAN_IP:1234/v1
```

Example:

```text
http://192.168.1.25:1234/v1
```

## Server Tools And MCP

LM Studio 0.4.0+ can call MCP/plugin tools through its native
`/api/v1/chat` endpoint. In LMSMOB Chat, open settings, enable
**Server tools**, and add the LM Studio integration IDs you want to allow.

Server-side tools require an LM Studio API token. In LM Studio, create a
permission token from **Developer > Server Settings > Manage Tokens**, then
paste it into the app's **API token** field. The token must allow the MCP/tool
permissions you want to use.

When **Server tools** is enabled, tap the refresh icon beside the model field.
The app loads model IDs from `/api/v1/models` and prefers currently loaded LLM
instances, which is what `/api/v1/chat` expects.

For an MCP server already configured in LM Studio's `mcp.json`, add one plugin
ID per line:

```text
mcp/local-web
mcp/gemma4-audio-python
```

Bare server labels are normalized by the app. For example, `local-web` becomes
`mcp/local-web`.

For broad local tools, fill **Allowed tools** in the app settings with one tool
name per line. The app will send:

```json
{
  "type": "plugin",
  "id": "mcp/local-web",
  "allowed_tools": ["web_fetch"]
}
```

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

## Build

With Android Studio's JDK and SDK available:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

To build a release APK:

```powershell
.\gradlew.bat assembleRelease
```

The project can sign release builds when a local `keystore.properties` file is
present. See [docs/release-signing.md](docs/release-signing.md). Do not commit
keystores, passwords, or `keystore.properties`.

## Keywords

LM Studio Android app, LM Studio mobile client, Android local LLM chat, local AI
Android app, OpenAI-compatible Android client, MCP Android chat, Kotlin Jetpack
Compose AI chat, local server chat app.

## License

Official LMSMOB Chat app builds are free for personal, non-commercial use.
The source code remains proprietary to MindyLab MB. Redistribution,
modification, commercialization, rebranding, company use, or source-code use
requires prior written permission. See [LICENSE.md](LICENSE.md).
