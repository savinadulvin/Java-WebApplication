package inventorysystem;

import java.time.LocalDateTime;

public class TransactionLog 
{
	public String TypeOfTransaction;
    public int ItemID;
    public String ItemName;
    public float ItemPrice;
    public int ItemQuantity;
    public String EmployeeName;
    public LocalDateTime DateAdded;
    
    public String getTypeOfTransaction()
    {
    	return TypeOfTransaction;
    }

    public int getItemID()
    {
    	return ItemID;
    }
    
    public String getItemName()
    {
    	return ItemName;
    }
    
    public float getItemPrice()
    {
    	return ItemPrice;
    }
    
    public int getItemQuantity()
    {
    	return ItemQuantity;
    }
    
    public String getEmployeeName()
    {
    	return EmployeeName;
    }
    
    public LocalDateTime getDateAdded()
    {
    	return DateAdded;
    }
    
    public TransactionLog(String type, Item i, float itemPrice, LocalDateTime dateAdded)
    {
        this.TypeOfTransaction = type;
        this.ItemID = i.ID;
        this.ItemName = i.Name;
        this.ItemPrice = itemPrice;
        this.ItemQuantity = i.Quantity;
        this.EmployeeName = i.EmpName;
        this.DateAdded = dateAdded;
    }

}
