import { useState, useRef, useEffect } from 'react';
import { Send, Bot, User } from 'lucide-react';
import { chatWithAi } from '../api';
import './ChatInterface.css';

interface Message {
  id: string;
  sender: 'user' | 'ai';
  text: string;
}

export default function ChatInterface() {
  const [messages, setMessages] = useState<Message[]>([
    { id: '1', sender: 'ai', text: 'Hello! I am BuddyAI, your personal AI companion. How can I help you organize your life today?' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userMsg: Message = { id: Date.now().toString(), sender: 'user', text: input };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      // In MVP, we might get connection refused if backend is not fully setup, 
      // but assuming it's running:
      const res = await chatWithAi(userMsg.text);
      const aiMsg: Message = { id: (Date.now() + 1).toString(), sender: 'ai', text: res.response || "I didn't quite get that." };
      setMessages(prev => [...prev, aiMsg]);
    } catch (error) {
      console.error(error);
      const errorMsg: Message = { id: (Date.now() + 1).toString(), sender: 'ai', text: "Sorry, I'm having trouble connecting right now." };
      setMessages(prev => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-container">
      <header className="chat-header glass">
        <div className="chat-header-info">
          <div className="logo-orb small"></div>
          <div>
            <h2>BuddyAI Companion</h2>
            <span className="status">Online</span>
          </div>
        </div>
      </header>

      <div className="messages-area">
        {messages.map((msg) => (
          <div key={msg.id} className={`message-wrapper ${msg.sender} animate-fade-in`}>
            <div className="avatar">
              {msg.sender === 'ai' ? <Bot size={20} /> : <User size={20} />}
            </div>
            <div className="message-bubble">
              <p>{msg.text}</p>
            </div>
          </div>
        ))}
        {loading && (
          <div className="message-wrapper ai animate-fade-in">
            <div className="avatar"><Bot size={20} /></div>
            <div className="message-bubble loading">
              <span className="dot"></span>
              <span className="dot"></span>
              <span className="dot"></span>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <div className="input-area glass">
        <form onSubmit={handleSend} className="input-form">
          <input
            type="text"
            className="input"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type a message or ask me to set a reminder..."
            disabled={loading}
          />
          <button type="submit" className="btn btn-primary" disabled={loading || !input.trim()}>
            <Send size={18} />
          </button>
        </form>
      </div>
    </div>
  );
}
