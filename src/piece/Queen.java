package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{

    public Queen(int color, int col, int row) {
        super(color, col, row);

        setType(Type.QUEEN);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-queen"));

        } else {
            setImage(getImage("/piece/b-queen"));

        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){

            // Vertical & Horizontal
            if(targetCol == getPreCol() || targetRow == getPreRow()){
                if(isValidSquare(targetCol, targetRow) && !isOnStraightLine(targetCol, targetRow)){
                    return true;
                }
            }

            // Diagonal
            if(Math.abs(targetCol - getPreCol()) == Math.abs(targetRow - getPreRow())){
                if(isValidSquare(targetCol, targetRow) && !isOnDiagonalLine(targetCol, targetRow)){
                    return true;
                }
            }
        }

        return false;
    }
}
