# Frontend Error Handling

We added a React Error Boundary to catch unexpected render/runtime errors in client components and show a user-friendly fallback with an option to reload the page.

Components:
- `frontend/components/ErrorBoundary.tsx` – a standard error boundary class component.
- Integrated in `frontend/app/layout.tsx` to wrap all pages under `ThemeRegistry` and `AdsProvider`.

Behavior:
- If a render error occurs, users see a prominent alert with a Reload button.
- Errors are logged to the console; integrate a real logging service as needed.

Guidelines alignment:
- Keeps concerns separated and observable, following clean architecture practices.
- Non-intrusive; does not change domain logic.
