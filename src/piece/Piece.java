package piece;

import main.Board;
import main.GamePanel;
import main.Type;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {

    private Type type;
    private BufferedImage image;
    private int x;
    private int y;
    private int col;
    private int row;
    private int preCol;
    private int preRow;
    private int color;
    private Piece hittingPiece;
    private boolean moved;
    private boolean twoStepped;

    public Piece(int color, int col, int row){

        this.color = color;
        this.col = col;
        this.row = row;
        this.x = getX(col);
        this.y = getY(row);
        this.preCol = col;
        this.preRow = row;
    }

    public BufferedImage getImage(String imagePath){

        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));

        } catch (IOException e){
            e.printStackTrace();
        }

        return image;
    }

    public void updatePosition(){

        // To check EN PASSANT
        if(type == Type.PAWN){
            if(Math.abs(row - preRow) == 2){
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow){
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7){
            return true;
        }

        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }

    public Piece getHittingPiece(int targetCol, int targetRow){
        for(Piece piece : GamePanel.simPieces){
            if(piece.getCol() == targetCol && piece.getRow() == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){

        hittingPiece = getHittingPiece(targetCol, targetRow);

        if(hittingPiece == null){ // This square is VACANT
            return true;

        } else { // This square is OCCUPIED
            if(hittingPiece.getColor() != this.getColor()){ // If the color is different, it can be captured
                return true;

            } else {
                hittingPiece = null;
            }
        }

        return false;
    }

    public boolean isOnStraightLine(int targetCol, int targetRow){

        // When this piece is moving to the left
        for(int c = preCol-1; c > targetCol; c--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // When this piece is moving to the right
        for(int c = preCol+1; c < targetCol; c++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == c && piece.row == targetRow){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // When this piece is moving up
        for(int r = preRow-1; r > targetRow; r--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == r){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // When this piece is moving down
        for(int r = preRow+1; r < targetRow; r++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == r){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isOnDiagonalLine(int targetCol, int targetRow){

        // UP
        if(targetRow < preRow) {

            // UP LEFT
            for(int c = preCol - 1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // UP RIGHT
            for(int c = preCol + 1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow - diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }

        // DOWN
        if(targetRow > preRow) {

            // DOWN LEFT
            for(int c = preCol - 1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // DOWN RIGHT
            for(int c = preCol + 1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == c && piece.row == preRow + diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getIndex(){
        for(int index = 0; index < GamePanel.simPieces.size(); index++) {
            if(GamePanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }

    public int getX(int col) {

        return col * Board.SQUARE_SIZE;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY(int row) {

        return row * Board.SQUARE_SIZE;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getCol(int x) {

        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow(int y) {

        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getPreCol() {
        return preCol;
    }

    public void setPreCol(int preCol) {
        this.preCol = preCol;
    }

    public int getPreRow() {
        return preRow;
    }

    public void setPreRow(int preRow) {
        this.preRow = preRow;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Piece getHittingPiece() {
        return hittingPiece;
    }

    public void setHittingPiece(Piece hittingPiece) {
        this.hittingPiece = hittingPiece;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean isTwoStepped() {
        return twoStepped;
    }

    public void setTwoStepped(boolean twoStepped) {
        this.twoStepped = twoStepped;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
