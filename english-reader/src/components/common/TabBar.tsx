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

export default TabBar;
