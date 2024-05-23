package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{

    public Rook(int color, int col, int row) {
        super(color, col, row);

        setType(Type.ROOK);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-rook"));

        } else {
            setImage(getImage("/piece/b-rook"));

        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            // Rook can move as long as either its col or row is the same
            if(targetCol == getPreCol() || targetRow == getPreRow()){
                if(isValidSquare(targetCol, targetRow) && !isOnStraightLine(targetCol, targetRow)){
                    return true;
                }
            }
        }

        return false;
    }
}
