import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import AdvertisementCard from '../components/AdvertisementCard';
import { AdvertisementSource } from '../types/advertisement';

const baseAd = {
  id: 1,
  title: 'Test Ad',
  description: 'A simple description',
  content: 'Some content',
  source: AdvertisementSource.STORAGE,
  sourceIdentifier: 'file.mp4',
  createdAt: undefined,
  updatedAt: undefined,
  active: true,
  targetUrl: 'https://example.com',
  clickable: true,
  weight: 1,
  overrideStart: undefined,
  overrideEnd: undefined,
  youtubeDetails: undefined,
  geoTargets: [],
  bioTargets: [],
  moodTargets: [],
};

describe('AdvertisementCard', () => {
  it('renders core fields and toggles details dialog', () => {
    render(<AdvertisementCard advertisement={baseAd as any} />);

    expect(screen.getByText('Test Ad')).toBeInTheDocument();
    expect(screen.getByText('A simple description')).toBeInTheDocument();

    const detailsBtn = screen.getByRole('button', { name: /details/i });
    fireEvent.click(detailsBtn);

    // dialog content appears
    expect(screen.getByText(/Content:/)).toBeInTheDocument();

    const closeBtn = screen.getByRole('button', { name: /close/i });
    fireEvent.click(closeBtn);
  });

  it('calls onEdit and onDelete when provided', () => {
    const onEdit = jest.fn();
    const onDelete = jest.fn();

    render(<AdvertisementCard advertisement={baseAd as any} onEdit={onEdit} onDelete={onDelete} />);

    fireEvent.click(screen.getByRole('button', { name: /edit/i }));
    expect(onEdit).toHaveBeenCalledTimes(1);

    fireEvent.click(screen.getByRole('button', { name: /delete/i }));
    expect(onDelete).toHaveBeenCalledWith(1);
  });

  it('renders YouTube preview iframe when source is YOUTUBE with videoId', () => {
    const ytAd = { ...baseAd, source: AdvertisementSource.YOUTUBE, sourceIdentifier: 'abc123' };
    render(<AdvertisementCard advertisement={ytAd as any} />);

    fireEvent.click(screen.getByRole('button', { name: /details/i }));

    const iframe = document.querySelector('iframe');
    expect(iframe).toBeTruthy();
    expect(iframe?.getAttribute('src')).toContain('abc123');
  });
});
