import React, {useEffect, useState} from "react";
import Chess from "chess.js";
import {Badge, Button, ButtonGroup, Card, Col, ListGroup, Row} from "react-bootstrap";
import Container from "react-bootstrap/Container";
import {Chessboard} from "react-chessboard";
import SanViewer from "./SanViewer";
import Clock from "./Clock";

export default function Board({boardId, whitePlayer, blackPlayer}) {
    const [game, setGame] = useState(() => {
        return new Chess();
    });

    const [board, setBoard] = useState(undefined);
    const [fullGame, setFullGame] = useState(new Chess());
    const [positon, setPosition] = useState(0);
    const [blackTimestamp, setBlackTimestamp] = useState(undefined);
    const [whiteTimestamp, setWhiteTimestamp] = useState(undefined);

    const safeMutate = (modify, f) => {
        f((g) => {
            const update = { ...g };
            modify(update);
            return update;
        });
    }

    const renderMoves = (board, pos) => {
        const moves = board.moves;

        safeMutate((game) => {
            game.reset();
        }, setGame);

        for (let i = 0; i < pos; i++) {
            safeMutate((game) => {
                game.move(moves[i].move, { sloppy: true });
            }, setGame);
        }
    }

    const getLastMove = (board) => {
        if (board && board.moves && board.moves.length > 0) {
            return board.moves[board.moves.length - 1];
        }
        else return null;
    }

    const getClockLiveTimestamp = (color, board) => {
        const lastMove = getLastMove(board);

        if (lastMove && lastMove.playerColor === color && !board.result) {
            return lastMove.timestamp;
        }

        return null;
    }

    const updateClocks = (board) => {
        setBlackTimestamp(blackTimestamp => getClockLiveTimestamp('BLACK', board))
        setWhiteTimestamp(whiteTimestamp => getClockLiveTimestamp('WHITE', board))

        return board;
    }

    useEffect(() => {
        const sse = new EventSource('/boards/' + boardId + '/live-updates');

        const onBoardEvent = (event) => {
            const data = JSON.parse(event.data);

            setPosition(data.moves.length);

            safeMutate((fullGame) => {
                for (let i = 0; i < data.moves.length; i++) {
                    fullGame.move(data.moves[i].move, { sloppy: true });
                }
            }, setFullGame);

            setBoard((board) => updateClocks(data));

            renderMoves(data, data.moves.length);
        }

        const onMove = (event) => {
            const data = JSON.parse(event.data)

            safeMutate((game) => {
                game.move(data.move, { sloppy: true });
            }, setGame);

            safeMutate((game) => {
                game.move(data.move, { sloppy: true });
            }, setFullGame);

            setBoard((board) =>
                updateClocks({
                    ...board,
                    moves: [...board.moves, data],
                    whiteClock: data.playerColor === 'WHITE' ? data.clock : board.whiteClock,
                    blackClock: data.playerColor === 'BLACK' ? data.clock : board.blackClock
                })
            );
        }

        const onResult = (event) => {
            setBoard((board) => updateClocks({
                ...board,
                result: event.data
            }));
        }

        sse.addEventListener('move', onMove);
        sse.addEventListener('board', onBoardEvent);
        sse.addEventListener('result', onResult);

        return () => {
            sse.close();
            sse.removeEventListener('board', onBoardEvent)
            sse.removeEventListener('move', onMove)
            sse.removeEventListener('result', onResult)
        }
    }, []);

    const start = () => {
        setPosition(0)
        renderMoves(board, 0);
    }

    const end = () => {
        setPosition(board.moves.length)
        renderMoves(board, board.moves.length);
    }

    const undo = () => {
        if (positon > 0) {
            setPosition(positon - 1);

            renderMoves(board, positon - 1);
        }
    }

    const redo = () => {
        if (positon < board.moves.length) {
            setPosition(positon + 1);

            renderMoves(board, positon + 1);
        }
    }

    const goToPosition = (pos) => {
        setPosition(pos);

        renderMoves(board, pos);
    }

    return (
        board && <Container>
            <Row>
                <Col md sm={8}>
                    <ListGroup>
                        <ListGroup.Item className="border-0">
                            <Clock clock={board.blackClock} timestamp={blackTimestamp} />
                            {blackPlayer && <Badge bg="dark">{blackPlayer.name}</Badge>}
                        </ListGroup.Item>
                        <ListGroup.Item className="border-0">
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
                        </ListGroup.Item>
                        <ListGroup.Item className="border-0">
                            <Clock clock={board.whiteClock} timestamp={whiteTimestamp} />
                            {whitePlayer && <Badge bg="light" text={"dark"}>{whitePlayer.name}</Badge>}
                        </ListGroup.Item>
                    </ListGroup>
                </Col>
                <Col md className="ms-5" sm={4}>
                    <Card style={{ width: '16rem' }} className="p-3">
                        <SanViewer game={fullGame} onPositionChange={(pos) => goToPosition(pos)}/>
                    </Card>
                    <ButtonGroup className="mt-4 d-flex" >
                        <Button variant="secondary" onClick={start}>{"|<"}</Button>
                        <Button variant="secondary" onClick={undo}>{"<"}</Button>
                        <Button variant="secondary" onClick={redo}>{">"}</Button>
                        <Button variant="secondary" onClick={end}>{">|"}</Button>
                    </ButtonGroup>
                    { board.result && <Container>Result: {board.result}</Container> }
                </Col>
            </Row>
        </Container>
    )
}