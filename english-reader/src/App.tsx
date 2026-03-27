import React, { useState, useEffect, useCallback } from 'react';
import { open } from '@tauri-apps/plugin-dialog';
import { TabBar, Toolbar } from './components/common/Toolbar';
import PDFReader from './components/reader/PDFReader';
import { useDocumentStore, Document } from './stores/documentStore';
import { useVocabularyStore } from './stores/vocabularyStore';
import { useAISummaryStore } from './stores/aiStore';
import { generateId } from './utils/helpers';
import './index.css';

function App() {
  const [activeSidebarTab, setActiveSidebarTab] = useState<'info' | 'notes' | 'ai' | 'vocabulary'>('info');
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
        
        addDocument(newDoc);
        openTab(newDoc);
        setCurrentDocument(newDoc);
      }
    } catch (error) {
      console.error('Error opening file:', error);
    }
  }, [addDocument, openTab, setCurrentDocument]);
  
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
  }, [closeTab, currentTabId, openTabs, setCurrentDocument]);
  
  useEffect(() => {
    if (currentTabId && currentTab) {
      setCurrentDocument(currentTab);
    }
  }, [currentTabId, currentTab, setCurrentDocument]);
  
  return (
    <div className="h-screen flex flex-col">
      <Toolbar
        onOpenFile={handleOpenFile}
        onZoomIn={handleZoomIn}
        onZoomOut={handleZoomOut}
        zoom={zoom}
      />
      
      {openTabs.length > 0 && (
        <TabBar onCloseTab={handleCloseTab} />
      )}
      
      <div className="flex-1 flex overflow-hidden">
        <div className="flex-1 overflow-hidden">
          {currentTab ? (
            <PDFReader document={currentTab} />
          ) : (
            <div className="h-full flex flex-col items-center justify-center bg-gray-50">
              <div className="text-center">
                <svg
                  className="w-24 h-24 mx-auto text-gray-300 mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1}
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                  />
                </svg>
                <h2 className="text-xl font-semibold text-gray-600 mb-2">
                  欢迎使用 English Reader
                </h2>
                <p className="text-gray-500 mb-6">
                  打开 PDF 文件开始阅读英文文献
                </p>
                <button
                  onClick={handleOpenFile}
                  className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                >
                  打开文件
                </button>
              </div>
            </div>
          )}
        </div>
        
        <div className="w-80 bg-white border-l border-gray-200 flex flex-col">
          <Sidebar
            activeTab={activeSidebarTab}
            onTabChange={setActiveSidebarTab}
            vocabularyCount={vocabularyItems.length}
            currentDocument={currentDocument}
          />
        </div>
      </div>
    </div>
  );
}

interface SidebarProps {
  activeTab: 'info' | 'notes' | 'ai' | 'vocabulary';
  onTabChange: (tab: 'info' | 'notes' | 'ai' | 'vocabulary') => void;
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
        {activeTab === 'ai' && <AIPanel summary={summary} isGenerating={isGenerating} />}
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
  return (
    <div className="space-y-4">
      <h3 className="font-semibold">阅读笔记</h3>
      <textarea
        className="w-full h-64 p-3 border border-gray-200 rounded resize-none focus:outline-none focus:border-blue-500"
        placeholder="在此输入笔记，支持 Markdown 格式..."
      />
    </div>
  );
};

interface AIPanelProps {
  summary: string;
  isGenerating: boolean;
}

const AIPanel: React.FC<AIPanelProps> = ({ summary, isGenerating }) => {
  const error = useAISummaryStore((state) => state.error);
  
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold">AI 论文总结</h3>
        <button
          className="px-3 py-1 bg-blue-500 text-white text-sm rounded hover:bg-blue-600 disabled:opacity-50"
          disabled={isGenerating}
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
