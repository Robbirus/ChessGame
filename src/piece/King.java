package piece;

import main.GamePanel;
import main.Main;
import main.Type;

public class King extends Piece{

    public King(int color, int col, int row) {
        super(color, col, row);

        setType(Type.KING);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-king"));

        } else {
            setImage(getImage("/piece/b-king"));

        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow)){

            if(Math.abs(targetCol - getPreCol()) + Math.abs(targetRow - getPreRow()) == 1 ||
                    Math.abs(targetCol - getPreCol()) * Math.abs(targetRow - getPreRow()) == 1){

                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }

            // CASTLING
            if(!isMoved()){

                // Right Castling
                if(targetCol == getPreCol() + 2 && targetRow == getPreRow() && !isOnStraightLine(targetCol, targetRow)){

                    for(Piece piece : GamePanel.simPieces){
                        if(piece.getCol() == getPreCol() + 3 && piece.getRow() == getPreRow() && !piece.isMoved()){
                            GamePanel.castlingPiece = piece;
                            return true;
                        }
                    }
                }

                // Left Castling
                if(targetCol == getPreCol() - 2 && targetRow == getPreRow() && !isOnStraightLine(targetCol, targetRow)){
                    Piece[] p = new Piece[2];

                    for(Piece piece : GamePanel.simPieces){
                        if(piece.getColor() == getPreCol() - 3 && piece.getRow() == targetRow){
                            p[0] = piece;
                        }

                        if(piece.getColor() == getPreCol() - 4 && piece.getRow() == targetRow){
                            p[1] = piece;
                        }

                        if(p[0] == null && p[1] != null && !p[1].isMoved()){
                            GamePanel.castlingPiece = p[1];
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
