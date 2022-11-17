import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import Chess from "chess.js";
import {Chessboard} from "react-chessboard";
import axios from "axios";

export default function Board({}) {
    const navigate = useNavigate();
    const params = useParams();

    const [game, setGame] = useState(() => {
        return new Chess();
    });

    const[players, setPlayers] = useState([]);

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const sse = new EventSource('/boards/' + params.boardId + '/live-updates');

        const onBoard = (event) => {
            setLoading(false);

            const data = JSON.parse(event.data);

            axios.get('/players/' + data.playerWhite).then((response) => {
                setPlayers(response.data);
            });

            data.moves.forEach(move => {
                safeGameMutate((game) => {
                    game.move(move.move, { sloppy: true });
                });
            });
        }

        const onMove = (event) => {
            const data = JSON.parse(event.data)

            safeGameMutate((game) => {
                game.move(data.move, { sloppy: true });
            });
        }

        sse.addEventListener('move', onMove);
        sse.addEventListener('board', onBoard);

        sse.onerror = () => {
            navigate("/error");

            sse.close();
        }

        function safeGameMutate(modify) {
            setGame((g) => {
                const update = { ...g };
                modify(update);
                return update;
            });
        }

        return () => {
            sse.close();
            sse.removeEventListener('board', onBoard)
            sse.removeEventListener('move', onBoard)
        }
    }, []);

    return !loading ? <div>
        <Chessboard
            arePremovesAllowed={true}
            animationDuration={0}
            position={game.fen()}
            arePiecesDraggable={false}
            customBoardStyle={{
                borderRadius: '4px',
                boxShadow: '0 5px 15px rgba(0, 0, 0, 0.5)'
            }}
        />
    </div> : "Loading...";
}