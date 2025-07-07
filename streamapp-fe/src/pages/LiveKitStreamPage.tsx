import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  LiveKitRoom,
  VideoConference,
} from '@livekit/components-react';
import '@livekit/components-styles';
import api from '../lib/api';

const LiveKitStreamPage: React.FC = () => {
  const { streamId } = useParams<{ streamId: string }>();
  const navigate = useNavigate();
  const [token, setToken] = useState<string>('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!streamId) {
      navigate('/');
      return;
    }

    const fetchToken = async () => {
      try {
        // The new URL includes the streamId in the path.
        // The POST request now has an empty body.
        const response = await api.post(`/api/streams/${streamId}/token`, {});

        setToken(response.data.token);
      } catch (error) {
        console.error('Failed to get LiveKit join token:', error);
        alert('Could not join stream. It may not exist or has ended.');
        navigate('/');
      } finally {
        setIsLoading(false);
      }
    };

    fetchToken();
  }, [streamId, navigate]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen bg-gray-900 text-white">
        <p>Connecting to stream...</p>
      </div>
    );
  }

  if (!token) {
    return (
      <div className="flex items-center justify-center h-screen bg-gray-900 text-white">
        <p>Error: Could not retrieve access token. Redirecting home.</p>
      </div>
    );
  }

  return (
    <div style={{ height: '100vh' }}>
      <LiveKitRoom
        serverUrl={import.meta.env.VITE_LIVEKIT_URL}
        token={token}
        connect={true}
        video={true} 
        audio={true}
        data-lk-theme="default"
        onDisconnected={() => navigate('/')}
      >
        <VideoConference />
      </LiveKitRoom>
    </div>
  );
};

export default LiveKitStreamPage;