'use client';

import { useState } from 'react';
import { Box, Container, Typography, Button, Dialog, DialogContent, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import Link from 'next/link';
import dynamic from 'next/dynamic';
import { Advertisement } from '../../types/advertisement';

const AdvertisementList = dynamic(() => import('../../components/AdvertisementList'), {
  loading: () => null,
});
const AdvertisementForm = dynamic(() => import('../../components/AdvertisementForm'), {
  loading: () => null,
});

export default function AdvertisementsPage() {
  const [formOpen, setFormOpen] = useState(false);
  const [currentAdvertisement, setCurrentAdvertisement] = useState<Advertisement | undefined>(undefined);
  const [refreshKey, setRefreshKey] = useState(0);

  const handleAddNew = () => {
    setCurrentAdvertisement(undefined);
    setFormOpen(true);
  };

  const handleEdit = (advertisement: Advertisement) => {
    setCurrentAdvertisement(advertisement);
    setFormOpen(true);
  };

  const handleFormSuccess = () => {
    setFormOpen(false);
    setCurrentAdvertisement(undefined);
    // Trigger a refresh of the advertisement list
    setRefreshKey(prev => prev + 1);
  };

  const handleFormCancel = () => {
    setFormOpen(false);
    setCurrentAdvertisement(undefined);
  };

  return (
    <Box>
      <Box sx={{ bgcolor: 'primary.main', color: 'primary.contrastText', py: { xs: 1.5, md: 2 }, mb: 4 }}>
        <Container>
          <Button
            component={Link}
            href="/"
            startIcon={<ArrowBackIcon />}
            color="inherit"
            sx={{ mb: 2 }}
          >
            Back to Home
          </Button>
          <Typography variant="h4" component="h1">
            Manage Advertisements
          </Typography>
        </Container>
      </Box>

      <Container>
        <AdvertisementList
          key={refreshKey}
          onAddNew={handleAddNew}
          onEdit={handleEdit}
        />

        <Dialog
          open={formOpen}
          onClose={handleFormCancel}
          fullWidth
          maxWidth="md"
          scroll="paper"
        >
          <DialogContent sx={{ position: 'relative', p: 3 }}>
            <IconButton
              aria-label="close"
              onClick={handleFormCancel}
              sx={{
                position: 'absolute',
                right: 8,
                top: 8,
                color: (theme) => theme.palette.grey[500],
              }}
            >
              <CloseIcon />
            </IconButton>
            <AdvertisementForm
              advertisement={currentAdvertisement}
              onSuccess={handleFormSuccess}
              onCancel={handleFormCancel}
            />
          </DialogContent>
        </Dialog>
      </Container>
    </Box>
  );
}