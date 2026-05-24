# 2026 Roadmap

This roadmap describes shipped and planned LMSMOB Chat improvements for making
the Android app a stronger LM Studio companion and phone-side MCP tool host.

The direction is local-first: permissions should be explicit, tools should be
allowlisted, and sensitive phone actions should require clear user control.

## Current Foundation

- Android chat client for LM Studio's OpenAI-compatible `/v1` server.
- Native LM Studio `/api/v1/chat` support for server tools.
- MCP/plugin integration IDs can already be configured from app settings.
- Web tools can already work when LM Studio is configured with a compatible MCP
  or plugin such as `mcp/local-web`.
- Version 1.28 adds Android share-sheet support for text, images, PDFs,
  DOC/DOCX, and XLS/XLSX files.
- Version 1.28 adds document text extraction and PDF-to-image attachment
  conversion for vision-capable models.
- Version 1.28 adds optional voice input, text-to-speech output, automatic
  answer read-aloud, context usage estimates, and advanced generation settings.
- Version 1.28 adds confirmable phone assistant tools for opening URLs, map
  routes, email/SMS drafts, phone dialing, calendar/reminder drafts, contacts,
  notification digest, local file search, and device status.
- Version 1.40 adds alarm tools for reading the next Android alarm, opening the
  Clock alarm screen, and preparing alarm creation drafts.
- Version 1.40 adds Watch Jobs for local notification or scheduled monitoring,
  filter or LM Studio AI matching, normal notifications, full-screen alarm
  alerts, and once/today/no-end lifetimes.
- Version 1.40 adds an optional capability guide so prompts include the current
  model, server-tool, phone-tool, document, voice, vision, and date/time
  capabilities enabled in the app.
- A packaged LMS/LM Studio web plugin is planned so setup becomes easier for
  normal users.

## Q2 2026

- Improve the Server tools settings flow with clearer presets and validation.
- Add a first-run tool setup guide for LM Studio MCP/plugin integrations.
- Document supported web tool IDs and recommended allowlists.
- Improve release packaging with a signed release APK workflow.
- Polish v1.40 Watch Jobs, alarm permissions, and release notes based on early
  tester feedback.

## Q3 2026

- Web MCP tools:
  - Package and document the planned LMS/LM Studio web plugin.
  - Add safer presets for web search and web fetch.
  - Show tool activity clearly in chat responses.
- Maps and phone location tools:
  - Build on the current maps route draft tool.
  - Add opt-in Android location permission flow.
  - Provide approximate/current phone location as an MCP-style tool.
  - Add map/search helpers for nearby places, travel context, and directions.
  - Keep location sharing explicit per session or per request.

## Q4 2026

- Cron-style scheduled tasks:
  - Build on Watch Jobs with broader scheduled prompts and task workflows.
  - Allow scheduled prompts to run against LM Studio when the phone is online.
  - Support simple phone actions after confirmation, such as opening an app,
    copying text, or preparing a message.
  - Add task history, pause/resume, and safe failure notifications.
- Phone assistant tools:
  - Expand the current opt-in device context tools for battery, network,
    storage, notification-aware workflows, and other Android permissions.
  - Expand the current phone action tools while keeping clear confirmation
    before sensitive actions.
  - Create a permission dashboard so users can see and disable every enabled
    phone-side tool.

## Safety And Privacy Principles

- No location, notification, clipboard, or phone-action tool should be enabled
  without explicit user permission.
- Broad tools should be scoped with allowlists.
- Destructive or externally visible phone actions should require confirmation.
- Tool outputs should be visible in chat so users can understand what happened.
- Private tokens, keystores, and local settings must stay out of git releases.

## Ideas Under Consideration

- Plugin marketplace presets for common LM Studio MCP servers.
- Home-screen shortcuts for favorite system profiles.
- Import/export for tool profiles and scheduled tasks.
- Optional on-device model/provider profiles for non-LM Studio backends.
