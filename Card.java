public class Card 
{

    private String owner; //gives an owner to the player who plays the card (so they never have to beat their own hand)
    private Ranks rank;
    private Suits suit;
    private int cornerX;
    private int cornerY;
    private boolean selected;


    public void setOwner(String player)
    {
        this.owner = player;
    }
    
    public String getOwner()
    {
        return this.owner;
    }
    
    public Card(Ranks rank, Suits suit) 
    {
        this.owner = "";
        this.rank = rank;
        this.suit = suit;
    }

    public Ranks getRank()
    {
        return this.rank;
    }

    public Suits getSuit()
    {
        return this.suit;
    }

    public int getX()
    {
        return this.cornerX;
    }

    public int getY()
    {
        return this.cornerY;
    }
    
    public void setX(int x)
    {
        this.cornerX = x;
    }

    public void setY(int y)
    {
        this.cornerY = y;
    }
    
     public boolean isSelected()
    {
        return this.selected;
    }
    
    public void select()
    {
        this.selected = true;
    }
    
    public void deselect()
    {
        this.selected = false;
    }

}
