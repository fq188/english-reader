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

export interface Note {
  id: string;
  documentId: string;
  content: string;
  createdAt: Date;
  updatedAt: Date;
}

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

export interface Folder {
  id: string;
  name: string;
  parentId?: string;
  createdAt: Date;
}

export interface TranslationResult {
  word: string;
  phonetic?: string;
  definitions: string[];
  examples?: string[];
}

export interface Reference {
  id: string;
  documentId: string;
  citationKey: string;
  fullText: string;
}
