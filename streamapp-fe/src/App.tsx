// import { useState } from 'react'
// import reactLogo from './assets/react.svg'
// import viteLogo from '/vite.svg'
// import './App.css'

// function App() {
//   const [count, setCount] = useState(0)

//   return (
//     <>
//       <div>
//         <a href="https://vite.dev" target="_blank">
//           <img src={viteLogo} className="logo" alt="Vite logo" />
//         </a>
//         <a href="https://react.dev" target="_blank">
//           <img src={reactLogo} className="logo react" alt="React logo" />
//         </a>
//       </div>
//       <h1>Vite + React</h1>
//       <div className="card">
//         <button onClick={() => setCount((count) => count + 1)}>
//           count is {count}
//         </button>
//         <p>
//           Edit <code>src/App.tsx</code> and save to test HMR
//         </p>
//       </div>
//       <p className="read-the-docs">
//         Click on the Vite and React logos to learn more
//       </p>
//     </>
//   )
// }

// export default App
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Import Page Components
import HomePage from './pages/HomePage';
import AuthPage from './pages/AuthPage';
import StreamPage from './pages/StreamPage';
import LiveKitStreamPage from './pages/LiveKitStreamPage';

// Import the route protection component
import ProtectedRoute from './components/ui/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* --- Public Routes --- */}
        {/* These routes are accessible to everyone. */}
        <Route path="/login" element={<AuthPage />} />
        <Route path="/register" element={<AuthPage />} />

        {/* --- Protected Routes --- */}
        {/* The <ProtectedRoute> component will check if a user is logged in. */}
        {/* If they are, it will render the child route (e.g., HomePage). */}
        {/* If not, it will redirect them to the /login page. */}
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/stream/:streamId" element={<StreamPage />} />
          <Route path="/stream/livekit/:streamId" element={<LiveKitStreamPage />} /> {/* New LiveKit page */}

          {/* Add any other future protected routes here */}
        </Route>

        {/* --- Fallback Route --- */}
        {/* If a user tries to access any other path, redirect them to the home page. */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;