// Centralized backend API configuration for DevFlow AI
const getApiBaseUrl = (): string => {
  if (typeof window !== 'undefined') {
    const origin = window.location.origin;
    if (origin.includes('localhost:4300')) {
      return 'http://localhost:9091';
    }
    return origin;
  }
  return 'http://localhost:9091';
};

export const API_BASE_URL = getApiBaseUrl();
