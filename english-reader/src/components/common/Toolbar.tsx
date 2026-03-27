import React from 'react';

interface ToolbarProps {
  onOpenFile: () => void;
  onZoomIn: () => void;
  onZoomOut: () => void;
  zoom: number;
  showZoomControls?: boolean;
  showBackButton?: boolean;
  onBackToLibrary?: () => void;
}

export const Toolbar: React.FC<ToolbarProps> = ({
  onOpenFile,
  onZoomIn,
  onZoomOut,
  zoom,
  showZoomControls = true,
  showBackButton = false,
  onBackToLibrary,
}) => {
  return (
    <div className="flex items-center justify-between bg-white border-b border-gray-200 px-4 py-2">
      <div className="flex items-center gap-2">
        {showBackButton && (
          <button
            onClick={onBackToLibrary}
            className="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 rounded"
            title="返回书库"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
        )}
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
      
      {showZoomControls && (
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
      )}
    </div>
  );
};

export default Toolbar;
