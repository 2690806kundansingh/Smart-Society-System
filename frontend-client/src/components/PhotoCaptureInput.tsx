import React, { useState, useRef } from 'react';
import { Camera, Upload, Link as LinkIcon, X, Check, RefreshCw } from 'lucide-react';

interface PhotoCaptureInputProps {
  value: string;
  onChange: (photoUrl: string) => void;
  label?: string;
}

export const PhotoCaptureInput: React.FC<PhotoCaptureInputProps> = ({
  value,
  onChange,
  label = 'Attach Photo (Direct Camera or File Upload)',
}) => {
  const [activeTab, setActiveTab] = useState<'CAMERA' | 'FILE' | 'URL'>('FILE');
  const [isCameraActive, setIsCameraActive] = useState(false);
  const [cameraError, setCameraError] = useState<string | null>(null);

  const videoRef = useRef<HTMLVideoElement | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const cameraFileInputRef = useRef<HTMLInputElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);

  // Handle File Upload from Gallery / Device Files
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        if (typeof reader.result === 'string') {
          onChange(reader.result);
        }
      };
      reader.readAsDataURL(file);
    }
  };

  // Start Live Webcam Stream (for Desktop/Web)
  const startCamera = async () => {
    setCameraError(null);
    setIsCameraActive(true);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment', width: { ideal: 1280 }, height: { ideal: 720 } },
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
      }
    } catch (err: any) {
      console.warn('Live camera stream not available, falling back to native file picker', err);
      setCameraError('Webcam access unavailable. Triggering native camera capture...');
      stopCamera();
      // Fallback to native mobile camera capture input
      if (cameraFileInputRef.current) {
        cameraFileInputRef.current.click();
      }
    }
  };

  // Stop Webcam Stream
  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
    setIsCameraActive(false);
  };

  // Capture Photo Frame from Live Video
  const capturePhoto = () => {
    if (!videoRef.current) return;
    const canvas = document.createElement('canvas');
    canvas.width = videoRef.current.videoWidth || 640;
    canvas.height = videoRef.current.videoHeight || 480;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
      const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
      onChange(dataUrl);
      stopCamera();
    }
  };

  // Sample Preset Photo
  const setSamplePhoto = () => {
    onChange('https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=600&q=80');
  };

  return (
    <div className="space-y-2">
      <label className="block text-xs font-semibold text-slate-700">{label}</label>

      {/* Preview Container if Photo Exists */}
      {value ? (
        <div className="relative rounded-xl border border-slate-200 overflow-hidden bg-slate-50 p-2 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <img
              src={value}
              alt="Uploaded Preview"
              className="w-14 h-14 object-cover rounded-lg border border-slate-200 shadow-xs"
            />
            <div>
              <div className="flex items-center space-x-1 text-emerald-700 text-xs font-bold">
                <Check className="w-3.5 h-3.5" />
                <span>Photo Attached</span>
              </div>
              <p className="text-[11px] text-slate-500 truncate max-w-[200px] sm:max-w-[280px]">
                {value.startsWith('data:') ? 'Image from Camera / Gallery' : value}
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={() => onChange('')}
            className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
            title="Remove Photo"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      ) : (
        <div className="border border-slate-200 rounded-xl overflow-hidden bg-white shadow-xs">
          {/* Option Selector Tabs */}
          <div className="flex bg-slate-100 p-1 border-b border-slate-200">
            <button
              type="button"
              onClick={() => {
                setActiveTab('FILE');
                stopCamera();
              }}
              className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                activeTab === 'FILE'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              <Upload className="w-3.5 h-3.5" />
              <span>Gallery / File</span>
            </button>

            <button
              type="button"
              onClick={() => {
                setActiveTab('CAMERA');
                startCamera();
              }}
              className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                activeTab === 'CAMERA'
                  ? 'bg-white text-emerald-700 shadow-xs'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              <Camera className="w-3.5 h-3.5" />
              <span>Direct Camera</span>
            </button>

            <button
              type="button"
              onClick={() => {
                setActiveTab('URL');
                stopCamera();
              }}
              className={`flex-1 py-1.5 px-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center space-x-1 ${
                activeTab === 'URL'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              <LinkIcon className="w-3.5 h-3.5" />
              <span>Image URL</span>
            </button>
          </div>

          <div className="p-4">
            {/* Tab 1: Upload from Phone Gallery or Computer File */}
            {activeTab === 'FILE' && (
              <div>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  className="hidden"
                />
                <button
                  type="button"
                  onClick={() => fileInputRef.current?.click()}
                  className="w-full py-4 px-4 border-2 border-dashed border-slate-300 rounded-xl hover:border-emerald-500 hover:bg-emerald-50/50 transition-all flex flex-col items-center justify-center space-y-2 group"
                >
                  <div className="w-10 h-10 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <Upload className="w-5 h-5" />
                  </div>
                  <div className="text-center">
                    <p className="text-xs font-bold text-slate-800">
                      Click to choose photo from Phone Gallery or Computer
                    </p>
                    <p className="text-[11px] text-slate-400 mt-0.5">Supports JPG, PNG, WEBP</p>
                  </div>
                </button>
              </div>
            )}

            {/* Tab 2: Direct Camera Capture */}
            {activeTab === 'CAMERA' && (
              <div className="space-y-3 text-center">
                {/* Native mobile camera capture hidden fallback input */}
                <input
                  ref={cameraFileInputRef}
                  type="file"
                  accept="image/*"
                  capture="environment"
                  onChange={handleFileChange}
                  className="hidden"
                />

                {isCameraActive ? (
                  <div className="relative rounded-xl overflow-hidden bg-black max-w-md mx-auto aspect-video flex items-center justify-center">
                    <video
                      ref={videoRef}
                      autoPlay
                      playsInline
                      className="w-full h-full object-cover"
                    />
                    <div className="absolute bottom-3 inset-x-0 flex items-center justify-center space-x-3">
                      <button
                        type="button"
                        onClick={capturePhoto}
                        className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-lg flex items-center space-x-1.5"
                      >
                        <Camera className="w-4 h-4" />
                        <span>Snap Photo</span>
                      </button>
                      <button
                        type="button"
                        onClick={stopCamera}
                        className="px-3 py-2 bg-slate-800/80 hover:bg-slate-900 text-white text-xs font-medium rounded-xl"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {cameraError && (
                      <p className="text-xs text-amber-700 bg-amber-50 p-2 rounded-lg border border-amber-200">
                        {cameraError}
                      </p>
                    )}
                    <button
                      type="button"
                      onClick={() => {
                        if (cameraFileInputRef.current) {
                          cameraFileInputRef.current.click();
                        } else {
                          startCamera();
                        }
                      }}
                      className="w-full py-4 px-4 border-2 border-emerald-300 bg-emerald-50/70 hover:bg-emerald-100 rounded-xl transition-all flex flex-col items-center justify-center space-y-2 group"
                    >
                      <div className="w-10 h-10 rounded-full bg-emerald-600 text-white flex items-center justify-center group-hover:scale-110 transition-transform shadow-md">
                        <Camera className="w-5 h-5" />
                      </div>
                      <div>
                        <p className="text-xs font-bold text-emerald-950">
                          Open Camera & Take Photo Now
                        </p>
                        <p className="text-[11px] text-emerald-700">
                          Opens your phone camera directly
                        </p>
                      </div>
                    </button>
                  </div>
                )}
              </div>
            )}

            {/* Tab 3: Image URL Input */}
            {activeTab === 'URL' && (
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  <input
                    type="url"
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    placeholder="https://images.unsplash.com/photo-..."
                    className="w-full px-3 py-2 border border-slate-300 rounded-lg text-xs focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                  />
                  <button
                    type="button"
                    onClick={setSamplePhoto}
                    className="px-3 py-2 text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg whitespace-nowrap"
                  >
                    Sample
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
