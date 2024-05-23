package main;

import piece.*;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{

    public static final int WIDTH  = 1100;
    public static final int HEIGHT = 800;
    private static final int FPS = 60;
    private Thread gameThread;
    private Board board = new Board();
    private Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    private ArrayList<Piece> promoPieces = new ArrayList<>();
    private Piece activePiece;
    private Piece checkingPiece;
    public static Piece castlingPiece;

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    private int currentColor = WHITE;

    // BOOLEANS
    private boolean canMove;
    private boolean validSquare;
    private boolean promotion;
    private boolean gameover;
    private boolean stalemate;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();

    }

    public void setPieces(){

        // White Piece Team
        pieces.add(new Pawn(WHITE, 0,6));
        pieces.add(new Pawn(WHITE, 1,6));
        pieces.add(new Pawn(WHITE, 2,6));
        pieces.add(new Pawn(WHITE, 3,6));
        pieces.add(new Pawn(WHITE, 4,6));
        pieces.add(new Pawn(WHITE, 5,6));
        pieces.add(new Pawn(WHITE, 6,6));
        pieces.add(new Pawn(WHITE, 7,6));
        pieces.add(new Rook(WHITE, 0,7));
        pieces.add(new Rook(WHITE, 7,7));
        pieces.add(new Knight(WHITE, 1,7));
        pieces.add(new Knight(WHITE, 6,7));
        pieces.add(new Bishop(WHITE, 2,7));
        pieces.add(new Bishop(WHITE, 5,7));
        pieces.add(new Queen(WHITE, 3,7));
        pieces.add(new King(WHITE, 4,7));


        // Black Piece Team
        pieces.add(new Pawn(BLACK, 0,1));
        pieces.add(new Pawn(BLACK, 1,1));
        pieces.add(new Pawn(BLACK, 2,1));
        pieces.add(new Pawn(BLACK, 3,1));
        pieces.add(new Pawn(BLACK, 4,1));
        pieces.add(new Pawn(BLACK, 5,1));
        pieces.add(new Pawn(BLACK, 6,1));
        pieces.add(new Pawn(BLACK, 7,1));
        pieces.add(new Rook(BLACK, 0,0));
        pieces.add(new Rook(BLACK, 7,0));
        pieces.add(new Knight(BLACK, 1,0));
        pieces.add(new Knight(BLACK, 6,0));
        pieces.add(new Bishop(BLACK, 2,0));
        pieces.add(new Bishop(BLACK, 5,0));
        pieces.add(new Queen(BLACK, 3,0));
        pieces.add(new King(BLACK, 4,0));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){

        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {

        // GAME LOOP
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update(){

        if(promotion){
            promoting();

        } else if(!gameover && !stalemate) {

            //// MOUSE BUTTON PRESSED ////
            if (mouse.isPressed()) {
                if (activePiece == null) {
                    // If the activeP is null check if you can pick up a piece
                    for (Piece piece : simPieces) {
                        // If the mouse is on an ally piece, pick it up as the activePiece
                        if (piece.getColor() == currentColor &&
                                piece.getCol() == mouse.getX() / Board.SQUARE_SIZE &&
                                piece.getRow() == mouse.getY() / Board.SQUARE_SIZE) {

                            activePiece = piece;
                        }
                    }
                } else {
                    // If the player is holding a piece, simulate the move
                    simulate();
                }
            }


            //// MOUSE BUTTON RELEASED ////
            if (!mouse.isPressed()) {

                if (activePiece != null) {

                    if (validSquare) {

                        // MOVE CONFIRMED //

                        // Update the piece list in case a piece has been captured and removed during the simulation
                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();

                        if (castlingPiece != null) {
                            castlingPiece.updatePosition();
                        }

                        if(isKingInCheck() && isCheckMate()){
                            gameover = true;

                        } else if(isStalemate() && !isKingInCheck()){
                            stalemate = true;

                        } else {
                            // The game is still going on
                            if (canPromote()) {
                                promotion = true;

                            } else {
                                changePlayer();

                            }
                        }

                    } else {
                        // The move is not valid so reset everything
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }
    }

    private boolean isKingInCheck(){

        Piece king = getKing(true);

        if(activePiece.canMove(king.getCol(), king.getRow())){
            checkingPiece = activePiece;
            return true;

        } else {
            checkingPiece = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent){

        Piece king = null;

        for(Piece piece : simPieces){
            if(opponent){
                if(piece.getType() == Type.KING && piece.getColor() != currentColor){
                    king = piece;
                }

            } else {
                if(piece.getType() == Type.KING && piece.getColor() == currentColor){
                    king = piece;
                }
            }
        }

        return king;
    }

    private void simulate(){

        canMove = false;
        validSquare = false;

        // Reset the piece list in every loop
        // This is basically for restoring the removed piece during the simulation
        copyPieces(pieces, simPieces);

        // Reset the castling piece's position
        if(castlingPiece != null){
            castlingPiece.setCol(castlingPiece.getPreCol());
            castlingPiece.setX(castlingPiece.getX(castlingPiece.getCol()));
            castlingPiece = null;
        }

        // If a piece is being held, update its position
        activePiece.setX(mouse.getX() - Board.HALF_SQUARE_SIZE);
        activePiece.setY(mouse.getY() - Board.HALF_SQUARE_SIZE);

        activePiece.setCol(activePiece.getCol(activePiece.getX()));
        activePiece.setRow(activePiece.getRow(activePiece.getY()));

        // Check if the piece is hovering over a reachable square
        if(activePiece.canMove(activePiece.getCol(), activePiece.getRow())){

            canMove = true;

            // If hitting a piece, remove it from the list
            if(activePiece.getHittingPiece() != null){
                simPieces.remove(activePiece.getHittingPiece().getIndex());
            }

            checkCastling();

            if(!isIllegal(activePiece) && !opponentCanCaptureKing()){
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king){

        if(king.getType() == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.getColor() != king.getColor() && piece.canMove(king.getCol(), king.getRow())){
                    return true;
                }
            }
        }

        return false;
    }

    private boolean opponentCanCaptureKing(){

        Piece king = getKing(false);

        for(Piece piece : simPieces){
            if(piece.getColor() != king.getColor() && piece.canMove(king.getCol(), king.getRow())){
                return true;
            }
        }

        return false;
    }

    private boolean isCheckMate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;

        } else {
            // But you still have a chance
            // Check if you can block the attack with your piece

            // Check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingPiece.getCol() - king.getCol());
            int rowDiff = Math.abs(checkingPiece.getRow() - king.getRow());

            if(colDiff == 0){
                // The checking piece is attacking vertically
                if(checkingPiece.getRow() < king.getRow()){
                    // The checking piece is above the king
                    for(int row = checkingPiece.getRow(); row < king.getRow(); row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.getColor() != currentColor && piece.canMove(checkingPiece.getCol(), row)){
                                return false;
                            }
                        }
                    }
                }

                if(checkingPiece.getRow() > king.getRow()){
                    // The checking piece is below the king
                    for(int row = checkingPiece.getRow(); row > king.getRow(); row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.getColor() != currentColor && piece.canMove(checkingPiece.getCol(), row)){
                                return false;
                            }
                        }
                    }
                }

            } else if(rowDiff == 0){
                // The checking piece is attacking horizontally
                if(checkingPiece.getCol() < king.getCol()){
                    // The checking piece is to the left
                    for(int col = checkingPiece.getCol(); col < king.getCol(); col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.getColor() != currentColor && piece.canMove(col, checkingPiece.getRow())){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.getCol() > king.getCol()){
                    // The checking piece is to the right
                    for(int col = checkingPiece.getCol(); col > king.getCol(); col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.getColor() != currentColor && piece.canMove(col, checkingPiece.getRow())){
                                return false;
                            }
                        }
                    }
                }

            } else if(colDiff == rowDiff) {
                // The checking piece is attacking diagonally
                if(checkingPiece.getRow() < king.getRow()){
                    // The checking is above the king
                    if(checkingPiece.getCol() < king.getCol()){
                        // The checking piece is upper left
                        for(int col = checkingPiece.getCol(), row = checkingPiece.getRow(); col < king.getCol(); col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.getColor() != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingPiece.getCol() > king.getCol()){
                        // The checking piece is upper right
                        for(int col = checkingPiece.getCol(), row = checkingPiece.getRow(); col > king.getCol(); col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.getColor() != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingPiece.getRow() > king.getRow()){
                    // The checking is below the king
                    if(checkingPiece.getCol() < king.getCol()){
                        // The checking piece is lower left
                        for(int col = checkingPiece.getCol(), row = checkingPiece.getRow(); col < king.getCol(); col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.getColor() != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingPiece.getCol() > king.getCol()){
                        // The checking piece is lower right
                        for(int col = checkingPiece.getCol(), row = checkingPiece.getRow(); col > king.getCol(); col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.getColor() != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean kingCanMove(Piece king){

        // Simulate if there is any square where the king can move to
        if(isValidMove(king, -1, -1)) { return true; }
        if(isValidMove(king, 0, -1)) { return true; }
        if(isValidMove(king, 1, -1)) { return true; }
        if(isValidMove(king, -1, 0)) { return true; }
        if(isValidMove(king, -1, 1)) { return true; }
        if(isValidMove(king, 0, 1)) { return true; }
        if(isValidMove(king, 1, 1)) { return true; }

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus){

        boolean isValidMove = false;

        // Update the king's position for a second
        king.setCol(king.getCol() + colPlus);
        king.setRow(king.getRow() + rowPlus);

        if(king.canMove(king.getCol(), king.getRow())){

            if(king.getHittingPiece() != null){
                simPieces.remove(king.getHittingPiece().getIndex());
            }
            if(!isIllegal(king)){
                isValidMove = true;
            }
        }

        // Reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate(){

        int count = 0;
        // Count the number of pieces
        for(Piece piece : simPieces){
            if(piece.getColor() != currentColor){
                count++;
            }
        }

        // If only one piece (the king) is left
        if(count == 1){
            if(!kingCanMove(getKing(true))){
                return true;
            }
        }

        return false;
    }

    private void checkCastling(){

        if(castlingPiece != null){
            if(castlingPiece.getCol() == 0){
                castlingPiece.setCol(castlingPiece.getCol() + 3);

            } else if(castlingPiece.getCol() == 7){
                castlingPiece.setCol(castlingPiece.getCol() - 2);

            }

            castlingPiece.setX(castlingPiece.getX(castlingPiece.getCol()));
        }
    }

    private void changePlayer(){

        if(currentColor == WHITE){
            currentColor = BLACK;
            // Reset Black's two stepped status
            for(Piece piece : pieces){
                if(piece.getColor() == BLACK){
                    piece.setTwoStepped(false);
                }
            }

        } else {
            currentColor = WHITE;
            // Reset White's two stepped status
            for(Piece piece : pieces){
                if(piece.getColor() == WHITE){
                    piece.setTwoStepped(false);
                }
            }

        }

        activePiece = null;
    }

    private boolean canPromote(){

        if(activePiece.getType() == Type.PAWN){
            if(currentColor == WHITE && activePiece.getRow() == 0 || currentColor == BLACK && activePiece.getRow() == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;

            }
        }

        return false;
    }

    private void promoting(){

        if(mouse.isPressed()){
            for(Piece piece : promoPieces){
                if(piece.getCol() == mouse.getX()/Board.SQUARE_SIZE && piece.getRow() == mouse.getY()/Board.SQUARE_SIZE){
                   switch (piece.getType()){
                       case ROOK: simPieces.add(new Rook(currentColor, activePiece.getCol(), activePiece.getRow())); break;
                       case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.getCol(), activePiece.getRow())); break;
                       case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.getCol(), activePiece.getRow())); break;
                       case QUEEN: simPieces.add(new Queen(currentColor, activePiece.getCol(), activePiece.getRow())); break;
                       default: break;
                   }

                   simPieces.remove(activePiece.getIndex());
                   copyPieces(simPieces, pieces);
                   activePiece = null;
                   promotion = false;
                   changePlayer();
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // BOARD
        board.draw(g2);

        // PIECES
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activePiece != null){
            if(canMove) {
                if(isIllegal(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(Color.GRAY);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activePiece.getCol() * Board.SQUARE_SIZE, activePiece.getRow() * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                } else {
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activePiece.getCol() * Board.SQUARE_SIZE, activePiece.getRow() * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                }
            }

            // Draw the active piece in the end so it won't be hidden by board or the colored square
            activePiece.draw(g2);
        }

        // STATUS MESSAGES
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.WHITE);

        if(promotion){
            g2.drawString("Promouvoir à: ", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.getImage(), piece.getX(piece.getCol()), piece.getY(piece.getRow()),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }

        } else {

            if (currentColor == WHITE) {
                g2.drawString("Tour des blancs", 815, 550);
                if(checkingPiece != null && checkingPiece.getColor() == BLACK){
                    g2.setColor(Color.RED);
                    g2.drawString("Le Roi est", 840, 650);
                    g2.drawString("en échec !", 840, 700);
                }

            } else {
                g2.drawString("Tour des noirs", 815, 250);
                if(checkingPiece != null && checkingPiece.getColor() == WHITE) {
                    g2.setColor(Color.RED);
                    g2.drawString("Le Roi est", 840, 100);
                    g2.drawString("en échec !", 840, 150);
                }
            }
        }

        if(gameover){
            String str = "";
            if(currentColor == WHITE){
                str = "Blanc gagne";

            } else {
                str = "Noir gagne";

            }
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.GREEN);
            g2.drawString(str, 200, 420);
        }

        if(stalemate){
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Égalité", 200, 420);
        }
    }
}
