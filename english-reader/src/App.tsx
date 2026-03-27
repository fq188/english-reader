import React, { useState, useEffect, useCallback } from 'react';
import { open } from '@tauri-apps/plugin-dialog';
import { TabBar } from './components/common/TabBar';
import { Toolbar } from './components/common/Toolbar';
import PDFReader from './components/reader/PDFReader';
import LibraryPanel from './components/library/LibraryPanel';
import DropZone from './components/library/DropZone';
import { useDocumentStore, Document } from './stores/documentStore';
import { useVocabularyStore } from './stores/vocabularyStore';
import { useAISummaryStore } from './stores/aiStore';
import { generateId } from './utils/helpers';
import { saveDocument } from './utils/database';
import './index.css';

type ViewMode = 'reader' | 'library';
type SidebarTab = 'info' | 'notes' | 'ai' | 'vocabulary';

function App() {
  const [viewMode, setViewMode] = useState<ViewMode>('library');
  const [activeSidebarTab, setActiveSidebarTab] = useState<SidebarTab>('info');
  const {
    currentDocument,
    openTabs,
    currentTabId,
    addDocument,
    openTab,
    closeTab,
    setZoom,
    zoom,
    setCurrentDocument,
  } = useDocumentStore();
  
  const vocabularyItems = useVocabularyStore((state) => state.items);
  const currentTab = openTabs.find(t => t.id === currentTabId);
  
  const handleOpenFile = useCallback(async () => {
    try {
      const selected = await open({
        multiple: false,
        filters: [{
          name: 'PDF Documents',
          extensions: ['pdf', 'epub', 'docx'],
        }],
      });
      
      if (selected) {
        const filePath = selected as string;
        const fileName = filePath.split(/[/\\]/).pop() || 'Untitled';
        
        const newDoc: Document = {
          id: generateId(),
          title: fileName.replace(/\.[^/.]+$/, ''),
          authors: [],
          year: 0,
          journal: '',
          filePath: filePath,
          fileHash: '',
          createdAt: new Date(),
          lastPage: 1,
          totalPages: 0,
          tags: [],
        };
        
        await saveDocument({
          id: newDoc.id,
          title: newDoc.title,
          authors: JSON.stringify(newDoc.authors),
          year: newDoc.year,
          journal: newDoc.journal,
          file_path: newDoc.filePath,
          file_hash: newDoc.fileHash,
          cover_image: newDoc.coverImage || null,
          created_at: newDoc.createdAt.toISOString(),
          last_read_at: null,
          last_page: newDoc.lastPage,
          total_pages: newDoc.totalPages,
          tags: JSON.stringify(newDoc.tags),
          folder_id: newDoc.folderId || null,
        });
        
        addDocument(newDoc);
        openTab(newDoc);
        setCurrentDocument(newDoc);
        setViewMode('reader');
      }
    } catch (error) {
      console.error('Error opening file:', error);
    }
  }, [addDocument, openTab, setCurrentDocument]);
  
  const handleFileDrop = useCallback(async (filePath: string, fileName: string) => {
    const newDoc: Document = {
      id: generateId(),
      title: fileName.replace(/\.[^/.]+$/, ''),
      authors: [],
      year: 0,
      journal: '',
      filePath: filePath,
      fileHash: '',
      createdAt: new Date(),
      lastPage: 1,
      totalPages: 0,
      tags: [],
    };
    
    try {
      await saveDocument({
        id: newDoc.id,
        title: newDoc.title,
        authors: JSON.stringify(newDoc.authors),
        year: newDoc.year,
        journal: newDoc.journal,
        file_path: newDoc.filePath,
        file_hash: newDoc.fileHash,
        cover_image: newDoc.coverImage || null,
        created_at: newDoc.createdAt.toISOString(),
        last_read_at: null,
        last_page: newDoc.lastPage,
        total_pages: newDoc.totalPages,
        tags: JSON.stringify(newDoc.tags),
        folder_id: newDoc.folderId || null,
      });
      
      addDocument(newDoc);
      openTab(newDoc);
      setCurrentDocument(newDoc);
      setViewMode('reader');
    } catch (error) {
      console.error('Failed to import document:', error);
    }
  }, [addDocument, openTab, setCurrentDocument]);
  
  const handleOpenDocument = useCallback((doc: Document) => {
    openTab(doc);
    setCurrentDocument(doc);
    setViewMode('reader');
  }, [openTab, setCurrentDocument]);
  
  const handleZoomIn = useCallback(() => {
    setZoom(zoom + 10);
  }, [setZoom, zoom]);
  
  const handleZoomOut = useCallback(() => {
    setZoom(zoom - 10);
  }, [setZoom, zoom]);
  
  const handleCloseTab = useCallback((id: string) => {
    closeTab(id);
    if (currentTabId === id) {
      const remainingTabs = openTabs.filter(t => t.id !== id);
      setCurrentDocument(remainingTabs[remainingTabs.length - 1] || null);
    }
    if (openTabs.length <= 1) {
      setViewMode('library');
    }
  }, [closeTab, currentTabId, openTabs, setCurrentDocument]);
  
  useEffect(() => {
    if (currentTabId && currentTab) {
      setCurrentDocument(currentTab);
    }
  }, [currentTabId, currentTab, setCurrentDocument]);
  
  return (
    <DropZone onFileDrop={handleFileDrop}>
      <div className="h-screen flex flex-col">
        <Toolbar
          onOpenFile={handleOpenFile}
          onZoomIn={handleZoomIn}
          onZoomOut={handleZoomOut}
          zoom={zoom}
          showZoomControls={viewMode === 'reader'}
          onBackToLibrary={() => setViewMode('library')}
          showBackButton={viewMode === 'reader'}
        />
        
        {viewMode === 'reader' && openTabs.length > 0 && (
          <TabBar onCloseTab={handleCloseTab} />
        )}
        
        <div className="flex-1 flex overflow-hidden">
          <div className="flex-1 overflow-hidden">
            {viewMode === 'reader' && currentTab ? (
              <PDFReader document={currentTab} />
            ) : (
              <div className="h-full bg-white">
                <LibraryPanel onOpenDocument={handleOpenDocument} />
              </div>
            )}
          </div>
          
          {viewMode === 'reader' && (
            <div className="w-80 bg-white border-l border-gray-200 flex flex-col">
              <Sidebar
                activeTab={activeSidebarTab}
                onTabChange={setActiveSidebarTab}
                vocabularyCount={vocabularyItems.length}
                currentDocument={currentDocument}
              />
            </div>
          )}
        </div>
      </div>
    </DropZone>
  );
}

interface SidebarProps {
  activeTab: SidebarTab;
  onTabChange: (tab: SidebarTab) => void;
  vocabularyCount: number;
  currentDocument: Document | null;
}

const Sidebar: React.FC<SidebarProps> = ({ activeTab, onTabChange, vocabularyCount, currentDocument }) => {
  const items = useVocabularyStore((state) => state.items);
  const removeItem = useVocabularyStore((state) => state.removeItem);
  const summary = useAISummaryStore((state) => state.summary);
  const isGenerating = useAISummaryStore((state) => state.isGenerating);
  
  const tabs = [
    { id: 'info' as const, label: '文献信息' },
    { id: 'notes' as const, label: '笔记' },
    { id: 'ai' as const, label: 'AI 总结' },
    { id: 'vocabulary' as const, label: '生词本', count: vocabularyCount },
  ];
  
  return (
    <>
      <div className="flex border-b border-gray-200">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={`flex-1 px-2 py-3 text-sm font-medium border-b-2 transition-colors ${
              activeTab === tab.id
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-600 hover:text-gray-800'
            }`}
          >
            {tab.label}
            {tab.count !== undefined && tab.count > 0 && (
              <span className="ml-1 px-1.5 py-0.5 text-xs bg-gray-100 rounded-full">
                {tab.count}
              </span>
            )}
          </button>
        ))}
      </div>
      
      <div className="flex-1 overflow-y-auto p-4">
        {activeTab === 'info' && <DocumentInfoPanel document={currentDocument} />}
        {activeTab === 'notes' && <NotesPanel />}
        {activeTab === 'ai' && <AIPanel summary={summary} isGenerating={isGenerating} documentId={currentDocument?.id} />}
        {activeTab === 'vocabulary' && <VocabularyPanel items={items} onRemove={removeItem} />}
      </div>
    </>
  );
};

const DocumentInfoPanel: React.FC<{ document: Document | null }> = ({ document }) => {
  if (!document) {
    return (
      <div className="text-center text-gray-500 py-8">
        暂未打开文档
      </div>
    );
  }
  
  return (
    <div className="space-y-4">
      <h3 className="font-semibold text-lg">{document.title}</h3>
      <div className="space-y-2 text-sm">
        <p>
          <span className="text-gray-600">作者：</span>
          <span>{document.authors.join(', ') || '未知'}</span>
        </p>
        <p>
          <span className="text-gray-600">年份：</span>
          <span>{document.year || '未知'}</span>
        </p>
        <p>
          <span className="text-gray-600">期刊：</span>
          <span>{document.journal || '未知'}</span>
        </p>
        <p>
          <span className="text-gray-600">页数：</span>
          <span>{document.totalPages || '未知'}</span>
        </p>
      </div>
    </div>
  );
};

const NotesPanel: React.FC = () => {
  const [content, setContent] = useState('');
  
  return (
    <div className="space-y-4">
      <h3 className="font-semibold">阅读笔记</h3>
      <textarea
        className="w-full h-64 p-3 border border-gray-200 rounded resize-none focus:outline-none focus:border-blue-500"
        placeholder="在此输入笔记，支持 Markdown 格式..."
        value={content}
        onChange={(e) => setContent(e.target.value)}
      />
    </div>
  );
};

interface AIPanelProps {
  summary: string;
  isGenerating: boolean;
  documentId?: string;
}

const AIPanel: React.FC<AIPanelProps> = ({ summary, isGenerating, documentId }) => {
  const error = useAISummaryStore((state) => state.error);
  
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold">AI 论文总结</h3>
        <button
          className="px-3 py-1 bg-blue-500 text-white text-sm rounded hover:bg-blue-600 disabled:opacity-50"
          disabled={isGenerating || !documentId}
          title={!documentId ? '请先打开文档' : ''}
        >
          {isGenerating ? '生成中...' : '生成总结'}
        </button>
      </div>
      
      {error && (
        <div className="p-3 bg-red-50 text-red-600 text-sm rounded">
          {error}
        </div>
      )}
      
      {summary ? (
        <div className="prose prose-sm max-w-none">
          {summary}
        </div>
      ) : (
        <div className="text-center text-gray-500 py-8">
          点击"生成总结"获取论文摘要
        </div>
      )}
    </div>
  );
};

interface VocabularyItem {
  id: string;
  word: string;
  phonetic?: string;
  definition: string;
  exampleSentence?: string;
  documentId?: string;
  translation?: string;
}

interface VocabularyPanelProps {
  items: VocabularyItem[];
  onRemove: (id: string) => void;
}

const VocabularyPanel: React.FC<VocabularyPanelProps> = ({ items, onRemove }) => {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold">生词本</h3>
        <button className="px-3 py-1 text-sm text-blue-500 hover:underline">
          导出
        </button>
      </div>
      
      {items.length === 0 ? (
        <div className="text-center text-gray-500 py-8">
          暂无生词，选中文本后点击"加入生词本"
        </div>
      ) : (
        <div className="space-y-2">
          {items.map((item) => (
            <div
              key={item.id}
              className="p-3 bg-gray-50 rounded-lg group"
            >
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <p className="font-medium">{item.word}</p>
                  {item.phonetic && (
                    <p className="text-sm text-gray-500">{item.phonetic}</p>
                  )}
                  <p className="text-sm mt-1">{item.definition}</p>
                </div>
                <button
                  onClick={() => onRemove(item.id)}
                  className="p-1 text-gray-400 hover:text-red-500 opacity-0 group-hover:opacity-100"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default App;
