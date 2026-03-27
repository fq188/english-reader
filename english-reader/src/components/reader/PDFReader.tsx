import React, { useEffect, useRef, useState } from 'react';
import * as pdfjsLib from 'pdfjs-dist';
import { useDocumentStore, Document } from '../../stores/documentStore';
import { TranslationPopup } from '../translation/TranslationPopup';

pdfjsLib.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.mjs`;

interface PDFReaderProps {
  document: Document;
}

export const PDFReader: React.FC<PDFReaderProps> = ({ document }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [pdfDoc, setPdfDoc] = useState<pdfjsLib.PDFDocumentProxy | null>(null);
  const [pageNum, setPageNum] = useState(document.lastPage || 1);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedText, setSelectedText] = useState<string>('');
  const [showTranslation, setShowTranslation] = useState(false);
  const [translationPosition, setTranslationPosition] = useState({ x: 0, y: 0 });
  const { zoom, setCurrentPage } = useDocumentStore();
  
  useEffect(() => {
    const loadPdf = async () => {
      try {
        const loadingTask = pdfjsLib.getDocument({
          url: document.filePath,
          cMapUrl: `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/cmaps/`,
          cMapPacked: true,
        });
        const pdf = await loadingTask.promise;
        setPdfDoc(pdf);
        setTotalPages(pdf.numPages);
      } catch (error) {
        console.error('Error loading PDF:', error);
      }
    };
    loadPdf();
  }, [document.filePath]);
  
  useEffect(() => {
    const renderPage = async () => {
      if (!pdfDoc || !canvasRef.current) return;
      
      const page = await pdfDoc.getPage(pageNum);
      const canvas = canvasRef.current;
      const context = canvas.getContext('2d');
      if (!context) return;
      
      const scale = 1.5 * (zoom / 100);
      const viewport = page.getViewport({ scale });
      
      canvas.height = viewport.height;
      canvas.width = viewport.width;
      
      await page.render({
        canvasContext: context,
        canvas: canvas,
        viewport: viewport,
      }).promise;
      
      setCurrentPage(pageNum);
    };
    
    renderPage();
  }, [pdfDoc, pageNum, zoom, setCurrentPage]);
  
  const handlePrevPage = () => {
    if (pageNum > 1) {
      setPageNum(pageNum - 1);
    }
  };
  
  const handleNextPage = () => {
    if (pageNum < totalPages) {
      setPageNum(pageNum + 1);
    }
  };
  
  const handleTextSelection = () => {
    const selection = window.getSelection();
    if (selection && selection.toString().trim()) {
      const text = selection.toString().trim();
      setSelectedText(text);
      
      const range = selection.getRangeAt(0);
      const rect = range.getBoundingClientRect();
      setTranslationPosition({
        x: rect.right,
        y: rect.bottom,
      });
      setShowTranslation(true);
    } else {
      setShowTranslation(false);
    }
  };
  
  const handleDoubleClick = (e: React.MouseEvent) => {
    const selection = window.getSelection();
    if (selection && selection.toString().trim()) {
      const text = selection.toString().trim();
      setSelectedText(text);
      const rect = (e.target as HTMLElement).getBoundingClientRect();
      setTranslationPosition({
        x: rect.right,
        y: rect.bottom,
      });
      setShowTranslation(true);
    }
  };
  
  return (
    <div className="flex flex-col h-full">
      <div
        ref={containerRef}
        className="flex-1 overflow-auto bg-gray-100 p-4"
        onMouseUp={handleTextSelection}
      >
        <div className="flex justify-center">
          <div className="pdf-page-container shadow-lg">
            <canvas ref={canvasRef} onDoubleClick={handleDoubleClick} />
          </div>
        </div>
      </div>
      
      <div className="flex items-center justify-center gap-4 bg-white border-t border-gray-200 py-3">
        <button
          onClick={handlePrevPage}
          disabled={pageNum <= 1}
          className="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded disabled:opacity-50"
        >
          上一页
        </button>
        <span className="text-sm">
          第 {pageNum} / {totalPages} 页
        </span>
        <button
          onClick={handleNextPage}
          disabled={pageNum >= totalPages}
          className="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded disabled:opacity-50"
        >
          下一页
        </button>
      </div>
      
      {showTranslation && selectedText && (
        <TranslationPopup
          text={selectedText}
          position={translationPosition}
          onClose={() => setShowTranslation(false)}
          documentId={document.id}
        />
      )}
    </div>
  );
};

export default PDFReader;
