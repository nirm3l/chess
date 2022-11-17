import React from "react";
import ReactDOM from "react-dom/client";
import Container from 'react-bootstrap/Container';

import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate
} from 'react-router-dom';

import "./index.css";
import 'bootstrap/dist/css/bootstrap.min.css';

import Game from "./components/Game";
const Home = () => {
  return (<h1 className="header">Welcome To Chess!</h1>);
};

const Error = () => {
  return "Board not found!";
};

export const ChessContext = React.createContext();

function App() {
  return (
      <Container className="p-3">
          <Router>
            <Routes>
              <Route path="game/:gameId" element={<Game />} />
              <Route path="home" element={<Home />} />
              <Route path="error" element={<Error />} />
              <Route path="" element={<Navigate to="/home" />} />
            </Routes>
          </Router>
      </Container>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
