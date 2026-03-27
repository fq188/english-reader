import React from 'react';
import { useDocumentStore } from '../../stores/documentStore';

interface TabBarProps {
  onCloseTab: (id: string) => void;
}

export const TabBar: React.FC<TabBarProps> = ({ onCloseTab }) => {
  const { openTabs, currentTabId, setCurrentTab } = useDocumentStore();
  
  return (
    <div className="flex items-center bg-gray-100 border-b border-gray-200 overflow-x-auto">
      {openTabs.map((tab) => (
        <div
          key={tab.id}
          className={`flex items-center px-4 py-2 cursor-pointer border-r border-gray-200 min-w-[120px] max-w-[200px] ${
            currentTabId === tab.id
              ? 'bg-white border-b-2 border-b-blue-500'
              : 'hover:bg-gray-50'
          }`}
          onClick={() => setCurrentTab(tab.id)}
        >
          <span className="truncate flex-1 text-sm">{tab.title}</span>
          <button
            className="ml-2 p-1 hover:bg-gray-200 rounded text-gray-500"
            onClick={(e) => {
              e.stopPropagation();
              onCloseTab(tab.id);
            }}
          >
            <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      ))}
    </div>
  );
};

interface ToolbarProps {
  onOpenFile: () => void;
  onZoomIn: () => void;
  onZoomOut: () => void;
  zoom: number;
}

export const Toolbar: React.FC<ToolbarProps> = ({
  onOpenFile,
  onZoomIn,
  onZoomOut,
  zoom,
}) => {
  return (
    <div className="flex items-center justify-between bg-white border-b border-gray-200 px-4 py-2">
      <div className="flex items-center gap-2">
        <button
          onClick={onOpenFile}
          className="flex items-center gap-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 19a2 2 0 01-2-2V7a2 2 0 012-2h4l2 2h4a2 2 0 012 2v1M5 19h14a2 2 0 002-2v-5a2 2 0 00-2-2H9a2 2 0 00-2 2v5a2 2 0 01-2 2z" />
          </svg>
          打开文件
        </button>
      </div>
      
      <div className="flex items-center gap-2">
        <button
          onClick={onZoomOut}
          className="p-2 hover:bg-gray-100 rounded"
          title="缩小"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
          </svg>
        </button>
        <span className="text-sm min-w-[60px] text-center">{zoom}%</span>
        <button
          onClick={onZoomIn}
          className="p-2 hover:bg-gray-100 rounded"
          title="放大"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
        </button>
      </div>
    </div>
  );
};

export default Toolbar;
