import { create } from 'zustand';

interface AISummaryState {
  summary: string;
  isGenerating: boolean;
  error: string | null;
  streamingContent: string;
  
  setSummary: (summary: string) => void;
  setGenerating: (isGenerating: boolean) => void;
  setError: (error: string | null) => void;
  setStreamingContent: (content: string) => void;
  clearSummary: () => void;
}

export const useAISummaryStore = create<AISummaryState>((set) => ({
  summary: '',
  isGenerating: false,
  error: null,
  streamingContent: '',
  
  setSummary: (summary) => set({ summary }),
  setGenerating: (isGenerating) => set({ isGenerating }),
  setError: (error) => set({ error }),
  setStreamingContent: (content) => set({ streamingContent: content }),
  clearSummary: () => set({ summary: '', streamingContent: '', error: null }),
}));
