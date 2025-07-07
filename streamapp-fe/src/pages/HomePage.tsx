import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import api from '../lib/api';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { Video, LogOut, Loader2 } from 'lucide-react';

// Define the type for a stream object received from the API
interface Stream {
  id: number;
  title: string;
  hostUsername: string;
  status: string;
  createdAt: string;
}

const HomePage: React.FC = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  
  const [title, setTitle] = useState('');
  const [liveStreams, setLiveStreams] = useState<Stream[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isFetching, setIsFetching] = useState(true); // For initial stream list loading
  const [error, setError] = useState<string | null>(null);

  // Fetch live streams when the component mounts
  useEffect(() => {
    let isMounted = true; // Prevent state updates on an unmounted component
    
    const fetchLiveStreams = async () => {
      try {
        const response = await api.get<Stream[]>('/api/streams/live');
        if (isMounted) {
          setLiveStreams(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch live streams", error);
        if (isMounted) {
          setError("Could not load live streams.");
        }
      } finally {
        if (isMounted) {
          setIsFetching(false);
        }
      }
    };
    
    fetchLiveStreams();
    
    return () => {
      isMounted = false;
    };
  }, []);

  const handleCreateStream = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      alert("Please enter a title for your stream.");
      return;
    }

    setIsLoading(true);

    try {
      // Step 1: Create the stream record. It will be in a 'PENDING' state.
      const createResponse = await api.post<Stream>('/api/streams', { title });
      const newStream = createResponse.data;
      console.log(`Stream created with ID: ${newStream.id}`);

      // Step 2: Explicitly start the stream. This changes its status to 'LIVE'.
      await api.post(`/api/streams/${newStream.id}/start`);
      console.log(`Stream ${newStream.id} started.`);

      // Step 3: ONLY after the stream is successfully created and started, navigate to the room.
      navigate(`/stream/${newStream.id}`);

    } catch (error) {
      console.error('Failed to create or start stream', error);
      alert('An error occurred while creating your stream. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="bg-gray-900 min-h-screen text-white p-4 sm:p-8">
      <div className="max-w-4xl mx-auto">
        <header className="flex justify-between items-center mb-8">
          <h1 className="text-2xl sm:text-3xl font-bold text-blue-400">StreamZone</h1>
          <div className="flex items-center space-x-4">
            <span className="hidden sm:block">Welcome, {user?.username}</span>
            <Button onClick={handleLogout} className="!w-auto p-2 bg-red-600 hover:bg-red-700">
              <LogOut size={20} />
            </Button>
          </div>
        </header>

        <div className="bg-gray-800 p-6 rounded-lg shadow-lg mb-8">
          <h2 className="text-xl font-semibold mb-4">Create a New Stream</h2>
          <form onSubmit={handleCreateStream} className="flex flex-col sm:flex-row gap-4">
            <Input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Enter your stream title..."
              className="flex-grow"
              required
              disabled={isLoading}
            />
            <Button type="submit" disabled={isLoading} className="!w-full sm:!w-auto flex justify-center items-center gap-2">
              {isLoading ? (
                <>
                  <Loader2 className="animate-spin" size={20} />
                  Creating...
                </>
              ) : (
                'Go Live'
              )}
            </Button>
          </form>
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4 border-b-2 border-gray-700 pb-2">Live Now</h2>
          {isFetching ? (
            <p className="text-gray-400">Loading live streams...</p>
          ) : error ? (
            <p className="text-red-500">{error}</p>
          ) : liveStreams.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {liveStreams.map((stream) => (
                <div key={stream.id} className="bg-gray-800 rounded-lg shadow-lg overflow-hidden transform hover:-translate-y-1 transition-transform duration-300">
                  <div className="p-4">
                    <h3 className="font-bold text-lg truncate">{stream.title}</h3>
                    <p className="text-sm text-gray-400">Hosted by {stream.hostUsername}</p>
                  </div>
                  <Button onClick={() => navigate(`/stream/${stream.id}`)} className="rounded-t-none !w-full flex items-center justify-center gap-2">
                    <Video size={16} />
                    Join Stream
                  </Button>
                  <Button onClick={() => navigate(`/stream/livekit/${stream.id}`)} className="flex-1 bg-green-600 hover:bg-green-700"><Video size={16} />Join (LiveKit)</Button>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-500 mt-4">No live streams at the moment. Why not start one?</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default HomePage;