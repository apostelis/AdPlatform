"use client";

import React, { createContext, useContext, useMemo, useState, useCallback } from "react";
import { Advertisement } from "../types/advertisement";

export interface AdsState {
  advertisements: Advertisement[];
  selectedAd?: Advertisement;
  setSelectedAd: (ad?: Advertisement) => void;
  setAdvertisements: (ads: Advertisement[]) => void;
  refreshToken: number;
  triggerRefresh: () => void;
}

const AdsContext = createContext<AdsState | undefined>(undefined);

export function AdsProvider({ children }: { children: React.ReactNode }) {
  const [advertisements, setAdvertisements] = useState<Advertisement[]>([]);
  const [selectedAd, setSelectedAd] = useState<Advertisement | undefined>(undefined);
  const [refreshToken, setRefreshToken] = useState<number>(0);

  const triggerRefresh = useCallback(() => setRefreshToken((v) => v + 1), []);

  const value = useMemo(
    () => ({ advertisements, setAdvertisements, selectedAd, setSelectedAd, refreshToken, triggerRefresh }),
    [advertisements, selectedAd, refreshToken, triggerRefresh]
  );

  return <AdsContext.Provider value={value}>{children}</AdsContext.Provider>;
}

export function useAds(): AdsState {
  const ctx = useContext(AdsContext);
  if (!ctx) {
    throw new Error("useAds must be used within an AdsProvider");
  }
  return ctx;
}
