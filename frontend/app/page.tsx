'use client';

import { useState, useEffect } from 'react';
import { Box, Typography, Container, AppBar, Toolbar, Button, Tab, Tabs } from '@mui/material';
import Link from 'next/link';

export default function Home() {
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Advertisement Platform
          </Typography>
          <Button aria-label="Go to advertisements management" color="inherit" component={Link} href="/advertisements">
            Manage Ads
          </Button>
          <Button aria-label="Go to targeting page" color="inherit" component={Link} href="/targeting">
            Targeting
          </Button>
        </Toolbar>
      </AppBar>

      <Container component="main" sx={{ mt: 4, mb: 4, flexGrow: 1 }}>
        <Box sx={{ my: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom sx={{ fontSize: { xs: '1.75rem', sm: '2rem', md: '2.125rem' } }}>
            Welcome to the Advertisement Platform
          </Typography>
          
          <Typography variant="body1" paragraph>
            This platform allows you to manage advertisements and target them based on:
          </Typography>
          
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="targeting tabs">
            <Tab id="tab-geo" aria-controls="tabpanel-0" label="Geolocation" />
            <Tab id="tab-bio" aria-controls="tabpanel-1" label="Biographical Data" />
            <Tab id="tab-mood" aria-controls="tabpanel-2" label="User Mood" />
          </Tabs>
          
          <Box sx={{ p: 3 }}>
            {tabValue === 0 && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Geolocation Targeting
                </Typography>
                <Typography variant="body1" paragraph>
                  Target your advertisements based on user location, including country, region, city, and proximity.
                </Typography>
                <Typography variant="body2">
                  Our platform uses precise geolocation data to ensure your ads reach the right audience in the right places.
                </Typography>
              </Box>
            )}
            
            {tabValue === 1 && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Biographical Targeting
                </Typography>
                <Typography variant="body1" paragraph>
                  Target your advertisements based on user demographics, including age, gender, occupation, education level, and interests.
                </Typography>
                <Typography variant="body2">
                  Reach specific demographic segments with tailored content that resonates with their background and interests.
                </Typography>
              </Box>
            )}
            
            {tabValue === 2 && (
              <Box>
                <Typography variant="h6" gutterBottom>
                  Mood-Based Targeting
                </Typography>
                <Typography variant="body1" paragraph>
                  Target your advertisements based on user mood and context, including emotional state, time of day, day of week, and season.
                </Typography>
                <Typography variant="body2">
                  Connect with users when they're in the right emotional state to engage with your content.
                </Typography>
              </Box>
            )}
          </Box>
          
          <Box sx={{ mt: 4 }}>
            <Button variant="contained" component={Link} href="/advertisements">
              Get Started
            </Button>
          </Box>
        </Box>
      </Container>
      
      <Box component="footer" sx={{ py: 3, px: 2, mt: 'auto', backgroundColor: (theme) => theme.palette.grey[200] }}>
        <Container maxWidth="sm">
          <Typography variant="body2" color="text.secondary" align="center">
            Advertisement Platform © {new Date().getFullYear()}
          </Typography>
        </Container>
      </Box>
    </Box>
  );
}