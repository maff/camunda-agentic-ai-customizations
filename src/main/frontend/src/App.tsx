import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ConversationList } from './components/ConversationList';
import { ConversationDetail } from './components/ConversationDetail';
import { Content } from '@carbon/react';

function App() {
  return (
    <Router>
      <Content>
        <Routes>
          <Route path="/" element={<ConversationList />} />
          <Route path="/conversation/:conversationId" element={<ConversationDetail />} />
        </Routes>
      </Content>
    </Router>
  );
}

export default App;