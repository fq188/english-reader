import React, { useState, useEffect, useRef } from 'react';
import { useVocabularyStore, VocabularyItem } from '../../stores/vocabularyStore';
import { generateId } from '../../utils/helpers';

interface TranslationPopupProps {
  text: string;
  position: { x: number; y: number };
  onClose: () => void;
  documentId?: string;
}

export const TranslationPopup: React.FC<TranslationPopupProps> = ({
  text,
  position,
  onClose,
  documentId,
}) => {
  const [translation, setTranslation] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const popupRef = useRef<HTMLDivElement>(null);
  const { addItem } = useVocabularyStore();
  
  useEffect(() => {
    const translate = async () => {
      if (!text || text.length === 0) return;
      
      setLoading(true);
      setError(null);
      
      try {
        const response = await fetch(
          `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=zh-CN&dt=t&q=${encodeURIComponent(text)}`
        );
        
        if (!response.ok) {
          throw new Error('Translation service unavailable');
        }
        
        const data = await response.json();
        
        if (data && data[0]) {
          const translatedText = data[0].map((item: any) => item[0]).join('');
          setTranslation(translatedText);
        } else {
          setTranslation('未找到翻译结果');
        }
      } catch (err) {
        setError('翻译服务暂时不可用，请检查网络连接');
        setTranslation('');
      } finally {
        setLoading(false);
      }
    };
    
    translate();
  }, [text]);
  
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (popupRef.current && !popupRef.current.contains(e.target as Node)) {
        onClose();
      }
    };
    
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClose]);
  
  const handleAddToVocabulary = () => {
    const item: VocabularyItem = {
      id: generateId(),
      word: text,
      definition: translation,
      documentId,
      createdAt: new Date(),
    };
    addItem(item);
    onClose();
  };
  
  return (
    <div
      ref={popupRef}
      className="fixed z-50 bg-white rounded-lg shadow-xl border border-gray-200 w-80 max-h-96 overflow-hidden"
      style={{
        left: Math.min(position.x, window.innerWidth - 320),
        top: Math.min(position.y + 8, window.innerHeight - 400),
      }}
    >
      <div className="p-3 bg-gray-50 border-b border-gray-200">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <p className="font-medium text-gray-800">{text}</p>
            {loading && (
              <p className="text-sm text-gray-500 mt-1">翻译中...</p>
            )}
            {error && (
              <p className="text-sm text-red-500 mt-1">{error}</p>
            )}
            {translation && !loading && (
              <p className="text-sm text-gray-600 mt-1">{translation}</p>
            )}
          </div>
          <button
            onClick={onClose}
            className="p-1 hover:bg-gray-200 rounded text-gray-500"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
      
      <div className="p-2 border-t border-gray-100">
        <button
          onClick={handleAddToVocabulary}
          className="w-full px-3 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded flex items-center gap-2"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          加入生词本
        </button>
      </div>
    </div>
  );
};

export default TranslationPopup;
