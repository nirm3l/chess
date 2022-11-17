import React, {useEffect, useState} from "react";
import {Badge} from "react-bootstrap";

export default function Clock({clock, timestamp}) {
    const [currentTimeout, setCurrentTimeout] = useState(undefined);

    const getClock = (milliseconds) => {
        const padTo2Digits = (num) => {
            return num.toString().padStart(2, '0');
        }

        let seconds = Math.floor(milliseconds / 1000);
        let minutes = Math.floor(seconds / 60);
        let hours = Math.floor(minutes / 60);

        return `${padTo2Digits(hours % 24)}:${padTo2Digits(minutes % 60)}:${padTo2Digits(seconds % 60)}`;
    }

    const [currentClock, setCurrentClock] = useState(getClock(clock));

    const refresh = () => {
        setCurrentClock(currentClock => getClock(clock - (new Date().getTime() - timestamp)));
    };

    useEffect(() => {
        if (timestamp) {
            const newTimeout = setInterval(refresh, 100);
            setCurrentTimeout((t) => newTimeout);

            refresh();
        }

        return () => {
            if (currentTimeout) {
                clearTimeout(currentTimeout);
            }
        }
    }, [currentClock])

    return (
        <Badge bg="light float-end" text="dark">{currentClock}</Badge>
    );
}