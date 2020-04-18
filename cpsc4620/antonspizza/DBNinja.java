package cpsc4620.antonspizza;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *  A utility class to help add and retrieve information from the database
 *  Mainly referred to by Menu.java
 *  Has several hard coded values which correspond to the sql database information
 *  These must be changed to work with a different database

 *  All public functions must open and close connections, while private
 *  functions are assumed to be called from the public functions and do not have
 *  to worry about opening or closing the connection.
 *
 *  NOTE: in this code, what is listed as PICKUP_CUSTOMER in the SQL database is
 *  listed as a dine_out customer
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

    //  These for consistency
    public final static String dine_out = "dine_out";
    public final static String delivery = "delivery";
    public final static String dine_in = "dine_in";

    /*  These must match the size strings currently in the database (in TOPPING,
     *  PIZZA, and BASE)  */
    public final static String size_s = "small";
    public final static String size_m = "medium";
    public final static String size_l = "Large";
    public final static String size_xl = "X-Large";

    public final static String crust_thin = "Thin";
    public final static String crust_orig = "Original";
    public final static String crust_pan = "Pan";
    public final static String crust_gf = "Gluten-Free";



    /**
     *  This function will handle the connection to the database
     *  @return true if the connection was successfully made
     *  @throws SQLException
     *  @throws IOException
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
     *  @param o order that needs to be saved to the database
     *  @throws SQLException
     *  @throws IOException
     *  @requires o is not NULL. o's ID is -1, as it has not been assigned yet.
     *      The pizzas do not exist in the database yet, and the topping inventory
     *      will allow for these pizzas to be made
     *  @ensures o will be assigned an id and added to the database, along with
     *      all of it's pizzas and discounts. Inventory levels will be updated
     *      appropriately
     */
    public static void addOrder(Order o) throws SQLException, IOException {
        connect_to_db();


        String findID = "Select MAX(order_id) FROM ORDER_;";

        String insertO = "Insert Into ORDER_ (order_id,total_price) Values (?,?);";
        String insertDineIn = "Insert Into DINE_IN_ORDER (order_id,table_number) " +
        "Values (?,?);";
        String insertDineOut = "Insert Into PICKUP_ORDER (order_id,customer_id) " +
        "Values (?,?);";
        String insertDelivery = "Insert Into DELIVERY_ORDER (order_id,customer_id) " +
        "Values (?,?);";

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
                selectAmt = "Select " + psize + " From TOPPING Where topping_id = ?;";
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
     *  @param cust the new customer to add to the database
     *  @throws SQLException
     *  @throws IOException
     *  @requires cust is not null. C's ID is -1 and will need to be assigned
     *  @ensures cust is given an ID and added to the database
     */
    public static void addCustomer(ICustomer cust) throws SQLException, IOException {
        connect_to_db();
        String findID = "Select MAX(customer_id) From PICKUP_CUSTOMER;";
        String query1 = "Insert Into PICKUP_CUSTOMER (customer_id,name,phone_number) " +
        "Values (?,?,?);";
        String query2 = "Insert Into DELIVERY_CUSTOMER (customer_id,address) " +
        "Values (?,?);";
        Statement stmt0;

        //  Depending on customer type, will need to have different SQL statements
        //  For a dine-in customer, no customer is added to the database, since
        //  their information is instead attached to the DINE_IN_ORDER table.
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
     *  @param o the order to mark as complete in the database
     *  @throws SQLException
     *  @throws IOException
     *  @requires the order exists in the database
     *  @ensures the order will be marked as complete (boolean flag in ORDER_
     *  set to true and all its pizzas have status set to 'Ready')
     */
    public static void CompleteOrder(Order o) throws SQLException, IOException {
        connect_to_db();

        String updateP = "Update PIZZA Set status = \'Ready\' Where order_id = ?;";
        String updateO = "Update ORDER_ Set status = 1 Where order_id = ?;";

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
     *  @param t the topping whose inventory is being replenished
     *  @param toAdd the amount of inventory of t to add
     *  @throws SQLException
     *  @throws IOException
     *  @requires t exists in the database and toAdd > 0
     *  @ensures t's inventory level is increased by toAdd (or decreased if toAdd
     *  is negative)
     */
    public static void AdjustInventoryLevel(Topping t, double toAdd) throws SQLException, IOException {
        connect_to_db();

        String update = "Update TOPPING " +
        "Set inventory = (? + ?) " +
        "Where topping_id = ?";

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

    /**
     *  Is the same as AdjustInventoryLevel but doesn't open/close connection
     *  @param t the topping whose inventory is being replenished
     *  @param toAdd the amount of inventory of t to add
     *  @throws SQLException
     *  @throws IOException
     *  @requires t exists in the database and toAdd > 0
     *  @ensures t's inventory level is increased by toAdd (or decreased if
     *      toAdd is negative)
     */
    private static void ChangeInventoryLevel(Topping t, double toAdd) throws SQLException, IOException {

        String update = "Update TOPPING " +
        "Set inventory = (? + ?) " +
        "Where topping_id = ?";

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

    /**
     *
     *  @return the List of all toppings in the database
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the returned list will include all toppings and accurate
     *  inventory levels
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

            while(rset.next())
            {
                int ID = rset.getInt(1);
                ts.add(getTopping(ID));
            }
        }
        catch (SQLException e) {
            System.out.println("Error loading inventory");
            while (e != null) {
                System.out.println("Message     : " + e.getMessage());
                e = e.getNextException();
            }

            conn.close();
            return ts;
        }

        conn.close();
        return ts;
    }

    /**
     *  @return a list of all orders that are currently open in the kitchen
     *  @throws SQLException
     *  @throws IOException
     *  @ensures all currently open orders will be included in the returned list.
     */
    public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
        connect_to_db();

        ArrayList<Order> os = new ArrayList<Order>();

        String query = "Select order_id From ORDER_ " +
        "Where status = 0;";

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
     *  @param size the pizza size
     *  @param crust the type of crust
     *  @return the base price for a pizza with that size and crust
     *  @throws SQLException
     *  @throws IOException
     *  @requires size = size_s || size_m || size_l || size_xl AND
     *      crust = crust_thin || crust_orig || crust_pan || crust_gf
     *  @ensures the base price for a pizza with that size and crust is returned
     */
    public static double getBasePrice(String size, String crust) throws SQLException, IOException {
        connect_to_db();
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
            conn.close();
            return bp;
        }
        conn.close();
        return bp;
    }

    /**
     *  @return the list of all discount types in the database
     *  @throws SQLException
     *  @throws IOException
     *  @ensures all discount types are included in the returned list
     */
    public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
        ArrayList<Discount> discs = new ArrayList<Discount>();
        connect_to_db();
        //add code to get a list of all discounts
        String query = "Select discount_id From DISCOUNT;";

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
     *  @return the list of all delivery and carry out customers
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the list contains all carryout and delivery customers in
     *      the database
     */
    public static ArrayList<ICustomer> getCustomerList() throws SQLException, IOException {
        ArrayList<ICustomer> custs = new ArrayList<ICustomer>();
        connect_to_db();

        String query1 = "Select customer_id From DELIVERY_CUSTOMER;";
        String query2 = "Select customer_id From PICKUP_CUSTOMER;";

        Statement stmt1 = conn.createStatement();
        Statement stmt2 = conn.createStatement();

        try {
            ResultSet deliv = stmt1.executeQuery(query1);
            //  First add all delivery customers
            while (deliv.next()) {
                int customerID = deliv.getInt(1);
                custs.add(getCustomer(customerID));
            }
            ResultSet dineout = stmt2.executeQuery(query2);
            while (dineout.next()) {
                int customerID = dineout.getInt(1);
                //  check if the dineout customer was already included as a
                //  delivery customer, since delivery is a subclass of dineout
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

    //  The following functions are just private helper functions and don't need
    //  to open or close the connection to the database.

    /**
     *  @param ID the topping's id
     *  @return a topping object
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the topping with the corresponding ID is returned
     *  @requires the topping already exists in the database
     */
    private static Topping getTopping(int ID) throws SQLException, IOException {
        Topping t = new Topping("fake", 0.25, 100.0, -1);
		String query = "Select name, price, inventory From TOPPING Where topping_id = ?";

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

    /**
     *  @param ID the discount's id
     *  @return a Discount object
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the Discount with the corresponding ID is returned
     *  @requires the discount already exists in the database
     */
    private static Discount getDiscount(int ID)  throws SQLException, IOException {
        Discount D = new Discount("fake",0,0,0);
        String query = "Select name, percent_off, dollar_off From DISCOUNT Where discount_id = ?;";

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

    /**
     *  @param ID the pizza's id
     *  @return a Pizza object
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the pizza with the given ID is returned, along with its
     *      attached toppings and discounts
     *  @requires the pizza already exists in the database
     */
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

    /**
     *  @param ID the customer's id
     *  @return a Pizza object
     *  @throws SQLException
     *  @throws IOException
     *  @ensures a customer of type delivery or dine_out with the given ID is
     *      returned
     *  @requires the customer already exists in the database
     */
    private static ICustomer getCustomer(int ID)  throws SQLException, IOException {
        ICustomer C = new DineOutCustomer(-1,"fake","nonumber");
        String query = "Select p.name, p.phone_number, d.address " +
        "From PICKUP_CUSTOMER p " +
        "Left Outer Join DELIVERY_CUSTOMER d On p.customer_id = d.customer_id " +
        "Where p.customer_id = ?;";

        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(query);
            stmt.clearParameters();
            stmt.setInt(1,ID);
            ResultSet rset = stmt.executeQuery();

            while(rset.next()) {
                String cname = rset.getString(1);
                if (rset.wasNull()) {
                    System.out.println("getCustomer() ERROR: CUSTOMER name is null");
                }
                String number = rset.getString(2);
                if (rset.wasNull()) {
                    System.out.println("getCustomer() ERROR: CUSTOMER number is null");
                }
                String address = rset.getString(3);
                if (rset.wasNull()) {
                    System.out.println("getCustomer() ERROR: CUSTOMER address is null");
                }

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

    /**
     *  @param ID the order's id
     *  @return an Order object
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the correct Order is returned, along with its pizzas, discounts,
     *      customer, and type
     *  @requires the order already exists in the database
     */
    private static Order getOrder(int ID)  throws SQLException, IOException {
        //add code to get an order. Remember, an order has pizzas, a customer, and discounts on it
        Order O = new Order();
        ICustomer C = new DineOutCustomer(-1,"fake","nonumber");

        String query1 = "Select din.table_number, dout.customer_id, del.customer_id " +
        "From ORDER_ o " +
        "Left Outer Join DINE_IN_ORDER din On o.order_id = din.order_id " +
        "Left Outer Join PICKUP_ORDER dout On o.order_id = dout.order_id " +
        "Left Outer Join DELIVERY_ORDER del On o.order_id = del.order_id " +
        "Where o.order_id = ?;";

        String query2 = "Select seat_number From SEAT_NUMBER " +
        "Where order_id = ?;";

        String query3 = "Select pizza_id From PIZZA Where order_id = ?;";

        String query4 = "Select d.discount_id From DISCOUNT d " +
        "Inner Join D_APPLY_O a On d.discount_id = a.discount_id " +
        "Inner Join ORDER_ o On a.order_id = o.order_id " +
        "Where o.order_id = ?;";

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

    /**
     *  @param P a fully initialized Pizza object
     *  @param orderID an Order's ID
     *  @throws SQLException
     *  @throws IOException
     *  @ensures a pizza is inserted to DB and is attached to the given order
     *  @requires the order already exists in the database
     */
    private static void addPizza(Pizza P, int orderID) throws SQLException, IOException {
        String updateP = "Insert Into PIZZA (pizza_id,price,order_id,size_,crust) " +
        "Values (?,?,?,?,?);";
        String findID = "Select MAX(pizza_id) From PIZZA;";
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

    /**
     *  @param pizzaID a Pizza's ID
     *  @param discountID a Discount's ID
     *  @throws SQLException
     *  @throws IOException
     *  @ensures a discount is applied to the pizza
     *  @requires the pizza and discount already exist in the database
     */
    private static void addPizzaDiscount(int pizzaID, int discountID) throws SQLException, IOException {
        String updateD = "Insert Into D_APPLY_P (pizza_id,discount_id) " +
        "Values (?,?)";
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

    /**
     *  @param orderID an Order's id
     *  @param discountID a Discount's ID
     *  @throws SQLException
     *  @throws IOException
     *  @ensures a discount is applied to the order
     *  @requires the order and discount already exist in the database
     */
    private static void addOrderDiscount(int orderID, int discountID) throws SQLException, IOException {
        String updateD = "Insert Into D_APPLY_O (order_id,discount_id) " +
        "Values (?,?)";
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

    /**
     *  @param pID a Pizza's ID
     *  @param tID a Topping's ID
     *  @param isExtra true if the pizza will have double of this topping
     *  @throws SQLException
     *  @throws IOException
     *  @ensures the topping is added to the pizza
     *  @requires the topping and pizza already exist in the database
     */
    private static void putTopping(int pID, int tID, boolean isExtra) throws SQLException, IOException {
        String insert = "Insert Into HAS (pizza_id, topping_id, extra) " +
        "Values (?,?,?);";
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

    /**
     *  @param size the pizza size
     *  @param crust the type of crust
     *  @return the base price for a pizza with that size and crust
     *  @throws SQLException
     *  @throws IOException
     *  @requires size = size_s || size_m || size_l || size_xl AND
     *      crust = crust_thin || crust_orig || crust_pan || crust_gf
     *  @ensures the base price for a pizza with that size and crust is returned
     */
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
