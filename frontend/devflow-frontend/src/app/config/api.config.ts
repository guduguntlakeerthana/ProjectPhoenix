// Centralized backend API configuration for DevFlow AI
const getApiBaseUrl = (): string => {
  if (typeof window !== 'undefined') {
    const origin = window.location.origin;
    
    // Local development: frontend on port 4200 maps to backend on port 9091
    if (origin.includes('localhost:4200')) {
      return 'http://localhost:9091';
    }
    
    // In production, check if a global runtime API URL is injected via window configuration
    const win = window as any;
    if (win.DEVFLOW_API_URL) {
      return win.DEVFLOW_API_URL;
    }
    
    return origin;
  }
  return 'http://localhost:9091';
};

export const API_BASE_URL = getApiBaseUrl();
