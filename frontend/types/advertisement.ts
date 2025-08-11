/**
 * Enum representing the source of an advertisement.
 */
export enum AdvertisementSource {
  STORAGE = 'STORAGE',
  YOUTUBE = 'YOUTUBE'
}

/**
 * Interface for geolocation targeting criteria.
 */
export interface GeoTarget {
  countryCode: string;
  region?: string;
  city?: string;
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  include: boolean;
}

/**
 * Enum representing gender options for targeting.
 */
export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
  ALL = 'ALL'
}

/**
 * Interface for biographical targeting criteria.
 */
export interface BioTarget {
  minAge?: number;
  maxAge?: number;
  gender?: Gender;
  occupation?: string;
  educationLevel?: string;
  language?: string;
  interestCategory?: string;
  include: boolean;
}

/**
 * Enum representing different mood states for targeting.
 */
export enum Mood {
  HAPPY = 'HAPPY',
  SAD = 'SAD',
  EXCITED = 'EXCITED',
  RELAXED = 'RELAXED',
  STRESSED = 'STRESSED',
  BORED = 'BORED',
  FOCUSED = 'FOCUSED',
  TIRED = 'TIRED',
  ENERGETIC = 'ENERGETIC',
  NEUTRAL = 'NEUTRAL'
}

/**
 * Interface for mood-based targeting criteria.
 */
export interface MoodTarget {
  mood: Mood;
  intensityMin?: number;
  intensityMax?: number;
  timeOfDay?: string;
  dayOfWeek?: string;
  season?: string;
  include: boolean;
}

/**
 * Interface representing an Advertisement.
 */
export interface YouTubeDetails {
  videoId?: string;
  videoTitle?: string;
  channelId?: string;
  channelTitle?: string;
  durationSeconds?: number;
  thumbnailUrl?: string;
  publishedAt?: string;
}

export interface Advertisement {
  id?: number;
  title: string;
  description?: string;
  content: string;
  source: AdvertisementSource;
  sourceIdentifier: string;
  createdAt?: string;
  updatedAt?: string;
  active: boolean;
  // Click behavior
  targetUrl?: string;
  clickable?: boolean;
  youtubeDetails?: YouTubeDetails;
  geoTargets?: GeoTarget[];
  bioTargets?: BioTarget[];
  moodTargets?: MoodTarget[];
}

/**
 * Interface for user biographical data.
 */
export interface UserBioData {
  age?: number;
  gender?: string;
  occupation?: string;
  educationLevel?: string;
  language?: string;
  interests?: string[];
}

/**
 * Interface for targeting request parameters.
 */
export interface TargetingParams {
  countryCode?: string;
  region?: string;
  city?: string;
  latitude?: number;
  longitude?: number;
  userBioData?: UserBioData;
  mood?: Mood;
  intensity?: number;
  timeOfDay?: string;
  dayOfWeek?: string;
  season?: string;
}