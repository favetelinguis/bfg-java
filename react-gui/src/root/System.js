import {useSubscription} from "react-stomp-hooks";
import {getContent} from "./utils";
import {useState} from "react";

function System({market}) {
  const {epic} = market;
  const [midPrice, setMidPrice] = useState(market.currentMidPrice);
  useSubscription(`/topic/${epic}/midPrice`,
      (message) => {
    const newMidPrice = getContent(message);
    setMidPrice(oldMidPrice => newMidPrice ? newMidPrice : oldMidPrice);
      });
  console.log(midPrice)
  return (
      <div>{epic}: {midPrice.level}</div>
  )
}

export default System;
