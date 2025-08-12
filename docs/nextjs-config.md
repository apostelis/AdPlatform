# Next.js Configuration Notes

Date: 2025-08-12

We upgraded/validated the frontend to run on Next.js 15.x. As of Next.js 13 and above, the `swcMinify` option has been removed from `next.config.js` because SWC is the default and only minifier.

Change applied:
- Removed `swcMinify` from `frontend/next.config.js`.

Why:
- Next.js 15 reports: `Invalid next.config.js options detected: Unrecognized key(s) in object: 'swcMinify'`.
- See: https://nextjs.org/docs/messages/invalid-next-config

What to do next:
- No action needed. Minification remains enabled by default.
- If you encounter further config warnings after upgrading Next.js, consult the Next.js upgrade guide and the message reference above.
