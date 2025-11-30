# JDA Interactions Extension

**JDA Interactions Extension** is a lightweight, annotation-driven framework designed to decouple business logic from
JDA event handling. Inspired by web frameworks like Spring MVC or JAX-RS, it treats Discord interactions as routable
requests, allowing you to focus on *what* your bot does, rather than *how* to parse events.

> Please note that this library is a personal project. Good practice and code quality is done a best-effort basis.

## 🚀 Key Features

* **Annotation-Driven**: Define commands and buttons using `@Slash`, `@Button`, and `@Option`.
* **URI-Based Routing**: Interactions are normalized into URIs (e.g., `slash://command/subcommand`,
  `button://action?id=5`), making routing predictable and consistent.
* **Dependency Injection**: Automatically inject options, resolved entities, or custom attributes into your methods
  using `@Param` and `@Attribute`.
* **Agnostic Design**: The core logic uses a generic `Request` and `Response` model, abstracting away the raw JDA events
  during processing.
* **Extensible**: Easily add custom `SchemeAdapter`s, `Interceptor`s, or `ResponseHandler`s.

## 📦 Installation

**Maven**

```xml

<dependency>
    <groupId>fr.alexpado</groupId>
    <artifactId>jda-interactions-extension</artifactId>
    <version>VERSION</version>
</dependency>
```

**Gradle**

```groovy
implementation 'fr.alexpado:jda-interactions-extension:VERSION'
```

## ⚡ Quick Start & Examples

> *Soon*

