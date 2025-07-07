import React, { useEffect, useRef, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { SignalingService } from '../services/signalingService';
import { Button } from '../components/ui/Button';
import { ScreenShare, PhoneOff } from 'lucide-react';

const StreamPage: React.FC = () => {
  const { streamId } = useParams<{ streamId: string }>();
  const navigate = useNavigate();
  const { user } = useAuthStore.getState();

  const localVideoRef = useRef<HTMLVideoElement>(null);
  const remoteVideoRef = useRef<HTMLVideoElement>(null); // A single ref for the remote video
  const signalingServiceRef = useRef<SignalingService | null>(null);

  const [isSharing, setIsSharing] = useState(false);

  // This callback will be given to the service to update our remote video element
  const handleAddRemoteStream = useCallback((stream: MediaStream) => {
    if (remoteVideoRef.current) {
      remoteVideoRef.current.srcObject = stream;
    }
  }, []);

  // Effect to connect and disconnect
  useEffect(() => {
    if (streamId && user?.username) {
      const service = new SignalingService(streamId, user.username, handleAddRemoteStream);
      signalingServiceRef.current = service;
      service.connect().catch(() => navigate('/'));

      return () => service.close();
    }
  }, [streamId, user, handleAddRemoteStream, navigate]);

  const handleShareAndCall = async () => {
    const service = signalingServiceRef.current;
    if (service) {
      try {
        const stream = await navigator.mediaDevices.getDisplayMedia({ video: true, audio: true });
        if (localVideoRef.current) {
          localVideoRef.current.srcObject = stream;
        }
        setIsSharing(true);
        // This single action now starts the call process
        service.startCall(stream);
      } catch (error) {
        console.error("Could not start screen sharing:", error);
      }
    }
  };

  const handleLeave = () => {
    signalingServiceRef.current?.close();
    navigate('/');
  };

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-white font-sans">
      <header className="p-4 text-center text-xl font-bold border-b border-gray-700">
        Stream Room: {streamId}
      </header>

      <main className="flex-grow p-4 grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <h3 className="font-bold mb-2">My View</h3>
          <video ref={localVideoRef} autoPlay muted className="bg-black w-full rounded-lg aspect-video"></video>
        </div>
        <div>
          <h3 className="font-bold mb-2">Remote View</h3>
          <video ref={remoteVideoRef} autoPlay className="bg-black w-full rounded-lg aspect-video"></video>
        </div>
      </main>

      <footer className="bg-gray-800 border-t border-gray-700 p-4 flex justify-center items-center space-x-4">
        <Button onClick={handleShareAndCall} disabled={isSharing} className="!w-auto bg-blue-600 hover:bg-blue-700 disabled:bg-gray-500 flex items-center gap-2">
          <ScreenShare size={20} /> Share & Call
        </Button>
        <Button onClick={handleLeave} className="!w-auto bg-red-600 hover:bg-red-700 flex items-center gap-2">
          <PhoneOff size={20} /> Leave
        </Button>
      </footer>
    </div>
  );
};

export default StreamPage;