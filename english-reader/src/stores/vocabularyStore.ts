import { create } from 'zustand';

export interface VocabularyItem {
  id: string;
  word: string;
  phonetic?: string;
  definition: string;
  exampleSentence?: string;
  documentId?: string;
  translation?: string;
  createdAt: Date;
}

interface VocabularyState {
  items: VocabularyItem[];
  isExporting: boolean;
  
  addItem: (item: VocabularyItem) => void;
  removeItem: (id: string) => void;
  updateItem: (id: string, updates: Partial<VocabularyItem>) => void;
  setItems: (items: VocabularyItem[]) => void;
  setExporting: (isExporting: boolean) => void;
}

export const useVocabularyStore = create<VocabularyState>((set) => ({
  items: [],
  isExporting: false,
  
  addItem: (item) => set((state) => ({
    items: [...state.items, item]
  })),
  
  removeItem: (id) => set((state) => ({
    items: state.items.filter(i => i.id !== id)
  })),
  
  updateItem: (id, updates) => set((state) => ({
    items: state.items.map(i => i.id === id ? { ...i, ...updates } : i)
  })),
  
  setItems: (items) => set({ items }),
  
  setExporting: (isExporting) => set({ isExporting }),
}));
