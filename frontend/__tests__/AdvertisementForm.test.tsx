import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AdvertisementForm from '../components/AdvertisementForm';
import { AdvertisementSource } from '../types/advertisement';
import advertisementService from '../services/advertisementService';

jest.mock('../services/advertisementService', () => ({
  __esModule: true,
  default: {
    createAdvertisement: jest.fn().mockResolvedValue({ id: 2, title: 'Created Ad' }),
    updateAdvertisement: jest.fn().mockResolvedValue({ id: 2, title: 'Updated Ad' }),
  },
}));

describe('AdvertisementForm', () => {
  it('shows validation errors on submit with empty required fields', async () => {
    render(<AdvertisementForm />);

    fireEvent.click(screen.getByRole('button', { name: /create advertisement/i }));

    expect(await screen.findAllByText(/is required/i)).toHaveLength(3);
  });

  it('submits successfully and calls onSuccess', async () => {
    const onSuccess = jest.fn();
    render(<AdvertisementForm onSuccess={onSuccess} />);

    // Basic tab inputs
    fireEvent.change(screen.getByLabelText(/title/i), { target: { value: 'My Ad' } });
    fireEvent.change(screen.getByLabelText(/^content$/i), { target: { value: 'Some content' } });

    // Source and identifier
    fireEvent.mouseDown(screen.getByLabelText(/source/i));
    const option = await screen.findByRole('option', { name: /storage/i });
    fireEvent.click(option);
    fireEvent.change(screen.getByLabelText(/source identifier/i), { target: { value: 'file.mp4' } });

    fireEvent.click(screen.getByRole('button', { name: /create advertisement/i }));

    await waitFor(() => {
      expect(advertisementService.createAdvertisement).toHaveBeenCalled();
      expect(onSuccess).toHaveBeenCalled();
    });
  });
});
