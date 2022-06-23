import {useStompClient, useSubscription} from "react-stomp-hooks";
import {useEffect, useState} from "react";
import System from "./System";
import {getContent} from "./utils";
import axios from "axios";

function Home() {
  const [isLoading, setIsLoading] = useState(false);
  const [openMarkets, setOpenMarkets] = useState([]);
  // TODO only add market if its not already in the list
  useSubscription("/topic/system",
      (message) => {
        console.log(message.body);
    setOpenMarkets(JSON.parse(message.body))
      });
  useSubscription("/topic/system/open",
      (message) => setOpenMarkets(oldMarkets => [...oldMarkets, getContent(message)]));
  useSubscription("/topic/system/close",
      (message) => setOpenMarkets(oldMarkets => oldMarkets.filter(m => m.epic === getContent(message).epic)));

  const stompClient = useStompClient();
  useEffect(() => {
    async function getSystems() {
      setIsLoading(true);
      // if (stompClient) {
      //   stompClient.publish({destination: "/app/system"})
      // } else {
      //   console.log("STOMP CLIENT NOT INIT");
      // }
      const response = await axios.get("/api/system");
      setOpenMarkets(response.data)
      setIsLoading(false);
    }
    getSystems();
  }, [stompClient]);

  if (isLoading) return <h1>Loading...</h1>;

  return (
      <div className="Home">
        {openMarkets.map(market => <System key={market.epic} market={market}/> )}
      </div>
  );
}

export default Home;
