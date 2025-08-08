# Frontend Testing (Jest + React Testing Library)

We configured Jest and React Testing Library for the Next.js 14 frontend.

How to run:
- cd frontend
- npm install
- npm test

Key files:
- jest.config.js – Jest configuration
- jest.setup.ts – RTL/Jest-DOM setup
- __tests__/ErrorBoundary.test.tsx – Example test for ErrorBoundary
- __tests__/AdvertisementList.test.tsx – Example test for a component with provided props

Notes:
- Tests run in jsdom environment and use ts-jest for TypeScript.
- Prefer testing behavior and accessibility (roles, labels) over implementation details.
