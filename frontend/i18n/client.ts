"use client";

import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import enCommon from '../locales/en/common.json';
import elCommon from '../locales/el/common.json';

// Prevent re-initialization during Fast Refresh in Next.js
if (!i18n.isInitialized) {
  i18n
    .use(initReactI18next)
    .init({
      resources: {
        en: { common: enCommon },
        el: { common: elCommon },
      },
      lng: 'en',
      fallbackLng: 'en',
      ns: ['common'],
      defaultNS: 'common',
      interpolation: { escapeValue: false },
    })
    .catch((e) => {
      // eslint-disable-next-line no-console
      console.error('[i18n] initialization error', e);
    });
}

export default i18n;
