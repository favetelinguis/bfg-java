import logo from './logo.svg';
import './App.css';
import Home from "./root/Home";
import {StompSessionProvider} from "react-stomp-hooks";

function App() {
  return (
      <StompSessionProvider url={"http://localhost:8080/bfgws"}>
        <Home/>
      </StompSessionProvider>
  );
}

export default App;
