import React, { useState, useRef, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import ReactMarkdown from 'react-markdown';
import Select from 'react-select';
import './App.css';


function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [feedback, setFeedback] = useState({});
  const [showFeedback, setShowFeedback] = useState(false);
  const [selectedSpaces, setSelectedSpaces] = useState([]);
  const [availableSpaces, setAvailableSpaces] = useState([]);

    const spaceOptions = availableSpaces.map(space => ({
      value: space.spaceId,
      label: space.spaceName,
    }));

  const bottomRef = useRef(null);

  useEffect(() => {
    fetch('/askfluence/spaces')
      .then((res) => res.json())
      .then((data) => {
        setAvailableSpaces(data);
      })
      .catch((error) => {
        console.error('Failed to load spaces:', error);
      });
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleFeedback = (index, type) => {
    setFeedback(prev => ({ ...prev, [index]: type }));
    console.log(`Feedback for message ${index}: ${type}`);
  };

 const sendMessage = async () => {
   if (input.trim() === '') return;

   const userMessage = { sender: 'user', text: input };
   setMessages([...messages, userMessage]);
   setInput('');
   setLoading(true);
   setShowFeedback(false);

   try {
     const response = await fetch(`/askfluence/ask`, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify({
         question: input,
         spaces: selectedSpaces  // 👈 Added here
       }),
     });

     const data = await response.json();
     displayBotMessage(data);
   } catch (error) {
     console.error('Error:', error);
     displayBotMessage('Oops! Something went wrong. Try again.');
   }
 };


 const displayBotMessage = (text) => {
   let index = 0;
   let newMessage = '';
   setLoading(false);

   const updateMessage = () => {
     if (index < text.length) {
       newMessage += text[index];
       setMessages((prev) => [
         ...prev.slice(0, -1),
         { sender: 'bot', text: newMessage },
       ]);
       index++;
       requestAnimationFrame(updateMessage); // Replaces setInterval
     } else {
       setShowFeedback(true);
     }
   };

   setMessages((prev) => [...prev, { sender: 'bot', text: '' }]);
   requestAnimationFrame(updateMessage); // Start the animation
 };

  return (
      <div className="container mt-4">
        <header className="bg-light text-dark text-center py-1 d-flex flex-column align-items-center">
          <div className="d-flex align-items-center">
            <img src="/askfluence1_Gv4_icon.ico" alt="Logo" height="40" className="rounded-circle me-3" />
            <h1 className="mb-0">AskFluence</h1>
          </div>
            <h8 className="fw-light mt-0"style={{marginLeft: 2 + 'em'}}>Why search when you can just ask!</h8>

        </header>
        <div className="card mt-3" style={{ maxWidth: '900px', margin: '0 auto', height: '430px' }}>
          <div className="card-body d-flex flex-column" style={{ overflowY: 'auto' }}>
            <div className="chatbox flex-grow-1">
              {messages.map((msg, index) => (
                <div key={index} className={`d-flex align-items-start mb-2 ${msg.sender === 'user' ? 'justify-content-end' : 'justify-content-start'}`}>
                  {msg.sender === 'bot' && <img src="/AskfluenceBot.ico" alt="Bot" className="rounded-circle me-2" style={{ width: '40px', height: '40px' }} />}
                <div className={`alert ${msg.sender === 'user' ? 'alert-primary' : 'alert-secondary'}`}
                        style={{ maxWidth: '75%', backgroundColor: msg.sender === 'bot' ? '#f8f9fa' : '', color: 'black' }}>
                    <ReactMarkdown
                      components={{
                        a: ({ node, ...props }) => <a {...props} target="_blank" rel="noopener noreferrer" style={{ color: 'blue', textDecoration: 'underline' }} />,
                      }}
                    >
                      {msg.text}
                    </ReactMarkdown>

                    {msg.sender === 'bot' && showFeedback && (
                            <div className="mt-2 d-flex">
                              <button className="btn btn-outline-success btn-sm me-2" onClick={() => handleFeedback(index, 'up')}>
                                👍
                              </button>
                              <button className="btn btn-outline-danger btn-sm" onClick={() => handleFeedback(index, 'down')}>
                                👎
                              </button>
                            </div>
                          )}
                  </div>
                  {msg.sender === 'user' && <img src="/AskfluenceUser.ico" alt="User" className="rounded-circle ms-2" style={{ width: '40px', height: '40px' }} />}
                </div>
              ))}
              {loading && <div className="text-muted">⏳ Generating...</div>}
            <div ref={bottomRef} />
            </div>
          </div>
        </div>

        <div className="input-group mt-3" style={{ maxWidth: '900px', margin: '0 auto' }}>
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
            className="form-control"
            placeholder="What do you want to ask?"
          />
          <button onClick={sendMessage} className="btn btn-primary">Ask</button>
        </div>
        <div className="mt-3" style={{ maxWidth: '900px', margin: '0 auto' }}>
            <div className="filter-group">
                    <Select
                      classNamePrefix="custom-select"
                      options={spaceOptions}
                      isMulti
                      placeholder="Filter by Confluence Space (optional):"
                      value={spaceOptions.filter(option => selectedSpaces.includes(option.value))}
                      onChange={(selected) => {
                        const values = selected ? selected.map(s => s.value) : [];
                        setSelectedSpaces(values);
                      }}
                    />
                  </div>
                </div>
        <footer className="bg-light text-center py-1 mt-1">
          <p>&copy; 2025 AskFluence (pravin.nimodiya@ideas.com)</p>
        </footer>
      </div>
    );
}

export default App;