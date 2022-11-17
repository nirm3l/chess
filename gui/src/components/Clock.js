import React from "react";

export default function SanViewer({game, onPositionChange}) {
    const pgnRegex = /([0-9]*?\.)(.*?)(?=[0-9]*?\.|$)/gm;

    const getSanMoves = () => {
        let result = [];

        let m;

        while ((m = pgnRegex.exec(game.pgn())) !== null) {
            if (m.index === pgnRegex.lastIndex) {
                pgnRegex.lastIndex++;
            }

            m.forEach((match, groupIndex) => {
                if (groupIndex === 2) {
                    const parts = match.split(' ');

                    result.push(parts[1])
                    result.push(parts[2])
                }
            });
        }

        return result;
    }

    return (
        <div className="san-viewer">
            {
                getSanMoves().map((move, index) => {
                        return <span key={index}>
                                        {index % 2 === 0 && <span>{((index / 2) + 1) + '. '}</span>}
                            <a key={index} href="#" className="move" onClick={() => onPositionChange(index + 1)}>{move}</a>
                                        <span> </span>
                                    </span>
                    }
                )}
        </div>
    );
}