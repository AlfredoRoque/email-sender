# email-sender

Microservicio Spring Boot que actúa como consumidor Kafka para el envío de correos electrónicos a través de la API de SendGrid.

## Descripción general

El servicio escucha eventos publicados en un topic de Kafka (`email-topic`), deserializa el mensaje como un `EmailEventDto` y delega el envío del correo a SendGrid. Está diseñado para ser stateless, tolerante a fallos y fácilmente integrable en una arquitectura orientada a eventos.

---

## EmailConsumer

**Clase:** `com.roridea.email_sender.consumer.EmailConsumer`

### Función

Es el punto de entrada de todos los eventos de correo. Utiliza la anotación `@KafkaListener` para suscribirse al topic `email-topic` con el grupo de consumidores `email-group`. Al recibir un mensaje, extrae el `EmailEventDto` deserializado y lo pasa al `EmailSenderService` para su procesamiento.

### Flujo de procesamiento

```
Kafka topic: email-topic
        │
        ▼
  EmailConsumer.listen(EmailEventDto)
        │
        ▼
  EmailSenderService.sendEmail(EmailEventDto)
        │
        ▼
  SendGrid API  ──► Correo entregado al destinatario
```

### Reintentos y tolerancia a fallos

El consumer está anotado con `@RetryableTopic`, lo que habilita el mecanismo de reintento no-bloqueante de Spring Kafka:

| Parámetro         | Valor              | Descripción                                               |
|-------------------|--------------------|-----------------------------------------------------------|
| `attempts`        | `3`                | Número máximo de intentos (incluido el original)          |
| `backoff delay`   | `5000 ms`          | Tiempo de espera entre reintentos                         |
| `retryTopicSuffix`| `-retry`           | Topic usado para mensajes en reintento: `email-topic-retry` |
| `dltTopicSuffix`  | `-dlt`             | Dead Letter Topic para mensajes fallidos: `email-topic-dlt` |

Si los 3 intentos fallan, el mensaje es redirigido al topic **`email-topic-dlt`** para su análisis o reprocesamiento manual.

### Payload esperado (`EmailEventDto`)

| Campo            | Tipo     | Obligatorio | Descripción                          |
|------------------|----------|-------------|--------------------------------------|
| `to`             | `String` | Sí          | Dirección de correo del destinatario |
| `subject`        | `String` | Sí          | Asunto del correo                    |
| `htmlContent`    | `String` | Sí          | Cuerpo del correo en formato HTML    |
| `attachment`     | `byte[]` | No          | Archivo adjunto en bytes             |
| `attachmentName` | `String` | No          | Nombre del archivo adjunto           |

Ejemplo de mensaje JSON publicado en `email-topic`:

```json
{
  "to": "usuario@ejemplo.com",
  "subject": "Bienvenido",
  "htmlContent": "<h1>Hola!</h1><p>Gracias por registrarte.</p>",
  "attachment": null,
  "attachmentName": null
}
```

---

## Infraestructura Kafka

El servicio se conecta a un cluster de Kafka en **Aiven** mediante SSL (mTLS). Las credenciales y certificados se configuran a través de variables de entorno.

### Variables de entorno requeridas

| Variable                | Descripción                                      |
|-------------------------|--------------------------------------------------|
| `AIVEN_HOST`            | Host y puerto del broker Kafka de Aiven          |
| `KAFKA_TRUSTSTORE_BASE64` | Truststore PKCS12 codificado en Base64         |
| `KAFKA_KEYSTORE_BASE64`   | Keystore PKCS12 codificado en Base64           |
| `APIKEY`                | API Key de SendGrid                              |
| `MAILFROM`              | Dirección de correo remitente verificada en SendGrid |

---

## Ejecución

```bash
./mvnw spring-boot:run
```

El servicio arranca en el puerto **8083**.

---

## Tecnologías

- Java 17 + Spring Boot
- Spring Kafka (consumer con reintentos no-bloqueantes)
- SendGrid Java SDK
- Kafka sobre Aiven con SSL/mTLS
- Lombok
