import { useState, useEffect } from 'react';
import { Target, Clock, CheckCircle2, Circle } from 'lucide-react';
import { fetchGoals, fetchReminders, createGoal, createReminder } from '../api';
import './Dashboard.css';

interface DashboardProps {
  activeTab?: 'dashboard' | 'goals' | 'reminders';
}

export default function Dashboard({ activeTab = 'dashboard' }: DashboardProps) {
  const [goals, setGoals] = useState<any[]>([]);
  const [reminders, setReminders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // New item states
  const [newTitle, setNewTitle] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [newDate, setNewDate] = useState('');

  const loadData = async () => {
    try {
      setLoading(true);
      const [g, r] = await Promise.all([fetchGoals(), fetchReminders()]);
      setGoals(g);
      setReminders(r);
    } catch (e) {
      console.error(e);
      // For MVP Demo purposes if backend is down
      setGoals([{ id: 1, title: 'Learn Spring Boot', status: 'IN_PROGRESS' }]);
      setReminders([{ id: 1, title: 'Drink Water', completed: false, dueDate: new Date().toISOString() }]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleAddGoal = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle) return;
    try {
      await createGoal({ title: newTitle, description: newDesc, targetDate: newDate, status: 'PENDING' });
      setNewTitle(''); setNewDesc(''); setNewDate('');
      loadData();
    } catch (e) { console.error(e); }
  };

  const handleAddReminder = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle) return;
    try {
      await createReminder({ title: newTitle, description: newDesc, dueDate: newDate });
      setNewTitle(''); setNewDesc(''); setNewDate('');
      loadData();
    } catch (e) { console.error(e); }
  };

  if (loading) {
    return <div className="dashboard-container"><div className="loading-spinner">Loading...</div></div>;
  }

  const showGoals = activeTab === 'dashboard' || activeTab === 'goals';
  const showReminders = activeTab === 'dashboard' || activeTab === 'reminders';

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1>{activeTab.charAt(0).toUpperCase() + activeTab.slice(1)}</h1>
        <p className="subtitle">Here's your personal overview</p>
      </header>

      <div className="dashboard-grid">
        {showGoals && (
          <section className="dashboard-section card animate-fade-in">
            <div className="section-header">
              <Target className="section-icon text-primary" />
              <h2>Your Goals</h2>
            </div>
            
            <div className="items-list">
              {goals.length === 0 ? <p className="empty-state">No goals set yet.</p> : null}
              {goals.map(goal => (
                <div key={goal.id} className="item-row">
                  <div className="item-status">
                    {goal.status === 'COMPLETED' ? <CheckCircle2 className="text-success" /> : <Circle className="text-muted" />}
                  </div>
                  <div className="item-content">
                    <h4>{goal.title}</h4>
                    <span className="badge">{goal.status}</span>
                  </div>
                </div>
              ))}
            </div>

            {activeTab === 'goals' && (
              <form className="add-form" onSubmit={handleAddGoal}>
                <h4>Add New Goal</h4>
                <input className="input" placeholder="Goal Title" value={newTitle} onChange={e=>setNewTitle(e.target.value)} required />
                <button type="submit" className="btn btn-primary mt-2">Add Goal</button>
              </form>
            )}
          </section>
        )}

        {showReminders && (
          <section className="dashboard-section card animate-fade-in" style={{ animationDelay: '0.1s' }}>
            <div className="section-header">
              <Clock className="section-icon text-warning" />
              <h2>Upcoming Reminders</h2>
            </div>
            
            <div className="items-list">
              {reminders.length === 0 ? <p className="empty-state">No reminders set.</p> : null}
              {reminders.map(rem => (
                <div key={rem.id} className="item-row">
                  <div className="item-status">
                    {rem.completed ? <CheckCircle2 className="text-success" /> : <Circle className="text-muted" />}
                  </div>
                  <div className="item-content">
                    <h4>{rem.title}</h4>
                    {rem.dueDate && <span className="date-text">{new Date(rem.dueDate).toLocaleString()}</span>}
                  </div>
                </div>
              ))}
            </div>

            {activeTab === 'reminders' && (
              <form className="add-form" onSubmit={handleAddReminder}>
                <h4>Add New Reminder</h4>
                <input className="input" placeholder="Reminder Title" value={newTitle} onChange={e=>setNewTitle(e.target.value)} required />
                <input className="input mt-2" type="datetime-local" value={newDate} onChange={e=>setNewDate(e.target.value)} required />
                <button type="submit" className="btn btn-primary mt-2">Add Reminder</button>
              </form>
            )}
          </section>
        )}
      </div>
    </div>
  );
}
