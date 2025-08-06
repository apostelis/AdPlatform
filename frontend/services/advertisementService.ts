import axios from 'axios';
import { 
  Advertisement, 
  AdvertisementSource, 
  Mood, 
  UserBioData, 
  TargetingParams 
} from '../types/advertisement';

const API_URL = '/api/advertisements';

/**
 * Service for interacting with the advertisement API.
 */
export const advertisementService = {
  /**
   * Get all advertisements.
   */
  getAllAdvertisements: async (): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(API_URL);
    return response.data;
  },

  /**
   * Get all active advertisements.
   */
  getActiveAdvertisements: async (): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(`${API_URL}/active`);
    return response.data;
  },

  /**
   * Get advertisement by ID.
   */
  getAdvertisementById: async (id: number): Promise<Advertisement> => {
    const response = await axios.get<Advertisement>(`${API_URL}/${id}`);
    return response.data;
  },

  /**
   * Create a new advertisement.
   */
  createAdvertisement: async (advertisement: Advertisement): Promise<Advertisement> => {
    const response = await axios.post<Advertisement>(API_URL, advertisement);
    return response.data;
  },

  /**
   * Update an existing advertisement.
   */
  updateAdvertisement: async (advertisement: Advertisement): Promise<Advertisement> => {
    if (!advertisement.id) {
      throw new Error('Advertisement ID is required for update');
    }
    const response = await axios.put<Advertisement>(`${API_URL}/${advertisement.id}`, advertisement);
    return response.data;
  },

  /**
   * Delete an advertisement by ID.
   */
  deleteAdvertisement: async (id: number): Promise<void> => {
    await axios.delete(`${API_URL}/${id}`);
  },

  /**
   * Get advertisements by source.
   */
  getAdvertisementsBySource: async (source: AdvertisementSource): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(`${API_URL}/source/${source}`);
    return response.data;
  },

  /**
   * Get advertisements by title containing the given text.
   */
  getAdvertisementsByTitle: async (title: string): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(`${API_URL}/search`, {
      params: { title }
    });
    return response.data;
  },

  /**
   * Get targeted advertisements based on user context.
   */
  getTargetedAdvertisements: async (
    countryCode?: string,
    userBioData?: UserBioData,
    mood?: Mood
  ): Promise<Advertisement[]> => {
    const response = await axios.post<Advertisement[]>(`${API_URL}/targeted`, userBioData, {
      params: { 
        countryCode,
        mood
      }
    });
    return response.data;
  },

  /**
   * Get advertisements targeted by geolocation.
   */
  getGeoTargetedAdvertisements: async (
    countryCode?: string,
    region?: string,
    city?: string,
    latitude?: number,
    longitude?: number
  ): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(`${API_URL}/geo-targeted`, {
      params: {
        countryCode,
        region,
        city,
        latitude,
        longitude
      }
    });
    return response.data;
  },

  /**
   * Get advertisements targeted by mood.
   */
  getMoodTargetedAdvertisements: async (
    mood: Mood,
    intensity?: number,
    timeOfDay?: string,
    dayOfWeek?: string,
    season?: string
  ): Promise<Advertisement[]> => {
    const response = await axios.get<Advertisement[]>(`${API_URL}/mood-targeted`, {
      params: {
        mood,
        intensity,
        timeOfDay,
        dayOfWeek,
        season
      }
    });
    return response.data;
  },

  /**
   * Get advertisements with all targeting parameters.
   */
  getAdvertisementsWithTargeting: async (params: TargetingParams): Promise<Advertisement[]> => {
    const { userBioData, ...queryParams } = params;

    if (userBioData) {
      return advertisementService.getTargetedAdvertisements(params.countryCode, userBioData, params.mood);
    } else if (params.mood) {
      return advertisementService.getMoodTargetedAdvertisements(
        params.mood,
        params.intensity,
        params.timeOfDay,
        params.dayOfWeek,
        params.season
      );
    } else {
      return advertisementService.getGeoTargetedAdvertisements(
        params.countryCode,
        params.region,
        params.city,
        params.latitude,
        params.longitude
      );
    }
  }
};

export default advertisementService;
