import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import axios from "axios";
import Board from "./Board";

export default function Game({}) {
    const navigate = useNavigate();
    const params = useParams();

    const[whitePlayer, setWhitePlayer] = useState({});
    const[blackPlayer, setBlackPlayer] = useState({});
    const[game, setGame] = useState(undefined);

    useEffect(() => {
        axios.get('/games/' + params.gameId).then((response) => {
            setGame(response.data);

            axios.get('/players/' + response.data.whitePlayerId).then((response) => {
                setWhitePlayer(response.data);
            });

            axios.get('/players/' + response.data.blackPlayerId).then((response) => {
                setBlackPlayer(response.data);
            });
        });
    }, []);

    return (
        game && <Board boardId={game.boardId} whitePlayer={whitePlayer} blackPlayer={blackPlayer}></Board>
    );
}