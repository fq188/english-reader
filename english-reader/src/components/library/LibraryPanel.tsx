import React, { useState, useEffect } from 'react';
import { useLibraryStore, Folder } from '../../stores/libraryStore';
import { Document } from '../../stores/documentStore';
import { getDocuments, getFolders, saveFolder, deleteDocument, deleteFolder } from '../../utils/database';
import { generateId } from '../../utils/helpers';

interface LibraryPanelProps {
  onOpenDocument: (doc: Document) => void;
}

export const LibraryPanel: React.FC<LibraryPanelProps> = ({ onOpenDocument }) => {
  const { folders, searchQuery, sortBy, sortOrder, addFolder, removeFolder, setSearchQuery, setSortBy, setSortOrder } = useLibraryStore();
  const [documents, setDocuments] = useState<Document[]>([]);
  const [selectedFolderId, setSelectedFolderId] = useState<string | null>(null);
  const [showNewFolderInput, setShowNewFolderInput] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const dbDocs = await getDocuments();
      const dbFolders = await getFolders();
      
      const docs = dbDocs.map(doc => ({
        id: doc.id,
        title: doc.title,
        authors: JSON.parse(doc.authors || '[]'),
        year: doc.year,
        journal: doc.journal,
        filePath: doc.file_path,
        fileHash: doc.file_hash,
        coverImage: doc.cover_image || undefined,
        createdAt: new Date(doc.created_at),
        lastReadAt: doc.last_read_at ? new Date(doc.last_read_at) : undefined,
        lastPage: doc.last_page,
        totalPages: doc.total_pages,
        tags: JSON.parse(doc.tags || '[]'),
        folderId: doc.folder_id || undefined,
      }));
      
      setDocuments(docs);
      
      const loadedFolders = dbFolders.map(f => ({
        id: f.id,
        name: f.name,
        parentId: f.parent_id || undefined,
        createdAt: new Date(f.created_at),
      }));
      
      useLibraryStore.setState({ folders: loadedFolders });
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateFolder = async () => {
    if (!newFolderName.trim()) return;
    
    const newFolder: Folder = {
      id: generateId(),
      name: newFolderName.trim(),
      createdAt: new Date(),
    };
    
    await saveFolder({
      id: newFolder.id,
      name: newFolder.name,
      parent_id: newFolder.parentId || null,
      created_at: newFolder.createdAt.toISOString(),
    });
    
    addFolder(newFolder);
    setNewFolderName('');
    setShowNewFolderInput(false);
  };

  const handleDeleteFolder = async (id: string) => {
    await deleteFolder(id);
    removeFolder(id);
  };

  const handleDeleteDocument = async (id: string) => {
    await deleteDocument(id);
    setDocuments(prev => prev.filter(d => d.id !== id));
  };

  const filteredDocuments = documents
    .filter(doc => {
      if (selectedFolderId && doc.folderId !== selectedFolderId) return false;
      if (searchQuery) {
        const query = searchQuery.toLowerCase();
        return doc.title.toLowerCase().includes(query) || 
               doc.authors.some(a => a.toLowerCase().includes(query));
      }
      return true;
    })
    .sort((a, b) => {
      let comparison = 0;
      switch (sortBy) {
        case 'title':
          comparison = a.title.localeCompare(b.title);
          break;
        case 'lastReadAt':
          comparison = (a.lastReadAt?.getTime() || 0) - (b.lastReadAt?.getTime() || 0);
          break;
        case 'createdAt':
          comparison = a.createdAt.getTime() - b.createdAt.getTime();
          break;
      }
      return sortOrder === 'asc' ? comparison : -comparison;
    });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-gray-500">加载中...</div>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col">
      <div className="p-4 border-b border-gray-200">
        <h2 className="font-semibold text-lg mb-3">书库</h2>
        
        <div className="mb-3">
          <input
            type="text"
            placeholder="搜索文献..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full px-3 py-2 border border-gray-200 rounded focus:outline-none focus:border-blue-500"
          />
        </div>
        
        <div className="flex items-center gap-2 mb-3">
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as 'title' | 'lastReadAt' | 'createdAt')}
            className="flex-1 px-2 py-1 border border-gray-200 rounded text-sm"
          >
            <option value="lastReadAt">最近阅读</option>
            <option value="title">标题</option>
            <option value="createdAt">添加时间</option>
          </select>
          <button
            onClick={() => setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc')}
            className="p-2 hover:bg-gray-100 rounded"
            title={sortOrder === 'asc' ? '升序' : '降序'}
          >
            {sortOrder === 'asc' ? '↑' : '↓'}
          </button>
        </div>
        
        <div className="flex items-center gap-2">
          {showNewFolderInput ? (
            <div className="flex-1 flex gap-1">
              <input
                type="text"
                value={newFolderName}
                onChange={(e) => setNewFolderName(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleCreateFolder()}
                placeholder="文件夹名称"
                className="flex-1 px-2 py-1 border border-gray-200 rounded text-sm"
                autoFocus
              />
              <button onClick={handleCreateFolder} className="px-2 py-1 bg-blue-500 text-white rounded text-sm">创建</button>
              <button onClick={() => setShowNewFolderInput(false)} className="px-2 py-1 bg-gray-200 rounded text-sm">取消</button>
            </div>
          ) : (
            <button
              onClick={() => setShowNewFolderInput(true)}
              className="flex items-center gap-1 px-3 py-2 bg-gray-100 hover:bg-gray-200 rounded text-sm"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              新建文件夹
            </button>
          )}
        </div>
      </div>
      
      <div className="flex-1 overflow-y-auto">
        {folders.length > 0 && (
          <div className="p-2 border-b border-gray-100">
            <div className="flex items-center gap-1 mb-2">
              <button
                onClick={() => setSelectedFolderId(null)}
                className={`px-2 py-1 rounded text-sm ${!selectedFolderId ? 'bg-blue-100 text-blue-600' : 'hover:bg-gray-100'}`}
              >
                全部
              </button>
              {folders.map(folder => (
                <div key={folder.id} className="flex items-center gap-1">
                  <button
                    onClick={() => setSelectedFolderId(folder.id)}
                    className={`px-2 py-1 rounded text-sm ${selectedFolderId === folder.id ? 'bg-blue-100 text-blue-600' : 'hover:bg-gray-100'}`}
                  >
                    {folder.name}
                  </button>
                  <button
                    onClick={() => handleDeleteFolder(folder.id)}
                    className="p-1 text-gray-400 hover:text-red-500"
                  >
                    <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
        
        <div className="p-2">
          {filteredDocuments.length === 0 ? (
            <div className="text-center text-gray-500 py-8">
              {searchQuery ? '未找到匹配的文献' : '暂无文献，拖拽 PDF 文件到窗口导入'}
            </div>
          ) : (
            <div className="space-y-2">
              {filteredDocuments.map(doc => (
                <div
                  key={doc.id}
                  className="p-3 bg-gray-50 hover:bg-gray-100 rounded-lg cursor-pointer group"
                  onClick={() => onOpenDocument(doc)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1 min-w-0">
                      <p className="font-medium truncate">{doc.title}</p>
                      <p className="text-sm text-gray-500 truncate">
                        {doc.authors.length > 0 ? doc.authors.join(', ') : '未知作者'}
                      </p>
                      <p className="text-xs text-gray-400 mt-1">
                        {doc.year > 0 && `${doc.year} · `}{doc.totalPages > 0 ? `${doc.totalPages} 页` : ''}
                      </p>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteDocument(doc.id);
                      }}
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
      </div>
    </div>
  );
};

export default LibraryPanel;
