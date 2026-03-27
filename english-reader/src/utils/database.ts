import Database from '@tauri-apps/plugin-sql';

let db: Database | null = null;

export async function getDatabase(): Promise<Database> {
  if (!db) {
    db = await Database.load('sqlite:english_reader.db');
    await initializeDatabase();
  }
  return db;
}

async function initializeDatabase(): Promise<void> {
  if (!db) return;

  await db.execute(`
    CREATE TABLE IF NOT EXISTS documents (
      id TEXT PRIMARY KEY,
      title TEXT NOT NULL,
      authors TEXT DEFAULT '[]',
      year INTEGER DEFAULT 0,
      journal TEXT DEFAULT '',
      file_path TEXT NOT NULL,
      file_hash TEXT NOT NULL,
      cover_image TEXT,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      last_read_at TEXT,
      last_page INTEGER DEFAULT 1,
      total_pages INTEGER DEFAULT 0,
      tags TEXT DEFAULT '[]',
      folder_id TEXT
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS folders (
      id TEXT PRIMARY KEY,
      name TEXT NOT NULL,
      parent_id TEXT,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS highlights (
      id TEXT PRIMARY KEY,
      document_id TEXT NOT NULL,
      page_number INTEGER NOT NULL,
      color TEXT NOT NULL,
      content TEXT NOT NULL,
      position_x REAL,
      position_y REAL,
      position_width REAL,
      position_height REAL,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS notes (
      id TEXT PRIMARY KEY,
      document_id TEXT NOT NULL,
      content TEXT NOT NULL,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS annotations (
      id TEXT PRIMARY KEY,
      document_id TEXT NOT NULL,
      page_number INTEGER NOT NULL,
      content TEXT NOT NULL,
      position_x REAL NOT NULL,
      position_y REAL NOT NULL,
      color TEXT DEFAULT '#FFEB3B',
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS vocabulary (
      id TEXT PRIMARY KEY,
      word TEXT NOT NULL,
      phonetic TEXT,
      definition TEXT NOT NULL,
      example_sentence TEXT,
      document_id TEXT,
      translation TEXT,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE SET NULL
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS settings (
      key TEXT PRIMARY KEY,
      value TEXT NOT NULL
    )
  `);

  await db.execute(`
    CREATE TABLE IF NOT EXISTS refs (
      id TEXT PRIMARY KEY,
      document_id TEXT NOT NULL,
      citation_key TEXT NOT NULL,
      full_text TEXT NOT NULL,
      FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
    )
  `);
}

export interface DBDocument {
  id: string;
  title: string;
  authors: string;
  year: number;
  journal: string;
  file_path: string;
  file_hash: string;
  cover_image: string | null;
  created_at: string;
  last_read_at: string | null;
  last_page: number;
  total_pages: number;
  tags: string;
  folder_id: string | null;
}

export interface DBFolder {
  id: string;
  name: string;
  parent_id: string | null;
  created_at: string;
}

export interface DBHighlight {
  id: string;
  document_id: string;
  page_number: number;
  color: string;
  content: string;
  position_x: number | null;
  position_y: number | null;
  position_width: number | null;
  position_height: number | null;
  created_at: string;
}

export interface DBNote {
  id: string;
  document_id: string;
  content: string;
  created_at: string;
  updated_at: string;
}

export interface DBVocabulary {
  id: string;
  word: string;
  phonetic: string | null;
  definition: string;
  example_sentence: string | null;
  document_id: string | null;
  translation: string | null;
  created_at: string;
}

export async function saveDocument(doc: DBDocument): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    `INSERT OR REPLACE INTO documents 
     (id, title, authors, year, journal, file_path, file_hash, cover_image, created_at, last_read_at, last_page, total_pages, tags, folder_id)
     VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14)`,
    [doc.id, doc.title, doc.authors, doc.year, doc.journal, doc.file_path, doc.file_hash, doc.cover_image, doc.created_at, doc.last_read_at, doc.last_page, doc.total_pages, doc.tags, doc.folder_id]
  );
}

export async function getDocuments(): Promise<DBDocument[]> {
  const database = await getDatabase();
  return await database.select<DBDocument[]>('SELECT * FROM documents ORDER BY last_read_at DESC');
}

export async function getDocument(id: string): Promise<DBDocument | null> {
  const database = await getDatabase();
  const results = await database.select<DBDocument[]>('SELECT * FROM documents WHERE id = $1', [id]);
  return results[0] || null;
}

export async function deleteDocument(id: string): Promise<void> {
  const database = await getDatabase();
  await database.execute('DELETE FROM documents WHERE id = $1', [id]);
}

export async function updateDocumentLastRead(id: string, lastPage: number): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    'UPDATE documents SET last_read_at = CURRENT_TIMESTAMP, last_page = $1 WHERE id = $2',
    [lastPage, id]
  );
}

export async function saveFolder(folder: DBFolder): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    'INSERT OR REPLACE INTO folders (id, name, parent_id, created_at) VALUES ($1, $2, $3, $4)',
    [folder.id, folder.name, folder.parent_id, folder.created_at]
  );
}

export async function getFolders(): Promise<DBFolder[]> {
  const database = await getDatabase();
  return await database.select<DBFolder[]>('SELECT * FROM folders ORDER BY name');
}

export async function deleteFolder(id: string): Promise<void> {
  const database = await getDatabase();
  await database.execute('DELETE FROM folders WHERE id = $1', [id]);
}

export async function saveVocabulary(item: DBVocabulary): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    'INSERT OR REPLACE INTO vocabulary (id, word, phonetic, definition, example_sentence, document_id, translation, created_at) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)',
    [item.id, item.word, item.phonetic, item.definition, item.example_sentence, item.document_id, item.translation, item.created_at]
  );
}

export async function getVocabulary(): Promise<DBVocabulary[]> {
  const database = await getDatabase();
  return await database.select<DBVocabulary[]>('SELECT * FROM vocabulary ORDER BY created_at DESC');
}

export async function deleteVocabulary(id: string): Promise<void> {
  const database = await getDatabase();
  await database.execute('DELETE FROM vocabulary WHERE id = $1', [id]);
}

export async function saveNote(note: DBNote): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    'INSERT OR REPLACE INTO notes (id, document_id, content, created_at, updated_at) VALUES ($1, $2, $3, $4, $5)',
    [note.id, note.document_id, note.content, note.created_at, note.updated_at]
  );
}

export async function getNote(documentId: string): Promise<DBNote | null> {
  const database = await getDatabase();
  const results = await database.select<DBNote[]>('SELECT * FROM notes WHERE document_id = $1', [documentId]);
  return results[0] || null;
}

export async function saveHighlight(highlight: DBHighlight): Promise<void> {
  const database = await getDatabase();
  await database.execute(
    'INSERT OR REPLACE INTO highlights (id, document_id, page_number, color, content, position_x, position_y, position_width, position_height, created_at) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)',
    [highlight.id, highlight.document_id, highlight.page_number, highlight.color, highlight.content, highlight.position_x, highlight.position_y, highlight.position_width, highlight.position_height, highlight.created_at]
  );
}

export async function getHighlights(documentId: string): Promise<DBHighlight[]> {
  const database = await getDatabase();
  return await database.select<DBHighlight[]>('SELECT * FROM highlights WHERE document_id = $1 ORDER BY page_number', [documentId]);
}

export async function deleteHighlight(id: string): Promise<void> {
  const database = await getDatabase();
  await database.execute('DELETE FROM highlights WHERE id = $1', [id]);
}
