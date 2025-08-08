# Frontend State Management

This project uses React Context (AdsContext) for lightweight, app-wide state management aligned with the App Router in Next.js 14.

Rationale:
- Minimal overhead, no middleware required for current needs.
- Coexists well with server components; the provider is placed in `app/layout.tsx`.
- Can be evolved to Redux Toolkit if future complexity demands.

Location:
- Provider and hook: `frontend/store/AdsContext.tsx`
- Wired in: `frontend/app/layout.tsx`

Usage:
- Access the store via the `useAds()` hook inside client components.

Example:
```tsx
'use client';
import { useAds } from '@/store/AdsContext';

export default function Example() {
  const { selectedAd, setSelectedAd, advertisements, setAdvertisements, refreshToken, triggerRefresh } = useAds();
  // ...use state as needed
  return null;
}
```

Notes:
- Only client components can consume the context; place `'use client'` pragma at the top when needed.
- For data fetching, continue to use the service layer and update the context as appropriate.
