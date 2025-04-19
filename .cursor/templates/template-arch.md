# Architecture for {PRD Title}

Status: { Draft | Approved }

## Technical Summary

{ Short 1-2 paragraph }

## Technology Table

Table listing choices for languages, libraries, infra, etc...

  <example>
  | Technology | Description |
  | ------------ | ------------------------------------------------------------- |
  | Kubernetes | Container orchestration platform for microservices deployment |
  | Apache Kafka | Event streaming platform for real-time data ingestion |
  | TimescaleDB | Time-series database for sensor data storage |
  | Go | Primary language for data processing services |
  | GoRilla Mux | REST API Framework |
  | Python | Used for data analysis and ML services |
  </example>

## Architectural Diagrams

{ Mermaid Diagrams to describe key flows interactions or architecture to be followed during implementation, infra provisioning, and deployments }

## Data Models, API Specs, Schemas, etc...

{ As needed - may not be exhaustive - but key ideas that need to be retained and followed into the architecture and stories }

<example>
### Sensor Reading Schema

```json
{
  "sensor_id": "string",
  "timestamp": "datetime",
  "readings": {
    "temperature": "float",
    "pressure": "float",
    "humidity": "float"
  },
  "metadata": {
    "location": "string",
    "calibration_date": "datetime"
  }
}
```

</example>

## Project Structure

{ Diagram the folder and file organization structure along with descriptions }

```
├ /src
├── /services
│   ├── /gateway        # Sensor data ingestion
│   ├── /processor      # Data processing and validation
│   ├── /analytics      # Data analysis and ML
│   └── /notifier       # Alert and notification system
├── /deploy
│   ├── /kubernetes     # K8s manifests
│   └── /terraform      # Infrastructure as Code
└── /docs
    ├── /api           # API documentation
    └── /schemas       # Data schemas
```

### Jetpack Compose Guidelines

* **Single-Activity with NavHost**
* All UI is written using @Composable; no mixing with XML.
* State is hoisted via ViewModel (using Hilt for DI).
* The UI layer must not access the data layer directly.

### Gradle KTS Conventions

* All module build scripts use the `.gradle.kts` suffix.
* **Version catalog** is defined in `gradle/libs.versions.toml`.
* Plugins and dependencies are declared via the catalog:
  ```kotlin
  plugins { alias(libs.plugins.android.app) }
  dependencies { implementation(libs.compose.material3) }
  ```
  
* Reusable tasks (detekt, ktlint) are extracted into `.gradle.kts` scripts inside `build-logic/`.

## Infrastructure

## Deployment Plan

## Change Log
