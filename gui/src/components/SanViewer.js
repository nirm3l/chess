import React, {useEffect, useState} from "react";
import axios from "axios";
import {Button, Table} from "react-bootstrap";

export default function Players({}) {
    const [players, setPlayers] = useState([]);

    useEffect(() => {
        axios.get('/players').then((response) => {
            setPlayers(response.data);
        });

        return () => {
        }
    }, []);

    return (
        <Table striped bordered hover>
            <thead>
            <tr>
                <th>Name</th>
                <th>Rating</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            {
                players.map((player) =>
                    <tr key={player.playerId}>
                        <td>{player.name}</td>
                        <td>{player.rating}</td>
                        <td>
                            <Button variant="primary" size="sm">
                                Challenge
                            </Button>
                        </td>
                    </tr>
                )}
            </tbody>
        </Table>
    );
}