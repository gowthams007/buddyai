import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import { Home, MessageSquare, CheckSquare, Bell } from 'lucide-react';
import ChatInterface from './components/ChatInterface';
import Dashboard from './components/Dashboard';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app-container">
        {/* Sidebar / Bottom Nav */}
        <nav className="glass app-nav">
          <div className="nav-logo">
            <div className="logo-orb"></div>
            <span>BuddyAI</span>
          </div>
          
          <div className="nav-links">
            <NavLink to="/" className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}>
              <MessageSquare size={20} />
              <span>Chat</span>
            </NavLink>
            <NavLink to="/dashboard" className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}>
              <Home size={20} />
              <span>Dashboard</span>
            </NavLink>
            <NavLink to="/goals" className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}>
              <CheckSquare size={20} />
              <span>Goals</span>
            </NavLink>
            <NavLink to="/reminders" className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}>
              <Bell size={20} />
              <span>Reminders</span>
            </NavLink>
          </div>
        </nav>

        {/* Main Content Area */}
        <main className="main-content">
          <Routes>
            <Route path="/" element={<ChatInterface />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/goals" element={<Dashboard activeTab="goals" />} />
            <Route path="/reminders" element={<Dashboard activeTab="reminders" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
