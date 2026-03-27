import { create } from 'zustand';

export interface Document {
  id: string;
  title: string;
  authors: string[];
  year: number;
  journal: string;
  filePath: string;
  fileHash: string;
  coverImage?: string;
  createdAt: Date;
  lastReadAt?: Date;
  lastPage: number;
  totalPages: number;
  tags: string[];
  folderId?: string;
}

export interface Highlight {
  id: string;
  documentId: string;
  pageNumber: number;
  color: 'yellow' | 'green' | 'blue' | 'pink';
  content: string;
  rect: { x: number; y: number; width: number; height: number };
  createdAt: Date;
}

export interface Annotation {
  id: string;
  documentId: string;
  pageNumber: number;
  content: string;
  positionX: number;
  positionY: number;
  color: string;
  createdAt: Date;
}

interface DocumentState {
  documents: Document[];
  currentDocument: Document | null;
  openTabs: Document[];
  currentTabId: string | null;
  highlights: Highlight[];
  annotations: Annotation[];
  zoom: number;
  currentPage: number;
  
  setCurrentDocument: (doc: Document | null) => void;
  addDocument: (doc: Document) => void;
  removeDocument: (id: string) => void;
  openTab: (doc: Document) => void;
  closeTab: (id: string) => void;
  setCurrentTab: (id: string) => void;
  setZoom: (zoom: number) => void;
  setCurrentPage: (page: number) => void;
  addHighlight: (highlight: Highlight) => void;
  removeHighlight: (id: string) => void;
  addAnnotation: (annotation: Annotation) => void;
  updateAnnotation: (id: string, content: string) => void;
  removeAnnotation: (id: string) => void;
}

export const useDocumentStore = create<DocumentState>((set) => ({
  documents: [],
  currentDocument: null,
  openTabs: [],
  currentTabId: null,
  highlights: [],
  annotations: [],
  zoom: 100,
  currentPage: 1,
  
  setCurrentDocument: (doc) => set({ currentDocument: doc }),
  
  addDocument: (doc) => set((state) => ({
    documents: [...state.documents, doc]
  })),
  
  removeDocument: (id) => set((state) => ({
    documents: state.documents.filter(d => d.id !== id),
    openTabs: state.openTabs.filter(t => t.id !== id),
    currentTabId: state.currentTabId === id ? null : state.currentTabId,
    currentDocument: state.currentDocument?.id === id ? null : state.currentDocument,
  })),
  
  openTab: (doc) => set((state) => {
    const exists = state.openTabs.find(t => t.id === doc.id);
    if (exists) {
      return { currentTabId: doc.id };
    }
    const newTabs = [...state.openTabs, doc];
    if (newTabs.length > 10) {
      newTabs.shift();
    }
    return { openTabs: newTabs, currentTabId: doc.id };
  }),
  
  closeTab: (id) => set((state) => {
    const newTabs = state.openTabs.filter(t => t.id !== id);
    let newCurrentTabId = state.currentTabId;
    if (state.currentTabId === id) {
      const closedIndex = state.openTabs.findIndex(t => t.id === id);
      newCurrentTabId = newTabs[closedIndex]?.id || newTabs[closedIndex - 1]?.id || null;
    }
    return { openTabs: newTabs, currentTabId: newCurrentTabId };
  }),
  
  setCurrentTab: (id) => set({ currentTabId: id }),
  
  setZoom: (zoom) => set({ zoom: Math.max(50, Math.min(200, zoom)) }),
  
  setCurrentPage: (page) => set({ currentPage: page }),
  
  addHighlight: (highlight) => set((state) => ({
    highlights: [...state.highlights, highlight]
  })),
  
  removeHighlight: (id) => set((state) => ({
    highlights: state.highlights.filter(h => h.id !== id)
  })),
  
  addAnnotation: (annotation) => set((state) => ({
    annotations: [...state.annotations, annotation]
  })),
  
  updateAnnotation: (id, content) => set((state) => ({
    annotations: state.annotations.map(a => 
      a.id === id ? { ...a, content } : a
    )
  })),
  
  removeAnnotation: (id) => set((state) => ({
    annotations: state.annotations.filter(a => a.id !== id)
  })),
}));
