package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        setType(Type.PAWN);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-pawn"));

        } else {
            setImage(getImage("/piece/b-pawn"));

        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol,targetRow) && !isSameSquare(targetCol, targetRow)){

            // Define the move value based on its color
            int moveValue;
            if(getColor() == GamePanel.WHITE){
                moveValue = -1;
            } else {
                moveValue = 1;
            }

            // Check the hitting piece
            setHittingPiece(getHittingPiece(targetCol, targetRow));

            // 1 Square movement
            if(targetCol == getPreCol() && targetRow == getPreRow() + moveValue && getHittingPiece() == null){
                return true;
            }

            // 2 Squares movement
            if(targetCol == getPreCol() && targetRow == getPreRow() + moveValue*2 && getHittingPiece() == null && !isMoved() &&
                    !isOnStraightLine(targetCol, targetRow)){
                return true;
            }

            // Diagonal movement & capture (if a piece is on a square diagonally in front of it)
            if(Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + moveValue && getHittingPiece() != null &&
                    getHittingPiece().getColor() != getColor()){
                return true;

            }

            // EN PASSANT
            if(Math.abs(targetCol - getPreCol()) == 1 && targetRow == getPreRow() + moveValue) {
                for(Piece piece : GamePanel.simPieces){
                    if(piece.getCol() == targetCol && piece.getRow() == getPreRow() && piece.isTwoStepped()){
                        setHittingPiece(piece);
                        return true;
                    }
                }
            }



        }

        return false;
    }
}
