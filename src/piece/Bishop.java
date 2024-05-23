package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        setType(Type.BISHOP);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-bishop"));

        } else {
            setImage(getImage("/piece/b-bishop"));

        }
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){

            if(Math.abs(targetCol - getPreCol()) == Math.abs(targetRow - getPreRow())){
                if(isValidSquare(targetCol, targetRow) && !isOnDiagonalLine(targetCol, targetRow)){
                    return true;
                }
            }
        }

        return false;
    }
}
