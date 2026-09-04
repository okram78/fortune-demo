# Fortune frontend

Vue + Bun frontend for the fortune SSE demo.

## Development

In one terminal, start the Spring Boot backend on port `12500`:

```sh
../gradlew bootRun
```

In another terminal, run the frontend:

```sh
bun install
bun run dev
```

Open `http://localhost:5173`. Vite proxies `/fortune` to the backend.
