import React, { useState, useEffect } from 'react';
import { listen } from '@tauri-apps/api/event';

interface DropZoneProps {
  onFileDrop: (filePath: string, fileName: string) => void;
  children: React.ReactNode;
}

export const DropZone: React.FC<DropZoneProps> = ({ onFileDrop, children }) => {
  const [isDragging, setIsDragging] = useState(false);

  useEffect(() => {
    const unlisten = listen<{ paths: string[] }>('tauri://drag-drop', async (event) => {
      setIsDragging(false);
      const paths = event.payload.paths;
      for (const path of paths) {
        const fileName = path.split(/[/\\]/).pop() || 'Untitled';
        const ext = fileName.split('.').pop()?.toLowerCase();
        
        if (ext === 'pdf' || ext === 'epub' || ext === 'docx') {
          onFileDrop(path, fileName);
        }
      }
    });

    const unlistenEnter = listen('tauri://drag-enter', () => {
      setIsDragging(true);
    });

    const unlistenLeave = listen('tauri://drag-leave', () => {
      setIsDragging(false);
    });

    return () => {
      unlisten.then(fn => fn());
      unlistenEnter.then(fn => fn());
      unlistenLeave.then(fn => fn());
    };
  }, [onFileDrop]);

  return (
    <div className="relative h-full w-full">
      {children}
      
      {isDragging && (
        <div className="absolute inset-0 bg-blue-500 bg-opacity-20 border-4 border-dashed border-blue-500 rounded-lg flex items-center justify-center z-50">
          <div className="text-center">
            <svg className="w-16 h-16 mx-auto text-blue-500 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <p className="text-blue-600 font-semibold text-lg">释放文件以导入</p>
            <p className="text-blue-500 text-sm">支持 PDF、EPUB、DOCX 格式</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default DropZone;
