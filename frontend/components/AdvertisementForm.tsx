'use client';

import { useState } from 'react';
import { Formik, Form, Field, FieldArray } from 'formik';
import * as Yup from 'yup';
import {
  Box,
  Button,
  TextField,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  Switch,
  Typography,
  Divider,
  Paper,
  IconButton,
  Tabs,
  Tab,
  Grid,
  Alert,
  CircularProgress
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import {
  Advertisement,
  AdvertisementSource,
  GeoTarget,
  BioTarget,
  MoodTarget,
  Gender,
  Mood
} from '../types/advertisement';
import advertisementService from '../services/advertisementService';

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

interface AdvertisementFormProps {
  advertisement?: Advertisement;
  onSuccess?: (advertisement: Advertisement) => void;
  onCancel?: () => void;
}

const emptyAdvertisement: Advertisement = {
  title: '',
  description: '',
  content: '',
  source: AdvertisementSource.STORAGE,
  sourceIdentifier: '',
  active: true,
  geoTargets: [],
  bioTargets: [],
  moodTargets: []
};

const validationSchema = Yup.object({
  title: Yup.string().required('Title is required'),
  content: Yup.string().required('Content is required'),
  source: Yup.string().required('Source is required'),
  sourceIdentifier: Yup.string().required('Source identifier is required'),
  geoTargets: Yup.array().of(
    Yup.object({
      countryCode: Yup.string().required('Country code is required')
    })
  ),
  bioTargets: Yup.array().of(
    Yup.object({
      minAge: Yup.number().nullable().min(0, 'Min age must be positive'),
      maxAge: Yup.number().nullable().min(0, 'Max age must be positive')
        .test('max-greater-than-min', 'Max age must be greater than min age', 
          function(value) {
            const { minAge } = this.parent;
            return !value || !minAge || value >= minAge;
          })
    })
  ),
  moodTargets: Yup.array().of(
    Yup.object({
      mood: Yup.string().required('Mood is required'),
      intensityMin: Yup.number().nullable().min(1, 'Min intensity must be between 1 and 10').max(10, 'Min intensity must be between 1 and 10'),
      intensityMax: Yup.number().nullable().min(1, 'Max intensity must be between 1 and 10').max(10, 'Max intensity must be between 1 and 10')
        .test('max-greater-than-min', 'Max intensity must be greater than min intensity', 
          function(value) {
            const { intensityMin } = this.parent;
            return !value || !intensityMin || value >= intensityMin;
          })
    })
  )
});

export default function AdvertisementForm({
  advertisement = emptyAdvertisement,
  onSuccess,
  onCancel
}: AdvertisementFormProps) {
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isNewAdvertisement = !advertisement.id;

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  const handleSubmit = async (values: Advertisement) => {
    setLoading(true);
    setError(null);
    try {
      let savedAdvertisement;
      if (isNewAdvertisement) {
        savedAdvertisement = await advertisementService.createAdvertisement(values);
      } else {
        savedAdvertisement = await advertisementService.updateAdvertisement(values);
      }
      if (onSuccess) {
        onSuccess(savedAdvertisement);
      }
    } catch (err) {
      console.error('Error saving advertisement:', err);
      setError(`Failed to ${isNewAdvertisement ? 'create' : 'update'} advertisement. Please try again later.`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" component="h2" gutterBottom>
        {isNewAdvertisement ? 'Create New Advertisement' : 'Edit Advertisement'}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Formik
        initialValues={advertisement}
        validationSchema={validationSchema}
        onSubmit={handleSubmit}
      >
        {({ values, errors, touched, handleChange, setFieldValue }) => (
          <Form>
            <Paper sx={{ mb: 3 }}>
              <Tabs value={tabValue} onChange={handleTabChange} aria-label="advertisement form tabs">
                <Tab label="Basic Information" />
                <Tab label="Geo Targeting" />
                <Tab label="Bio Targeting" />
                <Tab label="Mood Targeting" />
              </Tabs>

              <TabPanel value={tabValue} index={0}>
                <Grid container spacing={3}>
                  <Grid item xs={12}>
                    <Field
                      as={TextField}
                      fullWidth
                      name="title"
                      label="Title"
                      variant="outlined"
                      error={touched.title && Boolean(errors.title)}
                      helperText={touched.title && errors.title}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <Field
                      as={TextField}
                      fullWidth
                      name="description"
                      label="Description"
                      variant="outlined"
                      multiline
                      rows={3}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <Field
                      as={TextField}
                      fullWidth
                      name="content"
                      label="Content"
                      variant="outlined"
                      multiline
                      rows={5}
                      error={touched.content && Boolean(errors.content)}
                      helperText={touched.content && errors.content}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <FormControl fullWidth error={touched.source && Boolean(errors.source)}>
                      <InputLabel id="source-label">Source</InputLabel>
                      <Field
                        as={Select}
                        labelId="source-label"
                        name="source"
                        label="Source"
                      >
                        <MenuItem value={AdvertisementSource.STORAGE}>Storage</MenuItem>
                        <MenuItem value={AdvertisementSource.YOUTUBE}>YouTube</MenuItem>
                      </Field>
                      {touched.source && errors.source && (
                        <FormHelperText>{errors.source}</FormHelperText>
                      )}
                    </FormControl>
                  </Grid>

                  <Grid item xs={12} sm={6}>
                    <Field
                      as={TextField}
                      fullWidth
                      name="sourceIdentifier"
                      label={values.source === AdvertisementSource.YOUTUBE ? 'YouTube Video ID' : 'File Path'}
                      variant="outlined"
                      error={touched.sourceIdentifier && Boolean(errors.sourceIdentifier)}
                      helperText={touched.sourceIdentifier && errors.sourceIdentifier}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <FormControlLabel
                      control={
                        <Switch
                          checked={values.active}
                          onChange={handleChange}
                          name="active"
                          color="primary"
                        />
                      }
                      label="Active"
                    />
                  </Grid>
                </Grid>
              </TabPanel>

              <TabPanel value={tabValue} index={1}>
                <FieldArray name="geoTargets">
                  {({ push, remove }) => (
                    <Box>
                      <Typography variant="h6" gutterBottom>
                        Geolocation Targeting
                      </Typography>
                      <Typography variant="body2" color="text.secondary" paragraph>
                        Target your advertisements based on user location.
                      </Typography>

                      {values.geoTargets && values.geoTargets.length > 0 ? (
                        values.geoTargets.map((target, index) => (
                          <Paper key={index} sx={{ p: 2, mb: 2, position: 'relative' }}>
                            <IconButton
                              aria-label="delete"
                              onClick={() => remove(index)}
                              sx={{ position: 'absolute', top: 8, right: 8 }}
                            >
                              <DeleteIcon />
                            </IconButton>

                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`geoTargets.${index}.countryCode`}
                                  label="Country Code (ISO)"
                                  variant="outlined"
                                  error={
                                    touched.geoTargets?.[index]?.countryCode && 
                                    Boolean(errors.geoTargets?.[index]?.countryCode)
                                  }
                                  helperText={
                                    touched.geoTargets?.[index]?.countryCode && 
                                    errors.geoTargets?.[index]?.countryCode
                                  }
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`geoTargets.${index}.region`}
                                  label="Region/State"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`geoTargets.${index}.city`}
                                  label="City"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <FormControlLabel
                                  control={
                                    <Switch
                                      checked={target.include}
                                      onChange={(e) => {
                                        setFieldValue(`geoTargets.${index}.include`, e.target.checked);
                                      }}
                                      name={`geoTargets.${index}.include`}
                                      color="primary"
                                    />
                                  }
                                  label={target.include ? "Include" : "Exclude"}
                                />
                              </Grid>

                              <Grid item xs={12}>
                                <Typography variant="subtitle2" gutterBottom>
                                  Proximity Targeting (Optional)
                                </Typography>
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`geoTargets.${index}.latitude`}
                                  label="Latitude"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`geoTargets.${index}.longitude`}
                                  label="Longitude"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`geoTargets.${index}.radiusKm`}
                                  label="Radius (km)"
                                  variant="outlined"
                                />
                              </Grid>
                            </Grid>
                          </Paper>
                        ))
                      ) : (
                        <Typography color="text.secondary" sx={{ my: 2 }}>
                          No geolocation targeting rules defined.
                        </Typography>
                      )}

                      <Button
                        variant="outlined"
                        startIcon={<AddIcon />}
                        onClick={() => push({ countryCode: '', include: true })}
                        sx={{ mt: 2 }}
                      >
                        Add Geolocation Target
                      </Button>
                    </Box>
                  )}
                </FieldArray>
              </TabPanel>

              <TabPanel value={tabValue} index={2}>
                <FieldArray name="bioTargets">
                  {({ push, remove }) => (
                    <Box>
                      <Typography variant="h6" gutterBottom>
                        Biographical Targeting
                      </Typography>
                      <Typography variant="body2" color="text.secondary" paragraph>
                        Target your advertisements based on user demographics.
                      </Typography>

                      {values.bioTargets && values.bioTargets.length > 0 ? (
                        values.bioTargets.map((target, index) => (
                          <Paper key={index} sx={{ p: 2, mb: 2, position: 'relative' }}>
                            <IconButton
                              aria-label="delete"
                              onClick={() => remove(index)}
                              sx={{ position: 'absolute', top: 8, right: 8 }}
                            >
                              <DeleteIcon />
                            </IconButton>

                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`bioTargets.${index}.minAge`}
                                  label="Min Age"
                                  variant="outlined"
                                  error={
                                    touched.bioTargets?.[index]?.minAge && 
                                    Boolean(errors.bioTargets?.[index]?.minAge)
                                  }
                                  helperText={
                                    touched.bioTargets?.[index]?.minAge && 
                                    errors.bioTargets?.[index]?.minAge
                                  }
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`bioTargets.${index}.maxAge`}
                                  label="Max Age"
                                  variant="outlined"
                                  error={
                                    touched.bioTargets?.[index]?.maxAge && 
                                    Boolean(errors.bioTargets?.[index]?.maxAge)
                                  }
                                  helperText={
                                    touched.bioTargets?.[index]?.maxAge && 
                                    errors.bioTargets?.[index]?.maxAge
                                  }
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <FormControl fullWidth>
                                  <InputLabel id={`gender-label-${index}`}>Gender</InputLabel>
                                  <Field
                                    as={Select}
                                    labelId={`gender-label-${index}`}
                                    name={`bioTargets.${index}.gender`}
                                    label="Gender"
                                  >
                                    <MenuItem value={Gender.MALE}>Male</MenuItem>
                                    <MenuItem value={Gender.FEMALE}>Female</MenuItem>
                                    <MenuItem value={Gender.OTHER}>Other</MenuItem>
                                    <MenuItem value={Gender.ALL}>All</MenuItem>
                                  </Field>
                                </FormControl>
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <FormControlLabel
                                  control={
                                    <Switch
                                      checked={target.include}
                                      onChange={(e) => {
                                        setFieldValue(`bioTargets.${index}.include`, e.target.checked);
                                      }}
                                      name={`bioTargets.${index}.include`}
                                      color="primary"
                                    />
                                  }
                                  label={target.include ? "Include" : "Exclude"}
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`bioTargets.${index}.occupation`}
                                  label="Occupation"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`bioTargets.${index}.educationLevel`}
                                  label="Education Level"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`bioTargets.${index}.language`}
                                  label="Language"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`bioTargets.${index}.interestCategory`}
                                  label="Interest Category"
                                  variant="outlined"
                                />
                              </Grid>
                            </Grid>
                          </Paper>
                        ))
                      ) : (
                        <Typography color="text.secondary" sx={{ my: 2 }}>
                          No biographical targeting rules defined.
                        </Typography>
                      )}

                      <Button
                        variant="outlined"
                        startIcon={<AddIcon />}
                        onClick={() => push({ include: true })}
                        sx={{ mt: 2 }}
                      >
                        Add Biographical Target
                      </Button>
                    </Box>
                  )}
                </FieldArray>
              </TabPanel>

              <TabPanel value={tabValue} index={3}>
                <FieldArray name="moodTargets">
                  {({ push, remove }) => (
                    <Box>
                      <Typography variant="h6" gutterBottom>
                        Mood Targeting
                      </Typography>
                      <Typography variant="body2" color="text.secondary" paragraph>
                        Target your advertisements based on user mood and context.
                      </Typography>

                      {values.moodTargets && values.moodTargets.length > 0 ? (
                        values.moodTargets.map((target, index) => (
                          <Paper key={index} sx={{ p: 2, mb: 2, position: 'relative' }}>
                            <IconButton
                              aria-label="delete"
                              onClick={() => remove(index)}
                              sx={{ position: 'absolute', top: 8, right: 8 }}
                            >
                              <DeleteIcon />
                            </IconButton>

                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={6}>
                                <FormControl fullWidth error={
                                  touched.moodTargets?.[index]?.mood && 
                                  Boolean(errors.moodTargets?.[index]?.mood)
                                }>
                                  <InputLabel id={`mood-label-${index}`}>Mood</InputLabel>
                                  <Field
                                    as={Select}
                                    labelId={`mood-label-${index}`}
                                    name={`moodTargets.${index}.mood`}
                                    label="Mood"
                                  >
                                    {Object.values(Mood).map((mood) => (
                                      <MenuItem key={mood} value={mood}>{mood}</MenuItem>
                                    ))}
                                  </Field>
                                  {touched.moodTargets?.[index]?.mood && errors.moodTargets?.[index]?.mood && (
                                    <FormHelperText>{errors.moodTargets?.[index]?.mood}</FormHelperText>
                                  )}
                                </FormControl>
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <FormControlLabel
                                  control={
                                    <Switch
                                      checked={target.include}
                                      onChange={(e) => {
                                        setFieldValue(`moodTargets.${index}.include`, e.target.checked);
                                      }}
                                      name={`moodTargets.${index}.include`}
                                      color="primary"
                                    />
                                  }
                                  label={target.include ? "Include" : "Exclude"}
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`moodTargets.${index}.intensityMin`}
                                  label="Min Intensity (1-10)"
                                  variant="outlined"
                                  error={
                                    touched.moodTargets?.[index]?.intensityMin && 
                                    Boolean(errors.moodTargets?.[index]?.intensityMin)
                                  }
                                  helperText={
                                    touched.moodTargets?.[index]?.intensityMin && 
                                    errors.moodTargets?.[index]?.intensityMin
                                  }
                                />
                              </Grid>

                              <Grid item xs={12} sm={6}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  type="number"
                                  name={`moodTargets.${index}.intensityMax`}
                                  label="Max Intensity (1-10)"
                                  variant="outlined"
                                  error={
                                    touched.moodTargets?.[index]?.intensityMax && 
                                    Boolean(errors.moodTargets?.[index]?.intensityMax)
                                  }
                                  helperText={
                                    touched.moodTargets?.[index]?.intensityMax && 
                                    errors.moodTargets?.[index]?.intensityMax
                                  }
                                />
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`moodTargets.${index}.timeOfDay`}
                                  label="Time of Day"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`moodTargets.${index}.dayOfWeek`}
                                  label="Day of Week"
                                  variant="outlined"
                                />
                              </Grid>

                              <Grid item xs={12} sm={4}>
                                <Field
                                  as={TextField}
                                  fullWidth
                                  name={`moodTargets.${index}.season`}
                                  label="Season"
                                  variant="outlined"
                                />
                              </Grid>
                            </Grid>
                          </Paper>
                        ))
                      ) : (
                        <Typography color="text.secondary" sx={{ my: 2 }}>
                          No mood targeting rules defined.
                        </Typography>
                      )}

                      <Button
                        variant="outlined"
                        startIcon={<AddIcon />}
                        onClick={() => push({ mood: Mood.HAPPY, include: true })}
                        sx={{ mt: 2 }}
                      >
                        Add Mood Target
                      </Button>
                    </Box>
                  )}
                </FieldArray>
              </TabPanel>
            </Paper>

            <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
              {onCancel && (
                <Button variant="outlined" onClick={onCancel} disabled={loading}>
                  Cancel
                </Button>
              )}
              <Button
                type="submit"
                variant="contained"
                disabled={loading}
                startIcon={loading && <CircularProgress size={20} />}
              >
                {isNewAdvertisement ? 'Create' : 'Update'} Advertisement
              </Button>
            </Box>
          </Form>
        )}
      </Formik>
    </Box>
  );
}