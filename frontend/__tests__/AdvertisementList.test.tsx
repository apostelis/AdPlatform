import React from 'react';
import { render, screen } from '@testing-library/react';
import AdvertisementList from '../components/AdvertisementList';
import { Advertisement, AdvertisementSource } from '../types/advertisement';

const sampleAds: Advertisement[] = [
  {
    id: 1,
    title: 'Ad One',
    description: 'Desc',
    content: 'Content',
    source: AdvertisementSource.STORAGE,
    sourceIdentifier: '1',
    active: true,
  },
  {
    id: 2,
    title: 'Ad Two',
    description: 'Desc',
    content: 'Content',
    source: AdvertisementSource.STORAGE,
    sourceIdentifier: '2',
    active: false,
  },
];

describe('AdvertisementList', () => {
  it('renders provided advertisements without fetching', async () => {
    render(<AdvertisementList title="Ads" advertisements={sampleAds} searchEnabled={false} showControls={false} />);

    expect(screen.getByText('Ads')).toBeInTheDocument();
    expect(screen.getByText('Ad One')).toBeInTheDocument();
    expect(screen.getByText('Ad Two')).toBeInTheDocument();
  });
});
