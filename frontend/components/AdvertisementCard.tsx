'use client';

import {useState} from 'react';
import {
    Box,
    Button,
    Card,
    CardActions,
    CardContent,
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Typography
} from '@mui/material';
import {Advertisement, AdvertisementSource} from '@/types/advertisement';

interface AdvertisementCardProps {
  advertisement: Advertisement;
  onEdit?: (advertisement: Advertisement) => void;
  onDelete?: (id: number) => void;
}

/**
 * Component for displaying an advertisement card.
 */
export default function AdvertisementCard({ 
  advertisement, 
  onEdit, 
  onDelete 
}: AdvertisementCardProps) {
  const [showDetails, setShowDetails] = useState(false);

  const handleShowDetails = () => {
    setShowDetails(true);
  };

  const handleCloseDetails = () => {
    setShowDetails(false);
  };

  const handleEdit = () => {
    if (onEdit) {
      onEdit(advertisement);
    }
  };

  const handleDelete = () => {
    if (onDelete && advertisement.id) {
      onDelete(advertisement.id);
    }
  };

  return (
    <>
      <Card className="card">
        <CardContent className="card-content">
          <Typography variant="h6" component="div" gutterBottom>
            {advertisement.title}
          </Typography>
          
          <Typography variant="body2" color="text.secondary" gutterBottom>
            {advertisement.description || 'No description provided'}
          </Typography>
          
          <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
            <Chip 
              label={advertisement.source} 
              color={advertisement.source === AdvertisementSource.YOUTUBE ? 'error' : 'primary'} 
              size="small" 
            />
            <Chip 
              label={advertisement.active ? 'Active' : 'Inactive'} 
              color={advertisement.active ? 'success' : 'default'} 
              size="small" 
            />
            {advertisement.geoTargets && advertisement.geoTargets.length > 0 && (
              <Chip label="Geo Targeted" color="info" size="small" />
            )}
            {advertisement.bioTargets && advertisement.bioTargets.length > 0 && (
              <Chip label="Bio Targeted" color="info" size="small" />
            )}
            {advertisement.moodTargets && advertisement.moodTargets.length > 0 && (
              <Chip label="Mood Targeted" color="info" size="small" />
            )}
          </Box>
        </CardContent>
        
        <CardActions className="card-actions">
          <Button size="small" onClick={handleShowDetails}>Details</Button>
          {advertisement.clickable && advertisement.targetUrl && (
            <Button size="small" color="primary" component="a" href={advertisement.targetUrl} target="_blank" rel="noopener noreferrer">
              Visit
            </Button>
          )}
          {onEdit && <Button size="small" onClick={handleEdit}>Edit</Button>}
          {onDelete && <Button size="small" color="error" onClick={handleDelete}>Delete</Button>}
        </CardActions>
      </Card>

      <Dialog open={showDetails} onClose={handleCloseDetails} maxWidth="md" fullWidth>
        <DialogTitle>{advertisement.title}</DialogTitle>
        <DialogContent dividers>
          <Typography variant="body1" paragraph>
            {advertisement.description || 'No description provided'}
          </Typography>

          {/* Preview section for YouTube advertisements */}
          {advertisement.source === AdvertisementSource.YOUTUBE && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                Preview
              </Typography>
              {(() => {
                const videoId = advertisement.youtubeDetails?.videoId || advertisement.sourceIdentifier;
                const thumbnail = advertisement.youtubeDetails?.thumbnailUrl;
                if (videoId) {
                  return (
                    <Box sx={{ position: 'relative', width: '100%', pt: '56.25%', borderRadius: 1, overflow: 'hidden', bgcolor: 'black' }}>
                      <iframe
                        src={`https://www.youtube.com/embed/${videoId}`}
                        title={advertisement.youtubeDetails?.videoTitle || advertisement.title}
                        style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', border: 0 }}
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                        allowFullScreen
                      />
                    </Box>
                  );
                }
                if (thumbnail) {
                  return (
                    <Box sx={{ position: 'relative', width: '100%', pt: '56.25%', borderRadius: 1, overflow: 'hidden', bgcolor: 'black' }}>
                      {/* eslint-disable-next-line @next/next/no-img-element */}
                      <img
                        src={thumbnail}
                        alt={advertisement.youtubeDetails?.videoTitle || advertisement.title}
                        style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }}
                      />
                    </Box>
                  );
                }
                return null;
              })()}
            </Box>
          )}
          
          <Typography variant="body2" paragraph>
            <strong>Content:</strong> {advertisement.content}
          </Typography>
          
          <Typography variant="body2" paragraph>
            <strong>Source:</strong> {advertisement.source} ({advertisement.sourceIdentifier})
          </Typography>
          
          <Typography variant="body2" paragraph>
            <strong>Status:</strong> {advertisement.active ? 'Active' : 'Inactive'}
          </Typography>
          
          {advertisement.createdAt && (
            <Typography variant="body2" paragraph>
              <strong>Created:</strong> {new Date(advertisement.createdAt).toLocaleString()}
            </Typography>
          )}
          
          {advertisement.updatedAt && (
            <Typography variant="body2" paragraph>
              <strong>Updated:</strong> {new Date(advertisement.updatedAt).toLocaleString()}
            </Typography>
          )}
          
          {advertisement.geoTargets && advertisement.geoTargets.length > 0 && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">Geolocation Targeting</Typography>
              {advertisement.geoTargets.map((target, index) => (
                <Box key={index} sx={{ mt: 1, p: 1, bgcolor: 'background.paper', borderRadius: 1 }}>
                  <Typography variant="body2">
                    <strong>{target.include ? 'Include' : 'Exclude'}</strong> users from {target.countryCode}
                    {target.region && `, ${target.region}`}
                    {target.city && `, ${target.city}`}
                    {target.latitude && target.longitude && `, within ${target.radiusKm || 0}km of (${target.latitude}, ${target.longitude})`}
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
          
          {advertisement.bioTargets && advertisement.bioTargets.length > 0 && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">Biographical Targeting</Typography>
              {advertisement.bioTargets.map((target, index) => (
                <Box key={index} sx={{ mt: 1, p: 1, bgcolor: 'background.paper', borderRadius: 1 }}>
                  <Typography variant="body2">
                    <strong>{target.include ? 'Include' : 'Exclude'}</strong> users
                    {target.minAge !== undefined && target.maxAge !== undefined && ` aged ${target.minAge}-${target.maxAge}`}
                    {target.minAge !== undefined && target.maxAge === undefined && ` aged ${target.minAge}+`}
                    {target.minAge === undefined && target.maxAge !== undefined && ` aged up to ${target.maxAge}`}
                    {target.gender && `, gender: ${target.gender}`}
                    {target.occupation && `, occupation: ${target.occupation}`}
                    {target.educationLevel && `, education: ${target.educationLevel}`}
                    {target.language && `, language: ${target.language}`}
                    {target.interestCategory && `, interested in: ${target.interestCategory}`}
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
          
          {advertisement.moodTargets && advertisement.moodTargets.length > 0 && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">Mood Targeting</Typography>
              {advertisement.moodTargets.map((target, index) => (
                <Box key={index} sx={{ mt: 1, p: 1, bgcolor: 'background.paper', borderRadius: 1 }}>
                  <Typography variant="body2">
                    <strong>{target.include ? 'Include' : 'Exclude'}</strong> users in mood: {target.mood}
                    {target.intensityMin !== undefined && target.intensityMax !== undefined && 
                      `, intensity: ${target.intensityMin}-${target.intensityMax}`}
                    {target.timeOfDay && `, time of day: ${target.timeOfDay}`}
                    {target.dayOfWeek && `, day: ${target.dayOfWeek}`}
                    {target.season && `, season: ${target.season}`}
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDetails}>Close</Button>
          {onEdit && <Button onClick={handleEdit}>Edit</Button>}
        </DialogActions>
      </Dialog>
    </>
  );
}