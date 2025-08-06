'use client';

import { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Grid, 
  CircularProgress, 
  Alert, 
  Button, 
  TextField, 
  InputAdornment, 
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';
import AddIcon from '@mui/icons-material/Add';
import AdvertisementCard from './AdvertisementCard';
import { Advertisement } from '../types/advertisement';
import advertisementService from '../services/advertisementService';

interface AdvertisementListProps {
  title?: string;
  showControls?: boolean;
  onAddNew?: () => void;
  onEdit?: (advertisement: Advertisement) => void;
  activeOnly?: boolean;
  searchEnabled?: boolean;
  advertisements?: Advertisement[];
}

/**
 * Component for displaying a list of advertisements.
 * 
 * @param {object} props - Component props
 * @param {string} [props.title='Advertisements'] - Title to display above the list
 * @param {boolean} [props.showControls=true] - Whether to show controls (search, add button)
 * @param {Function} [props.onAddNew] - Callback when Add New button is clicked
 * @param {Function} [props.onEdit] - Callback when Edit button is clicked on an advertisement
 * @param {boolean} [props.activeOnly=false] - Whether to show only active advertisements
 * @param {boolean} [props.searchEnabled=true] - Whether to enable search functionality
 * @param {Advertisement[]} [props.advertisements] - Optional list of advertisements to display instead of fetching from service
 */
export default function AdvertisementList({
  title = 'Advertisements',
  showControls = true,
  onAddNew,
  onEdit,
  activeOnly = false,
  searchEnabled = true,
  advertisements: providedAdvertisements
}: AdvertisementListProps) {
  const [advertisements, setAdvertisements] = useState<Advertisement[]>(providedAdvertisements || []);
  const [loading, setLoading] = useState(providedAdvertisements ? false : true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [advertisementToDelete, setAdvertisementToDelete] = useState<number | null>(null);

  const fetchAdvertisements = async () => {
    setLoading(true);
    setError(null);
    try {
      let ads;
      if (searchTerm) {
        ads = await advertisementService.getAdvertisementsByTitle(searchTerm);
      } else if (activeOnly) {
        ads = await advertisementService.getActiveAdvertisements();
      } else {
        ads = await advertisementService.getAllAdvertisements();
      }
      setAdvertisements(ads);
    } catch (err) {
      console.error('Error fetching advertisements:', err);
      setError('Failed to load advertisements. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (providedAdvertisements) {
      setAdvertisements(providedAdvertisements);
      setLoading(false);
    } else {
      fetchAdvertisements();
    }
  }, [activeOnly, providedAdvertisements]);

  const handleSearch = () => {
    fetchAdvertisements();
  };

  const handleClearSearch = () => {
    setSearchTerm('');
    fetchAdvertisements();
  };

  const handleSearchKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleDelete = (id: number) => {
    setAdvertisementToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (advertisementToDelete) {
      setLoading(true);
      try {
        await advertisementService.deleteAdvertisement(advertisementToDelete);
        setAdvertisements(advertisements.filter(ad => ad.id !== advertisementToDelete));
        setDeleteDialogOpen(false);
        setAdvertisementToDelete(null);
      } catch (err) {
        console.error('Error deleting advertisement:', err);
        setError('Failed to delete advertisement. Please try again later.');
      } finally {
        setLoading(false);
      }
    }
  };

  const cancelDelete = () => {
    setDeleteDialogOpen(false);
    setAdvertisementToDelete(null);
  };

  return (
    <Box>
      {title && (
        <Typography variant="h4" component="h2" gutterBottom>
          {title}
        </Typography>
      )}

      {showControls && (
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3, flexWrap: 'wrap', gap: 2 }}>
          {searchEnabled && (
            <TextField
              label="Search by title"
              variant="outlined"
              size="small"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={handleSearchKeyPress}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
                endAdornment: searchTerm && (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="clear search"
                      onClick={handleClearSearch}
                      edge="end"
                      size="small"
                    >
                      <ClearIcon />
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          )}

          {onAddNew && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={onAddNew}
            >
              Add New Advertisement
            </Button>
          )}
        </Box>
      )}

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      ) : advertisements.length === 0 ? (
        <Alert severity="info" sx={{ mb: 3 }}>
          No advertisements found.
        </Alert>
      ) : (
        <Grid container spacing={3}>
          {advertisements.map((advertisement) => (
            <Grid item xs={12} sm={6} md={4} key={advertisement.id}>
              <AdvertisementCard
                advertisement={advertisement}
                onEdit={onEdit}
                onDelete={showControls ? handleDelete : undefined}
              />
            </Grid>
          ))}
        </Grid>
      )}

      <Dialog
        open={deleteDialogOpen}
        onClose={cancelDelete}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          Confirm Delete
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to delete this advertisement? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelDelete}>Cancel</Button>
          <Button onClick={confirmDelete} color="error" autoFocus>
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
