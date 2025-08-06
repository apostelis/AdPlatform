'use client';

import { useState } from 'react';
import { 
  Box, 
  Container, 
  Typography, 
  Button, 
  Paper, 
  Grid, 
  TextField, 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem, 
  Divider, 
  CircularProgress, 
  Alert,
  Tabs,
  Tab
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SearchIcon from '@mui/icons-material/Search';
import Link from 'next/link';
import AdvertisementList from '../../components/AdvertisementList';
import { Advertisement, Mood, TargetingParams } from '../../types/advertisement';
import advertisementService from '../../services/advertisementService';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tabpanel-${index}`}
      aria-labelledby={`tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

export default function TargetingPage() {
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [advertisements, setAdvertisements] = useState<Advertisement[]>([]);
  const [searchPerformed, setSearchPerformed] = useState(false);

  // Geo targeting state
  const [countryCode, setCountryCode] = useState('');
  const [region, setRegion] = useState('');
  const [city, setCity] = useState('');
  const [latitude, setLatitude] = useState<string>('');
  const [longitude, setLongitude] = useState<string>('');

  // Bio targeting state
  const [age, setAge] = useState<string>('');
  const [gender, setGender] = useState('');
  const [occupation, setOccupation] = useState('');
  const [educationLevel, setEducationLevel] = useState('');
  const [language, setLanguage] = useState('');
  const [interests, setInterests] = useState('');

  // Mood targeting state
  const [mood, setMood] = useState('');
  const [intensity, setIntensity] = useState<string>('');
  const [timeOfDay, setTimeOfDay] = useState('');
  const [dayOfWeek, setDayOfWeek] = useState('');
  const [season, setSeason] = useState('');

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleGeoSearch = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: TargetingParams = {
        countryCode: countryCode || undefined,
        region: region || undefined,
        city: city || undefined,
        latitude: latitude ? parseFloat(latitude) : undefined,
        longitude: longitude ? parseFloat(longitude) : undefined
      };
      
      const results = await advertisementService.getGeoTargetedAdvertisements(
        params.countryCode,
        params.region,
        params.city,
        params.latitude,
        params.longitude
      );
      
      setAdvertisements(results);
      setSearchPerformed(true);
    } catch (err) {
      console.error('Error fetching geo-targeted advertisements:', err);
      setError('Failed to fetch advertisements. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleBioSearch = async () => {
    setLoading(true);
    setError(null);
    try {
      const interestsList = interests.split(',').map(i => i.trim()).filter(i => i);
      
      const results = await advertisementService.getBioTargetedAdvertisements(
        age ? parseInt(age) : undefined,
        gender || undefined,
        occupation || undefined,
        educationLevel || undefined,
        language || undefined,
        interestsList.length > 0 ? interestsList : undefined
      );
      
      setAdvertisements(results);
      setSearchPerformed(true);
    } catch (err) {
      console.error('Error fetching bio-targeted advertisements:', err);
      setError('Failed to fetch advertisements. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleMoodSearch = async () => {
    setLoading(true);
    setError(null);
    try {
      if (!mood) {
        setError('Mood is required for mood-based targeting');
        setLoading(false);
        return;
      }
      
      const results = await advertisementService.getMoodTargetedAdvertisements(
        mood as Mood,
        intensity ? parseInt(intensity) : undefined,
        timeOfDay || undefined,
        dayOfWeek || undefined,
        season || undefined
      );
      
      setAdvertisements(results);
      setSearchPerformed(true);
    } catch (err) {
      console.error('Error fetching mood-targeted advertisements:', err);
      setError('Failed to fetch advertisements. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Box sx={{ bgcolor: 'primary.main', color: 'primary.contrastText', py: 2, mb: 4 }}>
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
            Target Advertisements
          </Typography>
        </Container>
      </Box>

      <Container>
        <Paper sx={{ mb: 4 }}>
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="targeting tabs">
            <Tab label="Geolocation Targeting" />
            <Tab label="Biographical Targeting" />
            <Tab label="Mood Targeting" />
          </Tabs>

          <TabPanel value={tabValue} index={0}>
            <Typography variant="h6" gutterBottom>
              Find Advertisements by Location
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Enter location details to find advertisements targeted to specific geographic areas.
            </Typography>

            <Grid container spacing={3}>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Country Code (ISO)"
                  variant="outlined"
                  value={countryCode}
                  onChange={(e) => setCountryCode(e.target.value)}
                  placeholder="e.g., US, CA, UK"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Region/State"
                  variant="outlined"
                  value={region}
                  onChange={(e) => setRegion(e.target.value)}
                  placeholder="e.g., California, Ontario"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="City"
                  variant="outlined"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="e.g., San Francisco, Toronto"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Latitude"
                  variant="outlined"
                  type="number"
                  value={latitude}
                  onChange={(e) => setLatitude(e.target.value)}
                  placeholder="e.g., 37.7749"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Longitude"
                  variant="outlined"
                  type="number"
                  value={longitude}
                  onChange={(e) => setLongitude(e.target.value)}
                  placeholder="e.g., -122.4194"
                />
              </Grid>
              <Grid item xs={12}>
                <Button
                  variant="contained"
                  startIcon={<SearchIcon />}
                  onClick={handleGeoSearch}
                  disabled={loading}
                >
                  {loading ? <CircularProgress size={24} /> : 'Search'}
                </Button>
              </Grid>
            </Grid>
          </TabPanel>

          <TabPanel value={tabValue} index={1}>
            <Typography variant="h6" gutterBottom>
              Find Advertisements by Biographical Data
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Enter biographical details to find advertisements targeted to specific demographic groups.
            </Typography>

            <Grid container spacing={3}>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Age"
                  variant="outlined"
                  type="number"
                  value={age}
                  onChange={(e) => setAge(e.target.value)}
                  placeholder="e.g., 30"
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <FormControl fullWidth>
                  <InputLabel id="gender-label">Gender</InputLabel>
                  <Select
                    labelId="gender-label"
                    value={gender}
                    label="Gender"
                    onChange={(e) => setGender(e.target.value)}
                  >
                    <MenuItem value="">Any</MenuItem>
                    <MenuItem value="MALE">Male</MenuItem>
                    <MenuItem value="FEMALE">Female</MenuItem>
                    <MenuItem value="OTHER">Other</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Occupation"
                  variant="outlined"
                  value={occupation}
                  onChange={(e) => setOccupation(e.target.value)}
                  placeholder="e.g., Engineer, Teacher"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Education Level"
                  variant="outlined"
                  value={educationLevel}
                  onChange={(e) => setEducationLevel(e.target.value)}
                  placeholder="e.g., Bachelor's, Master's"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Language"
                  variant="outlined"
                  value={language}
                  onChange={(e) => setLanguage(e.target.value)}
                  placeholder="e.g., English, Spanish"
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Interests (comma-separated)"
                  variant="outlined"
                  value={interests}
                  onChange={(e) => setInterests(e.target.value)}
                  placeholder="e.g., Sports, Music, Technology"
                />
              </Grid>
              <Grid item xs={12}>
                <Button
                  variant="contained"
                  startIcon={<SearchIcon />}
                  onClick={handleBioSearch}
                  disabled={loading}
                >
                  {loading ? <CircularProgress size={24} /> : 'Search'}
                </Button>
              </Grid>
            </Grid>
          </TabPanel>

          <TabPanel value={tabValue} index={2}>
            <Typography variant="h6" gutterBottom>
              Find Advertisements by Mood
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              Enter mood and contextual details to find advertisements targeted to specific emotional states.
            </Typography>

            <Grid container spacing={3}>
              <Grid item xs={12} sm={6} md={4}>
                <FormControl fullWidth required>
                  <InputLabel id="mood-label">Mood</InputLabel>
                  <Select
                    labelId="mood-label"
                    value={mood}
                    label="Mood *"
                    onChange={(e) => setMood(e.target.value)}
                  >
                    <MenuItem value="">Select a mood</MenuItem>
                    <MenuItem value="HAPPY">Happy</MenuItem>
                    <MenuItem value="SAD">Sad</MenuItem>
                    <MenuItem value="EXCITED">Excited</MenuItem>
                    <MenuItem value="RELAXED">Relaxed</MenuItem>
                    <MenuItem value="STRESSED">Stressed</MenuItem>
                    <MenuItem value="BORED">Bored</MenuItem>
                    <MenuItem value="FOCUSED">Focused</MenuItem>
                    <MenuItem value="TIRED">Tired</MenuItem>
                    <MenuItem value="ENERGETIC">Energetic</MenuItem>
                    <MenuItem value="NEUTRAL">Neutral</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Intensity (1-10)"
                  variant="outlined"
                  type="number"
                  value={intensity}
                  onChange={(e) => setIntensity(e.target.value)}
                  placeholder="e.g., 7"
                  inputProps={{ min: 1, max: 10 }}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  fullWidth
                  label="Time of Day"
                  variant="outlined"
                  value={timeOfDay}
                  onChange={(e) => setTimeOfDay(e.target.value)}
                  placeholder="e.g., Morning, Evening"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Day of Week"
                  variant="outlined"
                  value={dayOfWeek}
                  onChange={(e) => setDayOfWeek(e.target.value)}
                  placeholder="e.g., Monday, Weekend"
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Season"
                  variant="outlined"
                  value={season}
                  onChange={(e) => setSeason(e.target.value)}
                  placeholder="e.g., Summer, Winter"
                />
              </Grid>
              <Grid item xs={12}>
                <Button
                  variant="contained"
                  startIcon={<SearchIcon />}
                  onClick={handleMoodSearch}
                  disabled={loading}
                >
                  {loading ? <CircularProgress size={24} /> : 'Search'}
                </Button>
              </Grid>
            </Grid>
          </TabPanel>
        </Paper>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {searchPerformed && (
          <>
            <Divider sx={{ my: 4 }} />
            <Typography variant="h5" gutterBottom>
              Search Results
            </Typography>
            <AdvertisementList
              title=""
              showControls={false}
              searchEnabled={false}
              advertisements={advertisements}
            />
          </>
        )}
      </Container>
    </Box>
  );
}