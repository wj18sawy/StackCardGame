import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * Game of "Stack"
 * 
 * @author Wyatt Sawyer 
 * @version April 2018
 */
public class Game extends JFrame implements MouseListener, ActionListener
{
    public static JPanel pane;
    public ArrayList<Card> deck;
    public ArrayList<Card> discard;
    public ArrayList<Card> player1;
    public ArrayList<Card> computer1;
    public ArrayList<Card> computer2;
    public ArrayList<Card> computer3;
    public ArrayList<Card> cardsSelected;    
    public int discardCount;
    public boolean gameOver;
    public String winner;
    public final boolean DEBUGcomp = true;
    public final boolean DEBUG = true;
    public boolean compDrew;
    public boolean userDrew;
    public boolean userTurn;
    public boolean didSomething; //boolean to make sure if user presses done they at least did one action
    public static void main() throws IOException
    {
        Game g = new Game();
    }

    public Game() throws IOException
    {
        gameOver = false;
        userTurn = true;
        didSomething = false;
        compDrew = false;
        userDrew = false;
        winner = "";
        //first create the deck 
        deck = new ArrayList<Card>();
        discard = new ArrayList<Card>();
        cardsSelected = new ArrayList<Card>();
        discardCount = 1;

        for (Suits s : Suits.values()) {
            for (Ranks r : Ranks.values()) {
                if (!r.equals(Ranks.JOKER))
                {
                    Card c = new Card(r,s);
                    deck.add(c);
                }
            }  
        }
        //add two jokers in after
        Card c = new Card(Ranks.JOKER, null);
        deck.add(c);
        deck.add(c);

        // next shuffle and deal out four hands (one for the user 3 for the computers
        Collections.shuffle(deck);
        player1 = new ArrayList<Card>();
        computer1 = new ArrayList<Card>();

        for (int i = 0; i < 7; i++)
        {
            Card a = deck.remove(0);
            player1.add(a);
            a = deck.remove(0);
            computer1.add(a);
        }
        //flip the top card onto the discard pile

        discard.add(deck.remove(0));

        //gameboard picture
        BufferedImage table = ImageIO.read(new File("./pics/background1.png")); //gameboard

        //Jframe for the gameboard
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000, 1100);
        frame.setVisible(true);

        pane = new JPanel()
        {
            @Override
            /**
             * Task: overrides jPanel paint method and draws everything we need
             * @param: Graphics g is the needed instance of the graphics class to call its methods on
             */
            protected void paintComponent(Graphics g)   
            {
                super.paintComponent(g);
                g.fillRect(0,0,1920,1080);
                g.drawImage(table ,0, 0, 1920, 1080, null);

                //shows player1's hand
                int temp = 100;
                int temp2 = 100;
                for(Card c : player1)
                {
                    String currentName = cardName(c);
                    try
                    {
                        BufferedImage currentCard;
                        if (c.isSelected())
                        {
                            currentCard = fade(ImageIO.read(new File(currentName)));
                        }
                        else
                        {
                            currentCard = ImageIO.read(new File(currentName));
                        }
                        if(player1.indexOf(c)<=16)
                        {
                            g.drawImage(currentCard,temp,820,100,150, null);
                            c.setX(temp);
                            c.setY(820);
                            temp+=102;
                        }
                        else if (player1.indexOf(c)<=33)
                        {

                            g.drawImage(currentCard,temp2,850,100,150, null);
                            c.setX(temp2);
                            c.setY(850);
                            temp2+=102;
                        }
                    }
                    catch (IOException e)
                    {
                        System.err.println(e);
                        System.err.println(currentName);
                    }   

                }

                //show computer1's hand
                int temp1 = 100;
                for(Card c : computer1)
                {
                    String currentName = cardName(c);
                    try
                    {
                        BufferedImage currentCard = ImageIO.read(new File(currentName));
                        g.drawImage(currentCard,temp1,100,100,150, null);
                        c.setX(temp1);
                        c.setY(100);
                    }
                    catch (IOException e)
                    {
                        System.err.println(e);
                    }   
                    temp1+=102;
                }

                //show the deck 

                try
                {
                    String currentName = "./pics/backoption1.png";
                    BufferedImage currentCard = ImageIO.read(new File(currentName));
                    g.drawImage(currentCard,1000,450,100,150, null);
                }
                catch (IOException e)
                {
                    System.err.println(e);
                }   

                //show the discard pile
                if (discard.size() != 0)
                {
                    try
                    {
                        Card c = discard.get(discard.size()-1);
                        String currentName = cardName(c);
                        BufferedImage currentCard = ImageIO.read(new File(currentName));
                        g.drawImage(currentCard,800,450,100,150, null);
                    }
                    catch (IOException e)
                    {
                        System.err.println(e);
                    }   
                }
                if((discardCount>1)&&!gameOver)//displays the number of cards from the last hand played (if no number then it's just one)
                {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(890,435, 30, 30, true);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 23)); 
                    g.setColor(Color.red);
                    g.drawString("x" + discardCount, 892,460);
                }

                if(!gameOver)
                {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(475,455, 250, 100, true);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 20)); 
                    g.setColor(Color.black);
                    g.drawString("Last hand played: ", 478,475);
                    if(discard.size() >= 2) //catches the out of bounds exception
                    {
                        if(discard.get(discard.size()-(1+discardCount)).getRank() == Ranks.TWO || discard.get(discard.size()-(1+discardCount)).getRank() == Ranks.JOKER)//if the deck was cleared beforehand
                        {
                            g.drawString("A " + discard.get(discard.size()-(1+discardCount)).getRank() + " &", 478,495);
                            g.drawString(discardCount + " " + discard.get(discard.size()-1).getRank() + "(s)", 478,515);
                        }
                        else
                        {
                            g.drawString(discardCount + " " + discard.get(discard.size()-1).getRank() + "(s)", 478,495);
                        }
                    }
                    else
                    {
                        g.drawString(discardCount + " " + discard.get(discard.size()-1).getRank() + "(s)", 478,495);
                    }
                    if(!discard.get(discard.size()-1).getOwner().equals(""))
                    {
                        g.drawString("by " + discard.get(discard.size()-1).getOwner(), 478,535);
                    }
                }
                else
                {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(475,455, 250, 100, true);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 20)); 
                    g.setColor(Color.black);
                    g.drawString("Game Over!", 478,475);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 12)); 
                    g.setColor(Color.black);
                    String lastHand = "";
                    System.out.println(discardCount);
                    for(int i = 1; i <=discardCount; i++)
                    {
                        lastHand = lastHand + discard.get(discard.size()-i).getRank() + ", ";
                    }
                    g.drawString("Winning hand: " + lastHand.substring(0,(lastHand.length()-2)),478,495);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 20)); 
                    g.setColor(Color.black);
                    g.drawString(winner + " won!", 478,550);


                    JButton button3 = new JButton("New Game");
                    pane.setLayout(null);
                    button3.setBounds(890, 710, 120, 40);
                    if(gameOver)pane.add(button3);
                    //now an actionlistener is added to see when the user is ready to play their hand 
                    button3.addActionListener(new ActionListener()  { 
                            public void actionPerformed(ActionEvent e) { 

                                try
                                {
                                    frame.setVisible(false);

                                    main();

                                }
                                catch(IOException c)
                                {
                                }
                            } 
                        } );        
                    // 
                    //                     JButton button4 = new JButton("Exit Game");
                    //                     pane.setLayout(null);
                    //                     button4.setBounds(890, 760, 120, 40);
                    //                     if(gameOver)pane.add(button4);
                    //                     //now an actionlistener is added to see when the user is ready to play their hand 
                    //                     button3.addActionListener(new ActionListener()  { 
                    //                             public void actionPerformed(ActionEvent e) { 
                    //                                 frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    // 
                    //                             } 
                    //                         } );        
                }

            }
        };

        pane.addMouseListener(this);
        frame.add(pane);
        frame.setVisible(true);

        JLabel label = new JLabel("x" + discardCount);
        label.setFont(new Font("Verdana",1,20));
        pane.add(label);

        JButton button = new JButton("Play card(s)");
        pane.setLayout(null);
        button.setBounds(890, 610, 120, 40);
        pane.add(button);
        //now an actionlistener is added to see when the user is ready to play their hand 
        button.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) {
                    if (userTurn){

                        if((cardsSelected.size() >= discardCount) || discard.get(discard.size()-1).getOwner().equals("player1") || cardsSelected.get(0).getRank() == Ranks.JOKER || cardsSelected.get(0).getRank() == Ranks.TWO )
                        {

                            for( Card c : cardsSelected)
                            {
                                player1.remove(c);
                                discard.add(c);
                                c.setOwner("player1");

                            }
                            didSomething = true;
                            discardCount = cardsSelected.size();
                            if(((cardsSelected.get(0).getRank() == Ranks.TWO)|| (cardsSelected.get(0).getRank() == Ranks.JOKER))&& player1.size()>0) //if played a two or joker they get another turn
                            {
                                cardsSelected.clear();
                                userDrew = false;
                                pane.repaint();
                            }
                            else
                            {
                                if(player1.size() == 0)//if player won the game
                                {
                                    userTurn = false;
                                    winner = "Player1";
                                    gameOver = true;
                                    pane.repaint();
                                }
                                else
                                {
                                    userTurn = false;
                                    userDrew = false;
                                    pane.repaint();
                                }
                            }

                        }                  
                    }
                } 
            } );

        JButton button2 = new JButton("Done");
        pane.setLayout(null);
        button2.setBounds(890, 660, 120, 40);

        pane.add(button2);
        //now an actionlistener is added to see when the user is ready to play their hand 
        button2.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e) { 
                    if(didSomething && !gameOver)
                    {

                        try{
                            userTurn = false;
                            userDrew = false;
                            didSomething = false;
                            cardsSelected.clear();
                            compTurn();
                        }
                        catch (Exception error)
                        {
                            System.err.println(error);
                        }

                    }
                    
                } 
            } );
    }

    public void compTurn() throws Exception//carries out the act of computers turn if called 
    {
        if(DEBUGcomp)System.out.println("Computer's Turn");
        Card topCard = discard.get(discard.size()-1);
        if(DEBUGcomp)System.out.println("This is the top card to beat is " + topCard.getRank() + " and there are/is " + discardCount + " of them");
        Card low = null;
        ArrayList<Card> selected = new ArrayList<Card>();
        if(DEBUGcomp)System.out.println("Going through computer's hand");

        //A hashmap is created to see which cards in the hands have multiples
        HashMap<Ranks, Integer> multiples= new HashMap<Ranks, Integer>();
        for ( Card c : computer1)
        {
            if((multiples.get(c.getRank())!=null)) //if this card has already been seen
            {
                multiples.put(c.getRank(), multiples.get(c.getRank())+1);                
            }
            else
            {
                multiples.put(c.getRank(), 1);                
            }   
        }

        //check to see if computer has enough 2's and jokers to play it's last hand in one turn
        int numJokers = 0;
        int numTwos = 0;
        if(multiples.get(Ranks.JOKER) != null)
            numJokers = multiples.get(Ranks.JOKER);
        if(multiples.get(Ranks.TWO) != null)
            numTwos = multiples.get(Ranks.TWO);        

        int size = multiples.size(); //tells you how many different types of cards there are
        if(numTwos>0)
        {
            size--;
        }
        if(numJokers>0)
        {
            size--;
        }        
        if(size==1 && ((numTwos + numJokers)>0))//The computers hand has only wild cards and one type of card
        {
            for ( Card d : computer1) //now go through hand and see the quantity of that card 
            {

                selected.add(d);//add cards to arraylist incase they will be played

            }
        }
        else
        {
            for ( Card c : computer1)
            {
                if(DEBUGcomp)System.out.println("Card -> " + c.getRank());
                if(((c.getRank().compareTo(topCard.getRank())>=0) || topCard.getRank() == Ranks.JOKER || topCard.getRank() == Ranks.TWO || topCard.getOwner().equals("computer1")) && (low == null || (c.getRank().compareTo(low.getRank())<0)))//if the card in hand is greater than the topcard but is lower than any other card already seen
                {
                    if(DEBUGcomp)System.out.println("Card was greater than the top card");
                    if (multiples.get(c.getRank())>=discardCount || c.getRank() == Ranks.JOKER || c.getRank() == Ranks.TWO || topCard.getOwner().equals("computer1"))
                    {
                        if(DEBUGcomp)System.out.println("Amount was equal to or greater (or a two or joker is being played)");
                        low = c;
                        if(selected.size() != 0) //clears old selected cards
                        {
                            if(DEBUGcomp)System.out.println("Clearing computer's previously selected cards");
                            selected.clear();
                        }
                        for ( Card d : computer1) //now go through hand and see the quantity of that card 
                        {
                            if(d.getRank() == c.getRank())
                            {
                                selected.add(d);//add cards to arraylist incase they will be played
                            }
                        }

                    }
                }
            }
        }

        if(selected.size() > 0) //if cards can be played
        {

            if(DEBUGcomp)System.out.println("Hand being played is " + selected.size() + " " + selected.get(0).getRank() + "(s)");
            if(DEBUGcomp)System.out.println("Cards in selected:");
            for( Card c : selected)
            {
                if(DEBUGcomp)System.out.println(c.getRank());
                computer1.remove(c);
                discard.add(c);
                c.setOwner("computer1");
                pane.repaint();

            }
            discardCount = selected.size();
            pane.repaint();
            if(computer1.size() == 0)//if computer won the game
            {
                userTurn = false;
                winner = "Computer1";
                gameOver = true;
                pane.repaint();
            }
            else
            {
                userTurn = true;
            }

            if(((selected.get(0).getRank() == Ranks.TWO)|| (selected.get(0).getRank() == Ranks.JOKER))&&!gameOver) //if computer played a two or joker they get another turn
            {
                if(DEBUGcomp)System.out.println("Discard pile is reset..");
                selected.clear();
                compTurn();
            }

        }  
        else
        {
            if(!compDrew)
            {
                if(DEBUGcomp)System.out.println("No card can be played");
                compDrew = true;
                Card c = deck.remove(0);
                if(DEBUGcomp)System.out.println("Computer drew a " + c.getRank());
                computer1.add(c);

                pane.repaint();
                Thread.sleep(1);
                compTurn();
            }
            else
            {
                compDrew = false;
                userTurn = true;
                if(DEBUGcomp)System.out.println("Computer turn over...");
                pane.repaint();
            }

        }

    }

    /**
     * Makes images translucent 
     * 
     * @param image BufferedImage that user wants to make translucent
     */
    public static BufferedImage fade(BufferedImage image) {
        double amount = 0.65;
        BufferedImage newImage = new BufferedImage(image.getWidth(),image.getHeight(), java.awt.Transparency.TRANSLUCENT);
        Graphics2D g = newImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) amount));
        g.drawImage(image, null, 0, 0);
        g.dispose();
        return newImage;
    }

    public String cardName(Card c)
    {
        String temp = "./pics/" + c.getRank() + "_of_" + c.getSuit() + ".png";
        return temp;
    }

    public void handPrint()
    {
        for (Card b : player1)
        {
            System.out.println(b.getRank() + " " + b.getSuit());
        }
        System.out.println();
        for (Card b : computer1)
        {
            System.out.println(b.getRank() + " " + b.getSuit());
        }
        System.out.println();        
        for (Card b : computer2)
        {
            System.out.println(b.getRank() + " " + b.getSuit());
        }
        System.out.println();        
        for (Card b : computer3)
        {
            System.out.println(b.getRank() + " " + b.getSuit());
        }

    }

    /**
     * Task: method to handle what happens when mouse is clicked
     */
    public void mousePressed(MouseEvent e){

        //traverses through card locations to see what card the user clicked if any
        //because player can only click on their hand or the deck a data structure for all of the locations like a hash map is unnecessary 
        //if the user trys to draw a card from the deck

        if (userTurn && !gameOver)
        {
            if((e.getX()>=1000&& e.getX()<=1100 && e.getY()>=450 && e.getY()<=600)&&(!userDrew))//if user draws a card
            {
                Card c = deck.remove(0);
                player1.add(c);
                didSomething = true;
                userDrew = true;
                pane.repaint();
            }
            Card choice = null;
            for(Card c : player1)
            {           
                if(e.getX()>=c.getX()&& e.getX()<=c.getX()+100 && e.getY()>=c.getY() && e.getY()<=c.getY()+150)
                {
                    Card top = discard.get(discard.size()-1);
                    if((c.getRank().compareTo(top.getRank())>=0) || (top.getRank().equals(Ranks.JOKER)) || (top.getRank().equals(Ranks.TWO)) ||  (top.getOwner().equals("player1")) ) //if card(s) can beat lasts hand played
                    {
                        choice = c; 
                    }
                }

            }
            if (choice != null) //if user did click on a card
            {
                if(cardsSelected.size()==0)//if user is choosing the first card
                {
                    //only selects card if it's able to beat top of the discard pile  
                    if(!choice.isSelected())
                    {
                        choice.select(); 
                        pane.repaint();
                        cardsSelected.add(choice); 
                    }
                    else
                    {
                        choice.deselect(); 
                        pane.repaint();
                        cardsSelected.remove(choice); 

                    }

                }
                else //if another card is already selected
                {
                    Ranks r = cardsSelected.get(0).getRank();
                    if(choice.getRank() == r) //if card is pair with other card(s) selected
                    {
                        if(!choice.isSelected())
                        {
                            choice.select(); 
                            pane.repaint();
                            cardsSelected.add(choice); 
                        }
                        else
                        {
                            choice.deselect(); 
                            pane.repaint();
                            cardsSelected.remove(choice); 

                        }  
                    }

                }

            }

        }
        if(DEBUG)
        {
            System.out.println("Location of click: " + e.getX() + "x " + e.getY() + "y");
            System.out.print("Cards Selected: ");
            for(Card c : cardsSelected)
            {
                System.out.print(c.getRank() + " of " + c.getSuit() + " | ");
            }
            System.out.println();
            System.out.println("Last hand played: " + discardCount + " " + discard.get(discard.size()-1).getRank() + "(s)");
        }
    }

    /**
     * Task: method to handle what happens when mouse is clicked
     */
    public void mouseClicked(MouseEvent e){}

    /**
     * Task: method to handle what happens when mouse is created
     */
    public void mouseEntered(MouseEvent e){}

    /**
     * Task: method to handle what happens when mouse is released
     */
    public void mouseReleased(MouseEvent e){}

    /**
     * Task: method to handle what happens when mouse is destroyed
     */
    public void mouseExited(MouseEvent e){}

    /**
     * Task: method to handle what happens when the action event is triggered
     */
    public void actionPerformed(ActionEvent e){} 
}
