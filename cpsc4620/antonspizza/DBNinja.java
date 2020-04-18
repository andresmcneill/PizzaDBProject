package cpsc4620.antonspizza;

import java.io.*;
import java.sql.*;
import java.util.*;

/*
This file is where most of your code changes will occur
You will write the code to retrieve information from the database, or save
information to the database

The class has several hard coded static variables used for the connection, you
will need to change those to your connection information

This class also has static string variables for pickup, delivery and dine-in.
If your database stores the strings differently (i.e "pick-up" vs "pickup")
changing these static variables will ensure that the comparison is checking for
the right string in other places in the program. You will also need to use these
strings if you store this as boolean fields or an integer.


*/

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
    //enter your user name here
    private static String user = "anton";
    //enter your password here
    private static String password = "863thincrustpepperoni";
    //enter your database name here
    private static String database_name = "McNeillPizzaDB";
    //Do not change the port. 3306 is the default MySQL port
    private static String port = "3306";
    private static Connection conn;

    //Change these variables to however you record dine-in, pick-up and delivery,
    //and sizes and crusts
    public final static String dine_out = "dine_out";
    public final static String delivery = "delivery";
    public final static String dine_in = "dine_in";

    public final static String size_s = "small";
    public final static String size_m = "medium";
    public final static String size_l = "Large";
    public final static String size_xl = "X-Large";

    public final static String crust_thin = "Thin";
    public final static String crust_orig = "Original";
    public final static String crust_pan = "Pan";
    public final static String crust_gf = "Gluten-Free";



    /**
     * This function will handle the connection to the database
     * @return true if the connection was successfully made
     * @throws SQLException
     * @throws IOException
     */
    private static boolean connect_to_db() throws SQLException, IOException {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println ("Could not load the driver");

            System.out.println("Message     : " + e.getMessage());


            return false;
        }

        conn = DriverManager.getConnection("jdbc:mysql://mysql1.cs.clemson.edu:"+port+"/"+database_name, user, password);
        return true;
    }

    /**
     *
     * @param o order that needs to be saved to the database
     * @throws SQLException
     * @throws IOException
     * @requires o is not NULL. o's ID is -1, as it has not been assigned yet. The pizzas do not exist in the database
     *          yet, and the topping inventory will allow for these pizzas to be made
     * @ensures o will be assigned an id and added to the database, along with all of it's pizzas. Inventory levels
     *          will be updated appropriately
     */
    public static void addOrder(Order o) throws SQLException, IOException {
        connect_to_db();
		/* add code to add the order to the DB. Remember to add the pizzas and
        discounts as well, which will involve multiple tables. Customer should
        already exist. Toppings will need to be added to the pizzas.

		It may be beneficial to define more functions to add an individual pizza
        to a database, add a topping to a pizza, etc.

		Note: the order ID will be -1 and will need to be replaced to be a
        fitting primary key.

		You will also need to add timestamps to your pizzas/orders in your
        database. Those timestamps are not stored in this program, but you can
        get the current time before inserting into the database

		Remember, when a new order comes in the ingredient levels for the
        topping need to be adjusted accordingly. Remember to check for "extra"
        of a topping here as well.

		You do not need to check to see if you have the topping in stock before
        adding to a pizza. You can just let it go negative.
		*/
        
        String findID = "Select MAX(order_id) FROM ORDER_;";
        
        String insertO = "INSERT INTO ORDER_ (order_id,total_price) VALUES (?,?);";
        String insertDineIn = "INSERT INTO DINE_IN_ORDER (order_id,table_number) " +
        "VALUES (?,?);";
        String insertDineOut = "INSERT INTO PICKUP_ORDER (order_id,customer_id) " +
        "VALUES (?,?);";
        String insertDelivery = "INSERT INTO DELIVERY_ORDER (order_id,customer_id) " +
        "VALUES (?,?);";
        
        PreparedStatement stmt0;
        PreparedStatement stmt1;
        PreparedStatement stmt2;
        PreparedStatement stmt3;
        
        try {
            stmt0 = conn.prepareStatement(findID);
            ResultSet omax = stmt0.executeQuery();
            int newID = -1;
            
            while (omax.next()) {
                newID = omax.getInt(1);
            }
            newID++;
            o.setID(newID);
            
            stmt1 = conn.prepareStatement(insertO);
            stmt1.setInt(1,o.getID());
            stmt1.setDouble(2,o.calcPrice());
            stmt1.executeUpdate();
            
            if (o.getType().equals(DBNinja.dine_in)) {
                DineInCustomer cust = (DineInCustomer)o.getCustomer();
                stmt2 = conn.prepareStatement(insertDineIn);
                stmt2.setInt(1,o.getID());
                stmt2.setInt(2,cust.getTableNum());
                stmt2.executeUpdate();
            }
            else if (o.getType().equals(DBNinja.dine_out)) {
                stmt2 = conn.prepareStatement(insertDineOut);
                stmt2.setInt(1,o.getID());
                stmt2.setInt(2,o.getCustomer().getID());
                stmt2.executeUpdate();
            }
            else if (o.getType().equals(DBNinja.delivery)) {
                stmt2 = conn.prepareStatement(insertDelivery);
                stmt2.setInt(1,o.getID());
                stmt2.setInt(2,o.getCustomer().getID());
                stmt2.executeUpdate();
            }
            else {
                System.out.println("Order type not initialized properly");
            }
        }
        catch (SQLException e) {
            System.out.println("Error adding Order");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
        
        //  Change inventory
        for (Pizza P : o.getPizzas()) {
            for (Topping T : P.getToppings()) {
                String selectAmt;
                String psize = "";
                if (P.getSize().equals(DBNinja.size_s)) {psize = "small";}
                else if (P.getSize().equals(DBNinja.size_m)) {psize = "medium";}
                else if (P.getSize().equals(DBNinja.size_l)) {psize = "large";}
                else if (P.getSize().equals(DBNinja.size_xl)) {psize = "x_large";}
                selectAmt = "Select " + psize + " FROM TOPPING WHERE topping_id = ?;";
                try {
                    stmt3 = conn.prepareStatement(selectAmt);
                    stmt3.setInt(1,T.getID());
                    ResultSet rset = stmt3.executeQuery();
                    
                    while (rset.next()) {
                        String amt = rset.getString(psize);
                        double dbl = Double.parseDouble(amt);
                        dbl = dbl * -1;
                        if (T.getExtra()) {ChangeInventoryLevel(T,(2.0*dbl));}
                        else {ChangeInventoryLevel(T,dbl);}
                    }
                }
                catch (SQLException e) {
                    System.out.println("Error adding updating inventory");
                    while (e != null) {
                        System.out.println("Message     : " + e.getMessage());
                        e = e.getNextException();
                    }
                }
            }
        }
        
        for (Pizza P : o.getPizzas()) {
            addPizza(P,o.getID());
        }
        
        for (Discount D : o.getDiscounts()) {
            addOrderDiscount(o.getID(),D.getID());
        }
        
        conn.close();
    }

    /**
     *
     * @param c the new customer to add to the database
     * @throws SQLException
     * @throws IOException
     * @requires c is not null. C's ID is -1 and will need to be assigned
     * @ensures c is given an ID and added to the database
     */
    public static void addCustomer(ICustomer cust) throws SQLException, IOException {
        connect_to_db();
        String findID = "Select MAX(customer_id) FROM PICKUP_CUSTOMER;";
        String query1 = "INSERT INTO PICKUP_CUSTOMER (customer_id,name,phone_number) " +
        "VALUES (?,?,?);";
        String query2 = "INSERT INTO DELIVERY_CUSTOMER (customer_id,address) " +
        "VALUES (?,?);";
        Statement stmt0;
		/*add code to add the customer to the DB.
		Note: the ID will be -1 and will need to be replaced to be a fitting primary key
		Note that the customer is an ICustomer data type, which means c could be a dine in, carryout or delivery customer
		*/
        if(cust instanceof DeliveryCustomer) {
            DeliveryCustomer c = (DeliveryCustomer) cust;
            
            PreparedStatement stmt1;
            PreparedStatement stmt2;

            try {
                stmt0 = conn.createStatement();
                ResultSet rset = stmt0.executeQuery(findID);
                int newID = -1;
                while (rset.next()) {
                    newID = rset.getInt(1);
                    if (rset.wasNull()) {newID = 1;}
                    else {newID++;}
                }
                
                stmt1 = conn.prepareStatement(query1);
                stmt2 = conn.prepareStatement(query2);
                
                stmt1.setInt(1,newID);
                stmt1.setString(2,c.getName());
                stmt1.setString(3,c.getPhone());
                stmt1.executeUpdate();
                
                stmt2.setInt(1,newID);
                stmt2.setString(2,c.getAddress());
                stmt2.executeUpdate();
                cust.setID(newID);
            }
            catch (SQLException e) {
                System.out.println("Error adding Customer");
                while (e != null) {
                    System.out.println("Message     : " + e.getMessage());
                    e = e.getNextException();
                }
                conn.close();
            }
        }
        else if(cust instanceof DineOutCustomer) {
            DineOutCustomer c = (DineOutCustomer) cust;
            //now can call DineOutCustomer methods on c
            PreparedStatement stmt1;

            try {
                stmt0 = conn.createStatement();
                ResultSet rset = stmt0.executeQuery(findID);
                int newID = -1;
                while (rset.next()) {
                    newID = rset.getInt(1);
                    if (rset.wasNull()) {newID = 1;}
                    else {newID++;}
                }
                
                stmt1 = conn.prepareStatement(query1);                
                stmt1.setInt(1,newID);
                stmt1.setString(2,c.getName());
                stmt1.setString(3,c.getPhone());
                stmt1.executeUpdate();
                cust.setID(newID);
            }
            catch (SQLException e) {
                System.out.println("Error adding Customer");
                while (e != null) {
                    System.out.println("Message     : " + e.getMessage());
                    e = e.getNextException();
                }
                conn.close();
            }
        }
        
        conn.close();
    }

    /**
     *
     * @param o the order to mark as complete in the database
     * @throws SQLException
     * @throws IOException
     * @requires the order exists in the database
     * @ensures the order will be marked as complete
     */
    public static void CompleteOrder(Order o) throws SQLException, IOException {
        connect_to_db();
		/*add code to mark an order as complete in the DB. You may have a boolean
          field for this, or maybe a completed time timestamp. However you have it, */
        String updateP = "Update PIZZA SET status = \'Ready\' WHERE order_id = ?;";
        String updateO = "Update ORDER_ SET status = 1 WHERE order_id = ?;";
        
        PreparedStatement stmtP;
        PreparedStatement stmtO;
        
        try {
            stmtP = conn.prepareStatement(updateP);
            stmtO = conn.prepareStatement(updateO);
            
            stmtP.clearParameters();
            stmtP.setInt(1,o.getID());
            stmtP.executeUpdate();
            stmtO.clearParameters();
            stmtO.setInt(1,o.getID());
            stmtO.executeUpdate();        
        }
        catch (SQLException e) {
            System.out.println("Error completing order " + o.getID());
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            conn.close();
        }
        conn.close();
    }

    /**
     *
     * @param t the topping whose inventory is being replenished
     * @param toAdd the amount of inventory of t to add
     * @throws SQLException
     * @throws IOException
     * @requires t exists in the database and toAdd > 0
     * @ensures t's inventory level is increased by toAdd
     */
    public static void AdjustInventoryLevel(Topping t, double toAdd) throws SQLException, IOException {
        connect_to_db();
		/*add code to add toAdd to the inventory level of T. This is not adding
        a new topping, it is adding a certain amount of stock for a topping.
        This would be used to show that an order was made to replenish the
        restaurants supply of pepperoni, etc*/
        
        //NOTE I'm also gonna use this to subtract from inventory levels
        
        String update = "UPDATE TOPPING " +
        "SET inventory = (? + ?) " +
        "WHERE topping_id = ?";
        
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(update);
            stmt.setDouble(1,t.getInv());
            stmt.setDouble(2,toAdd);
            stmt.setInt(3,t.getID());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error changing inventory level");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }

            //don't leave your connection open!
            conn.close();
        }
        
        conn.close();
    }
    
    /*  Used to adjust inventory levels when a new order/pizza is added. 
        Private, so it doesn't need to open or close the connection  */
    private static void ChangeInventoryLevel(Topping t, double toAdd) throws SQLException, IOException {        
        
        String update = "UPDATE TOPPING " +
        "SET inventory = (? + ?) " +
        "WHERE topping_id = ?";
        
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(update);
            stmt.setDouble(1,t.getInv());
            stmt.setDouble(2,toAdd);
            stmt.setInt(3,t.getID());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error changing inventory level");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
    }


    /*
        A function to get the list of toppings and their inventory levels. I
        have left this code "complete" as an example of how to use JDBC to get
        data from the database. This query will not work on your database if you
        have different field or table names, so it will need to be changed

        Also note, this is just getting the topping ids and then calling
        getTopping() to get the actual topping. You will need to complete this
        on your own

        You don't actually have to use and write the getTopping() function, but
        it can save some repeated code if the program were to expand, and it
        keeps the functions simpler, more elegant and easy to read. Breaking up
        the queries this way also keeps them simpler. I think it's a better way
        to do it, and many people in the industry would agree, but its a
        suggestion, not a requirement.
    */

    /**
     *
     * @return the List of all toppings in the database
     * @throws SQLException
     * @throws IOException
     * @ensures the returned list will include all toppings and accurate inventory levels
     */
    public static ArrayList<Topping> getInventory() throws SQLException, IOException {
        //start by connecting
        connect_to_db();
        ArrayList<Topping> ts = new ArrayList<Topping>();
        //create a string with out query, this one is an easy one
        String query = "Select Topping_ID From TOPPING;";

        Statement stmt = conn.createStatement();
        try {
            ResultSet rset = stmt.executeQuery(query);
            //even if you only have one result, you still need to call
            //ResultSet.next() to load the first tuple
            while(rset.next())
            {
					/*Use getInt, getDouble, getString to get the actual value.
                    You can use the column number starting with 1, or use the
                    column name as a string

					NOTE: You want to use rset.getInt() instead of
                    Integer.parseInt(rset.getString()), not just because it's
                    shorter, but because of the possible NULL values. A NUll
                    would cause parseInt to fail

					If there is a possibility that it could return a NULL value
                    you need to check to see if it was NULL. In this query we
                    won't get nulls, so I didn't. If I was going to I would do:

					int ID = rset.getInt(1);
					if(rset.wasNull())
					{
						//set ID to what it should be for NULL, and whatever you
                        need to do.
					}

					NOTE: you can't check for NULL until after you have read
                    the value using one of the getters.

					*/
                int ID = rset.getInt(1);
                //Now I'm just passing my primary key to this function to get
                //the topping itself individually
                ts.add(getTopping(ID));
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading inventory");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }

            //don't leave your connection open!
            conn.close();
            return ts;
        }


        //end by closing the connection
        conn.close();
        return ts;
    }

    /**
     *
     * @return a list of all orders that are currently open in the kitchen
     * @throws SQLException
     * @throws IOException
     * @ensures all currently open orders will be included in the returned list.
     */
    public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
        connect_to_db();

        ArrayList<Order> os = new ArrayList<Order>();
		/*add code to get a list of all open orders. Only return Orders that have
        not been completed. If any pizzas are not completed, then the order is open.*/
        String query = "Select order_id FROM ORDER_ " +
        "WHERE status = 0;";
        
        Statement stmt = conn.createStatement();
        try {
            ResultSet rset = stmt.executeQuery(query);
            
            while (rset.next()) {
                int orderID = rset.getInt(1);
                os.add(getOrder(orderID));
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading list of open Orders");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            conn.close();
            return os;
        }
        conn.close();
        return os;
    }

    /**
     *
     * @param size the pizza size
     * @param crust the type of crust
     * @return the base price for a pizza with that size and crust
     * @throws SQLException
     * @throws IOException
     * @requires size = size_s || size_m || size_l || size_xl AND crust = crust_thin || crust_orig || crust_pan || crust_gf
     * @ensures the base price for a pizza with that size and crust is returned
     */
    public static double getBasePrice(String size, String crust) throws SQLException, IOException {
        connect_to_db();
        double bp = 0.0;
        //add code to get the base price for that size and crust pizza Depending on how you store size and crust in your database, you may have to do a conversion
        String query = "Select price From BASE Where size_ LIKE \'" + size +
        "\' AND crust LIKE \'" + crust + "\';";
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(query);
            ResultSet rset = stmt.executeQuery();
            
            while (rset.next()) {
                bp = rset.getDouble(1);
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading base price");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            conn.close();
            return bp;
        }
        conn.close();
        return bp;
    }

    /**
     *
     * @return the list of all discounts in the database
     * @throws SQLException
     * @throws IOException
     * @ensures all discounts are included in the returned list
     */
    public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
        ArrayList<Discount> discs = new ArrayList<Discount>();
        connect_to_db();
        //add code to get a list of all discounts
        String query = "Select discount_id FROM DISCOUNT;";
        
        Statement stmt = conn.createStatement();
        try {
            ResultSet rset = stmt.executeQuery(query);
            
            while (rset.next()) {
                int discountID = rset.getInt(1);
                discs.add(getDiscount(discountID));
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading Discount list");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            conn.close();
            return discs;
        }
        conn.close();
        return discs;
    }

    /**
     *
     * @return the list of all delivery and carry out customers
     * @throws SQLException
     * @throws IOException
     * @ensures the list contains all carryout and delivery customers in the database
     */
    public static ArrayList<ICustomer> getCustomerList() throws SQLException, IOException {
        ArrayList<ICustomer> custs = new ArrayList<ICustomer>();
        connect_to_db();
        
        String query1 = "Select customer_id FROM DELIVERY_CUSTOMER;";
        String query2 = "Select customer_id FROM PICKUP_CUSTOMER;";
        
        Statement stmt1 = conn.createStatement();
        Statement stmt2 = conn.createStatement();

        try {
            ResultSet deliv = stmt1.executeQuery(query1);
            while (deliv.next()) {
                int customerID = deliv.getInt(1);
                custs.add(getCustomer(customerID));
            }
            ResultSet dineout = stmt2.executeQuery(query2);
            while (dineout.next()) {
                int customerID = dineout.getInt(1);
                //  check if the dineout customer was already included as a
                //  delivery customer
                boolean wasDeliv = false;
                for (ICustomer C : custs) {
                    if (customerID == C.getID()) {
                        wasDeliv = true;
                    }
                }
                if (!wasDeliv) {
                    custs.add(getCustomer(customerID));
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading list of Customers");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            conn.close();
            return custs;
        }
        conn.close();
        return custs;
    }



	/*
	Note: The following incomplete functions are not strictly required, but could
    make your DBNinja class much simpler. For instance, instead of writing one
    query to get all of the information about an order, you can find the primary
    key of the order, and use that to find the primary keys of the pizzas on
    that order, then use the pizza primary keys individually to build your
    pizzas. We are no longer trying to get everything in one query, so feel
    free to break them up as much as possible

	You could also add functions that take in a Pizza object and add that to the
    database, or take in a pizza id and a topping id and add that topping to the
    pizza in the database, etc. I would recommend this to keep your addOrder
    function much simpler

	These simpler functions should still not be called from our menu class. That
    is why they are private

	We don't need to open and close the connection in these, since they are only
    called by a function that has opened the connection and will close it after
	*/


    private static Topping getTopping(int ID) throws SQLException, IOException {
        //add code to get a topping
		//the java compiler on unix does not like that t could be null, so I created a fake topping that will be replaced
        Topping t = new Topping("fake", 0.25, 100.0, -1);
		String query = "Select name, price, inventory From TOPPING where topping_id = ?";

        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setInt(1,ID);
            ResultSet rset = stmt.executeQuery();
            
            while(rset.next()) {
					String tname = rset.getString(1);
					double price = rset.getDouble(2);
					double inv = rset.getDouble(3);

					t = new Topping(tname, price, inv, ID);
			}
		}
		catch (SQLException e) {
            System.out.println("Error loading Topping");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return t;
        }
        return t;
    }

    private static Discount getDiscount(int ID)  throws SQLException, IOException {
        Discount D = new Discount("fake",0,0,0);
        String query = "Select name, percent_off, dollar_off From DISCOUNT where discount_id = ?;";

        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setInt(1,ID);
            ResultSet rset = stmt.executeQuery();
            
            while(rset.next()) {
                    String dname = rset.getString(1);
                    double percent = rset.getDouble(2);
                    double dollar = rset.getDouble(3);

                    D = new Discount(dname, percent, dollar, ID);
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading Discount");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return D;
        }

        return D;
    }

    //  Get a pizza, its base price, and its discounts and toppings
    private static Pizza getPizza(int ID)  throws SQLException, IOException {
        
        Pizza P = new Pizza (-1,"small","Thin",3);
        String pquery = "Select size_, crust From PIZZA Where pizza_id = ?;";
        String dquery = "Select discount_id From D_APPLY_P Where pizza_id = ?;";
        String tquery = "Select topping_id From HAS Where pizza_id = ?;";
        PreparedStatement stmt1;
        PreparedStatement stmt2;
        PreparedStatement stmt3;
        String size = "";
        String crust = "";
        double bp = -1.0;

        try {
            stmt1 = conn.prepareStatement(pquery);
            stmt2 = conn.prepareStatement(dquery);
            stmt3 = conn.prepareStatement(tquery);
            
            ResultSet baseSet = stmt1.executeQuery();
            while (baseSet.next()) {
                size = baseSet.getString(1);
                if (baseSet.wasNull()) {System.out.println("getPizza() ERROR: PIZZA size not initialized");}
                crust = baseSet.getString(2);
                if (baseSet.wasNull()) {System.out.println("getPizza() ERROR: PIZZA crust not initialized");}
            }
            bp = mygetBasePrice(size,crust);
            P = new Pizza(ID,size,crust,bp);
            
            ResultSet discSet = stmt2.executeQuery();
            while (discSet.next()) {
                Discount d = getDiscount(discSet.getInt(1));
                P.addDiscount(d);
            }
            
            ResultSet topSet = stmt3.executeQuery();
            while (topSet.next()) {
                Topping t = getTopping(topSet.getInt(1));
                P.addTopping(t);
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading Pizza");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return P;
        }
        return P;
    }

    private static ICustomer getCustomer(int ID)  throws SQLException, IOException {
        ICustomer C = new DineOutCustomer(-1,"fake","nonumber");
        String query = "Select p.name, p.phone_number, d.address " +
        "FROM PICKUP_CUSTOMER p " +
        "LEFT OUTER JOIN DELIVERY_CUSTOMER d ON p.customer_id = d.customer_id " +
        "WHERE p.customer_id = ?;";

        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setInt(1,ID);
            ResultSet rset = stmt.executeQuery();
            
            while(rset.next()) {
                String cname = rset.getString(1);
                if (rset.wasNull()) {System.out.println("getCustomer() ERROR: CUSTOMER name is null");}
                String number = rset.getString(2);
                if (rset.wasNull()) {System.out.println("getCustomer() ERROR: CUSTOMER number is null");}
                String address = rset.getString(3);
                if (rset.wasNull()) {System.out.println("getCustomer() ERROR: CUSTOMER address is null");}
                
                if (address == null) {
                    C = new DineOutCustomer(ID,cname,number);
                }
                else {
                    C = new DeliveryCustomer(ID,cname,number,address);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading Customer");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return C;
        }
        return C;
    }

    private static Order getOrder(int ID)  throws SQLException, IOException {
        //add code to get an order. Remember, an order has pizzas, a customer, and discounts on it
        Order O = new Order();
        ICustomer C = new DineOutCustomer(-1,"fake","nonumber");
                
        String query1 = "Select din.table_number, dout.customer_id, del.customer_id " +
        "FROM ORDER_ o " +
        "LEFT OUTER JOIN DINE_IN_ORDER din ON o.order_id = din.order_id " +
        "LEFT OUTER JOIN PICKUP_ORDER dout ON o.order_id = dout.order_id " +
        "LEFT OUTER JOIN DELIVERY_ORDER del ON o.order_id = del.order_id " +
        "WHERE o.order_id = ?;";
        
        String query2 = "Select seat_number FROM SEAT_NUMBER " +
        "WHERE order_id = ?;";
        
        String query3 = "Select pizza_id FROM PIZZA WHERE order_id = ?;";
        
        String query4 = "Select d.discount_id FROM DISCOUNT d " +
        "INNER JOIN D_APPLY_O a ON d.discount_id = a.discount_id " +
        "INNER JOIN ORDER_ o ON a.order_id = o.order_id " +
        "WHERE o.order_id = ?;";
        
        PreparedStatement stmt;
        PreparedStatement stmt2;
        PreparedStatement stmt3;
        PreparedStatement stmt4;
        try {
            stmt = conn.prepareStatement(query1);
            stmt.clearParameters();
            stmt.setInt(1,ID);
            ResultSet rset = stmt.executeQuery();
            
            while (rset.next()) {
                
                //  Check if it's a dine in
                int tnum = rset.getInt(1);
                if (!rset.wasNull()) {
                    stmt2 = conn.prepareStatement(query2);
                    stmt2.clearParameters();
                    stmt2.setInt(1,ID);
                    ResultSet seatset = stmt2.executeQuery();
                    
                    List<Integer> seats = new ArrayList<Integer>();
                    while (seatset.next()) {
                        seats.add(seatset.getInt(1));
                    }
                    C = new DineInCustomer(tnum,seats,-1);
                    O = new Order(ID,C,DBNinja.dine_in);
                }
                else { 
                    int pickupid = rset.getInt(2);
                    if (!rset.wasNull()) {
                        C = getCustomer(pickupid);
                        O = new Order(ID,C,DBNinja.dine_out);
                    }
                    else {
                        int delivid = rset.getInt(3);
                        if (!rset.wasNull()) {
                            C = getCustomer(delivid);
                            O = new Order(ID,C,DBNinja.delivery);
                        }
                        else {
                            System.out.println("Error in finding CUSTOMER from ORDER number: "
                                + ID);
                        }
                    }
                }
                
                stmt3 = conn.prepareStatement(query3);
                stmt3.clearParameters();
                stmt3.setInt(1,ID);
                ResultSet pizzaset = stmt3.executeQuery();
                while (pizzaset.next()) {
                    int pizzaID = pizzaset.getInt(1);
                    Pizza P = getPizza(pizzaID);
                    O.addPizza(P);
                }
                stmt4 = conn.prepareStatement(query4);
                stmt4.clearParameters();
                stmt4.setInt(1,ID);
                ResultSet discountset = stmt4.executeQuery();
                while (discountset.next()) {
                    int discountID = discountset.getInt(1);
                    Discount D = getDiscount(discountID);
                    O.addDiscount(D);
                }
            }            
        }
        catch (SQLException e) {
            System.out.println("Error loading Order");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return O;
        }
        return O;
    }
    
    /*  You could also add functions that take in a Pizza object and add that to the
    database, or take in a pizza id and a topping id and add that topping to the
    pizza in the database, etc. I would recommend this to keep your addOrder
    function much simpler  */
    private static void addPizza(Pizza P, int orderID) throws SQLException, IOException {
        String updateP = "INSERT INTO PIZZA (pizza_id,price,order_id,size_,crust) " +
        "VALUES (?,?,?,?,?);";
        String findID = "Select MAX(pizza_id) FROM PIZZA;";
        PreparedStatement stmt;
        PreparedStatement fnd;
        
        try {
            fnd = conn.prepareStatement(findID);
            ResultSet rset = fnd.executeQuery();
            int newID = -1;
            while (rset.next()) {
                newID = rset.getInt(1);
            }
            newID++;
            P.setID(newID);
            
            stmt = conn.prepareStatement(updateP);
            stmt.setInt(1,P.getID());
            stmt.setDouble(2,P.calcPrice());
            stmt.setInt(3,orderID);
            stmt.setString(4,P.getSize());
            stmt.setString(5,P.getCrust());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error inserting Pizza");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
        
        for (Discount D : P.getDiscounts()) {
            addPizzaDiscount(P.getID(),D.getID());
        }
        for (Topping T : P.getToppings()) {
            putTopping(P.getID(),T.getID(),T.getExtra());
        }
    }
    
    /*  Assumes that the corresponding PIZZA and DISCOUNT already exist in the 
        database.  */
    private static void addPizzaDiscount(int pizzaID, int discountID) throws SQLException, IOException {
        String updateD = "INSERT INTO D_APPLY_P (pizza_id,discount_id) " +
        "VALUES (?,?)";
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(updateD);
            stmt.setInt(1,pizzaID);
            stmt.setInt(2,discountID);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error adding Discount to Pizza");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
    }
    
    /*  Assumes that the corresponding ORDER and DISCOUNT already exist in the 
        database.  */
    private static void addOrderDiscount(int orderID, int discountID) throws SQLException, IOException {
        String updateD = "INSERT INTO D_APPLY_O (order_id,discount_id) " +
        "VALUES (?,?)";
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(updateD);
            stmt.setInt(1,orderID);
            stmt.setInt(2,discountID);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error adding Discount to Order");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
    }
    
    /*  Assumes that the corresponding PIZZA and TOPPING already exist in the 
        database.  */
    private static void putTopping(int pID, int tID, boolean isExtra) throws SQLException, IOException {
        String insert = "INSERT INTO HAS (pizza_id, topping_id, extra) " +
        "VALUES (?,?,?);";
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(insert);
            stmt.setInt(1,pID);
            stmt.setInt(2,tID);
            if (isExtra) {stmt.setInt(3,1);} // set extra to true
            else {stmt.setInt(3,0);}
            
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error adding Topping to Pizza");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
        }
    }
    
    //  I made a separate function that doesn't open or close a connection to the
    //  database. I know this isn't good practice, it was a quick fix
    private static double mygetBasePrice(String size, String crust) throws SQLException, IOException {
        double bp = 0.0;
        String query = "Select price From BASE Where size_ LIKE \'" + size +
        "\' AND crust LIKE \'" + crust + "\';";
        PreparedStatement stmt;
        
        try {
            stmt = conn.prepareStatement(query);
            ResultSet rset = stmt.executeQuery();
            
            while (rset.next()) {
                bp = rset.getDouble(1);
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading base price");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }
            return bp;
        }
        return bp;
    }
}


