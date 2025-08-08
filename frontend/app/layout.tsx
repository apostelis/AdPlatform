import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import ThemeRegistry from '../components/ThemeRegistry';
import ErrorBoundary from '../components/ErrorBoundary';
import { AdsProvider } from '../store/AdsContext';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'Advertisement Platform',
  description: 'Platform for managing and targeting advertisements',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ThemeRegistry>
          <AdsProvider>
            <ErrorBoundary>
              {children}
            </ErrorBoundary>
          </AdsProvider>
        </ThemeRegistry>
      </body>
    </html>
  );
}
