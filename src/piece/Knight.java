package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{

    public Knight(int color, int col, int row) {
        super(color, col, row);

        setType(Type.KNIGHT);

        if(color == GamePanel.WHITE){
            setImage(getImage("/piece/w-knight"));

        } else {
            setImage(getImage("/piece/b-knight"));

        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow)){
            // Knight can move if its movement ratio of col and row is 1:2 or 2:1
            if(Math.abs(targetCol - getPreCol()) * Math.abs(targetRow - getPreRow()) == 2){
                if(isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }

        return false;
    }
}
