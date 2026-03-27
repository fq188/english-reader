import { create } from 'zustand';

export interface Folder {
  id: string;
  name: string;
  parentId?: string;
  createdAt: Date;
}

interface LibraryState {
  folders: Folder[];
  selectedFolderId: string | null;
  searchQuery: string;
  sortBy: 'title' | 'lastReadAt' | 'createdAt';
  sortOrder: 'asc' | 'desc';
  
  addFolder: (folder: Folder) => void;
  removeFolder: (id: string) => void;
  setSelectedFolder: (id: string | null) => void;
  setSearchQuery: (query: string) => void;
  setSortBy: (sort: 'title' | 'lastReadAt' | 'createdAt') => void;
  setSortOrder: (order: 'asc' | 'desc') => void;
}

export const useLibraryStore = create<LibraryState>((set) => ({
  folders: [],
  selectedFolderId: null,
  searchQuery: '',
  sortBy: 'lastReadAt',
  sortOrder: 'desc',
  
  addFolder: (folder) => set((state) => ({
    folders: [...state.folders, folder]
  })),
  
  removeFolder: (id) => set((state) => ({
    folders: state.folders.filter(f => f.id !== id)
  })),
  
  setSelectedFolder: (id) => set({ selectedFolderId: id }),
  
  setSearchQuery: (query) => set({ searchQuery: query }),
  
  setSortBy: (sort) => set({ sortBy: sort }),
  
  setSortOrder: (order) => set({ sortOrder: order }),
}));
