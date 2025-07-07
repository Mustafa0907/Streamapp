// A type for our WebSocket messages for clarity
interface WebSocketMessage {
  type: 'offer' | 'answer' | 'candidate';
  sender: string;
  payload: any;
}

export class SignalingService {
  private ws: WebSocket | null = null;
  private peerConnection: RTCPeerConnection | null = null;
  private localStream: MediaStream | null = null;
  private readonly streamId: string;
  private readonly username: string;

  // Callback to update the React UI
  private readonly onAddRemoteStream: (stream: MediaStream) => void;

  private ICE_SERVERS = [{ urls: 'stun:stun.l.google.com:19302' }];

  constructor(
    streamId: string,
    username: string,
    onAddRemoteStream: (stream: MediaStream) => void
  ) {
    this.streamId = streamId;
    this.username = username;
    this.onAddRemoteStream = onAddRemoteStream;
  }

  // --- Public Methods ---

  public connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      const wsUrl = `ws://localhost:8080/ws/signal/${this.streamId}`;
      this.ws = new WebSocket(wsUrl);

      this.ws.onopen = () => {
        console.log('✅ Signaling server connected');
        resolve();
      };

      this.ws.onmessage = (message) => {
        const data: WebSocketMessage = JSON.parse(message.data);
        if (data.sender === this.username) return; // Ignore self
        
        console.log(`📩 Received '${data.type}' from '${data.sender}'`);

        switch (data.type) {
          case 'offer':
            this.handleOffer(data.payload);
            break;
          case 'answer':
            this.handleAnswer(data.payload);
            break;
          case 'candidate':
            this.handleCandidate(data.payload);
            break;
        }
      };
      
      this.ws.onerror = (err) => reject(err);
      this.ws.onclose = () => console.log('Signaling server disconnected');
    });
  }

  public async startCall(localStream: MediaStream) {
    console.log('▶️ Starting call and sending offer...');
    this.localStream = localStream;
    this.createPeerConnection();

    if (this.peerConnection) {
      const offer = await this.peerConnection.createOffer();
      await this.peerConnection.setLocalDescription(offer);
      this.sendMessage({ type: 'offer', payload: offer });
    }
  }

  public close() {
    console.log('🔌 Closing connection.');
    this.peerConnection?.close();
    this.localStream?.getTracks().forEach(track => track.stop());
    this.ws?.close();
  }

  // --- Private Handlers (Directly from test.html) ---

  private createPeerConnection() {
    this.peerConnection = new RTCPeerConnection({ iceServers: this.ICE_SERVERS });

    this.peerConnection.onicecandidate = event => {
      if (event.candidate) {
        this.sendMessage({ type: 'candidate', payload: event.candidate });
      }
    };

    this.peerConnection.ontrack = event => {
      console.log('✅✅✅ Received remote track!');
      this.onAddRemoteStream(event.streams[0]);
    };

    if (this.localStream) {
      this.localStream.getTracks().forEach(track => {
        this.peerConnection!.addTrack(track, this.localStream!);
      });
    }
  }

  private async handleOffer(offer: RTCSessionDescriptionInit) {
    console.log('📞 Handling received offer...');
    this.createPeerConnection(); // Create the connection when an offer is received
    await this.peerConnection!.setRemoteDescription(new RTCSessionDescription(offer));
    
    const answer = await this.peerConnection!.createAnswer();
    await this.peerConnection!.setLocalDescription(answer);
    
    this.sendMessage({ type: 'answer', payload: answer });
  }

  private async handleAnswer(answer: RTCSessionDescriptionInit) {
    console.log('🤝 Handling received answer...');
    if (this.peerConnection) {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
    }
  }

  private async handleCandidate(candidate: RTCIceCandidateInit) {
    console.log('🧊 Adding received ICE candidate...');
    if (this.peerConnection) {
      await this.peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
    }
  }

  private sendMessage(message: Partial<WebSocketMessage>) {
    const messageWithSender = { ...message, sender: this.username };
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(messageWithSender));
    }
  }
}