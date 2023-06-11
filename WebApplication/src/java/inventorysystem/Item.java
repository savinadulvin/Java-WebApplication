package inventorysystem;

import java.time.LocalDateTime;

public class Item 
{
    public int ID;
    public String Name;
    public float Price;
    public int Quantity;
    public String EmpName;
    public LocalDateTime DateCreated;
    private LocalDateTime dateAdded;
    
    public int getID()
    {
    	return ID;
    }
    
    public String getName()
    {
    	return Name;
    }
    
    public int getQuantity()
    {
    	return Quantity;
    }
    
    public String getEmpName()
    {
    	return EmpName;
    }
    
    public Float getPrice() {
        return Price;
    }
    
    public LocalDateTime getDateCreated()
    {
    	return DateCreated;
    }
    
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }
    
    public void setQuantity(int quantity)
    {
    	this.Quantity = quantity;
    }
    
    public Item(int id, String name, int quantity, String empName, float price, LocalDateTime dateCreated)
    {
        this.ID = id;
        this.Name = name;
        this.Quantity = quantity;
        this.EmpName = empName;
        this.Price = price;
        this.DateCreated = dateCreated;
    }
}

//
//public class Item 
//{
//    private int ID;
//    private String Name;
//    private int Quantity;
//    private String EmpName;
//    private float Price;
//    private float UpdatedPrice;
//    private LocalDateTime DateCreated;
//    private LocalDateTime DateUpdated;
//    private String UpdatedBy;
//    
//    // getter methods for the Item class
//    public int getID() {
//        return ID;
//    }
//
//    public String getName() {
//        return Name;
//    }
//
//    public int getQuantity() {
//        return Quantity;
//    }
//
//    public String getEmpName() {
//        return EmpName;
//    }
//    
//    public Float getPrice() {
//        return Price;
//    }
//    
//    public Float getUpdatedPrice() {
//        return UpdatedPrice;
//    }
//
//    public LocalDateTime getDateCreated() {
//        return DateCreated;
//    }
//    
//    public LocalDateTime getDateUpdatedted() {
//        return DateUpdated;
//    }
//    
//    public String getUpdatedBy() {
//        return UpdatedBy;
//    }

//   public void setQuantity(int quantity)
//    {
//    	this.Quantity = quantity;
//    }
//    
//    public Item(int id, String name, int quantity, String empName, float price, float updatedPrice,LocalDateTime dateCreated, LocalDateTime dateUpdated, String updatedBy)
//    {
//        this.ID = id;
//        this.Name = name;
//        this.Quantity = quantity;
//        this.EmpName = empName;
//        this.Price= price;
//        this.UpdatedPrice = updatedPrice;
//        this.DateCreated = dateCreated;
//        this.DateUpdated = dateUpdated;
//        this.UpdatedBy = updatedBy;
//    }
//}  
//    public void setQuantity(int quantity)
//    {
//    	this.Quantity = quantity;
//    }
//    
//    public Item(int id, String name, int quantity, String empName, float price, float updatedPrice,LocalDateTime dateCreated, LocalDateTime dateUpdated, String updatedBy)
//    {
//        this.ID = id;
//        this.Name = name;
//        this.Quantity = quantity;
//        this.EmpName = empName;
//        this.Price= price;
//        this.UpdatedPrice = updatedPrice;
//        this.DateCreated = dateCreated;
//        this.DateUpdated = dateUpdated;
//        this.UpdatedBy = updatedBy;
//    }
//}