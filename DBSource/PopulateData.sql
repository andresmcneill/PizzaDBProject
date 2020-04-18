/*	Andres McNeill					 *
 *	CPSC 4620						 *
 *	Spring 2020						 *
 *	Script to populate the database  */

--	Populate TOPPING  --

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (1,'Pepperoni',1.25,0.2,100,2,2.75,3.5,4.5);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (2,'Sausage',1.25,0.15,100,2.5,3,3.5,4.25);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (3,'Ham',1.5,0.15,78,2,2.5,3.25,4);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (4,'Chicken',1.75,0.25,56,1.5,2,2.25,3);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (5,'Green Pepper',0.5,0.02,79,1,1.5,2,2.5);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (6,'Onion',0.5,0.02,85,1,1.5,2,2.75);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (7,'Roma tomato',0.75,0.03,86,2,3,3.5,4.5);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (8,'Mushrooms',0.75,0.13,52,1.5,2,2.5,3);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (9,'Black Olives',0.6,0.1,39,0.75,1,1.5,2);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (10,'Pineapple',1,0.25,15,1,1.25,1.75,2);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (11,'Jalapenos',0.5,0.05,64,0.5,0.75,1.25,1.75);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (12,'Banana Peppers',0.5,0.05,36,0.6,1,1.3,1.75);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (13,'Regular Cheese',1.5,0.12,250,2,3.5,5,7);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (14,'Four Cheese Blend',2,0.15,150,2,3.5,5,7);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (15,'Feta Cheese',2,0.18,75,1.75,3,4,5.5);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (16,'Goat Cheese',2,0.2,54,1.6,2.75,4,5.5);

INSERT INTO TOPPING (topping_id,name,price,cost,
	inventory,small,medium,large,x_large)
VALUES (17,'Bacon',1.5,0.25,89,1,1.5,2,3);


--	Populate DISCOUNT  --

INSERT INTO DISCOUNT (discount_id,name,percent_off,dollar_off)
VALUES (1,'employee',15,NULL);

INSERT INTO DISCOUNT (discount_id,name,percent_off,dollar_off)
VALUES (2,'Lunch Special Medium',NULL,1);

INSERT INTO DISCOUNT (discount_id,name,percent_off,dollar_off)
VALUES (3,'Lunch Special Large',NULL,2);

INSERT INTO DISCOUNT (discount_id,name,percent_off,dollar_off)
VALUES (4,'Specialty',NULL,1.5);

INSERT INTO DISCOUNT (discount_id,name,percent_off,dollar_off)
VALUES (5,'Gameday special',20,NULL);


--	Populate BASE  --

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('small','Thin',3,0.5);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('small','Original',3,0.75);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('small','Pan',3.5,1);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('small','Gluten-Free',4,2);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('medium','Thin',5,1);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('medium','Original',5,1.5);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('medium','Pan',6,2.25);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('medium','Gluten-Free',6.25,3);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('Large','Thin',8,1.25);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('Large','Original',8,2);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('Large','Pan',9,3);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('Large','Gluten-Free',9.5,4);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('X-Large','Thin',10,2);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('X-Large','Original',10,3);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('X-Large','Pan',11.5,4.5);

INSERT INTO BASE (size_,crust,price,cost)
VALUES ('X-Large','Gluten-Free',12.5,6);


--  Populate ORDERS, PIZZA  

--	PIZZA 1
--	ORDER 1

INSERT INTO ORDER_ (order_id,status) VALUES (1,1);

-- dine in
INSERT INTO DINE_IN_ORDER (order_id,table_number) VALUES (1,14);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	1,
	13.50,
	3.68,
	'2020-03-05 12:03:00',
	'Ready',
	1,
	'Large',
	'Thin'
);

-- list toppings
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Regular Cheese');
SET @top2 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Pepperoni');
SET @top3 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Sausage');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (1,(SELECT @top1),1);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (1,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (1,(SELECT @top3),0);

-- discount price
INSERT INTO D_APPLY_P (pizza_id, discount_id)
VALUES (1,
	(SELECT discount_id FROM DISCOUNT WHERE name LIKE 'Lunch Special Large'));

UPDATE PIZZA
SET price = price - (SELECT dollar_off FROM DISCOUNT
	WHERE name LIKE 'Lunch Special Large')
WHERE pizza_id = 1;

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 1),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 1)
WHERE order_id = 1;
-- seat numbers
INSERT INTO SEAT_NUMBER (order_id,seat_number)
VALUES (1,1),(1,2),(1,3);


--	PIZZA 2
--	ORDER 2

INSERT INTO ORDER_ (order_id,status) VALUES (2,1);

-- dine in
INSERT INTO DINE_IN_ORDER (order_id,table_number) VALUES (2,4);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	2,
	10.60,
	3.23,
	'2020-03-03 12:05:00',
	'Ready',
	2,
	'Medium',
	'Pan'
);

-- list toppings
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Feta Cheese');
SET @top2 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Black Olives');
SET @top3 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Roma tomato');
SET @top4 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Mushrooms');
SET @top5 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Banana Peppers');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (2,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (2,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (2,(SELECT @top3),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (2,(SELECT @top4),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (2,(SELECT @top5),0);

-- discount price
INSERT INTO D_APPLY_P (pizza_id, discount_id)
VALUES (2,
	(SELECT discount_id FROM DISCOUNT WHERE name LIKE 'Lunch Special Medium'));

UPDATE PIZZA
SET price = price - (SELECT dollar_off FROM DISCOUNT
	WHERE name LIKE 'Lunch Special Medium')
WHERE pizza_id = 2;

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 2),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 2)
WHERE order_id = 2;
-- seat numbers
INSERT INTO SEAT_NUMBER (order_id,seat_number)
VALUES (2,1);


--	PIZZA 3
--	ORDER 3

INSERT INTO ORDER_ (order_id,status) VALUES (3,1);

-- dine in
INSERT INTO DINE_IN_ORDER (order_id,table_number) VALUES (3,4);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	3,
	6.75,
	1.40,
	'2020-03-03 12:05:00',
	'Ready',
	3,
	'small',
	'Original'
);

-- list toppings
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Regular Cheese');
SET @top2 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Chicken');
SET @top3 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Banana Peppers');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (3,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (3,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (3,(SELECT @top3),0);

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 3),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 3)
WHERE order_id = 3;


--	PIZZA 4-9
--	ORDER 4

INSERT INTO ORDER_ (order_id,status) VALUES (4,1);

-- pickup customer
INSERT INTO PICKUP_CUSTOMER (customer_id,name,phone_number)
VALUES (1,'Andrew Wilkes-Brier','864-254-5861');

-- pick up
INSERT INTO PICKUP_ORDER (order_id,customer_id) VALUES (4,1);

-- 6 of the same pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	4,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	5,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	6,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	7,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	8,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (
	9,10.75,3.30,'2020-03-03 21:30:00','Ready',4,'Large','Original');

-- list toppings
SET @top1 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Regular Cheese');
SET @top2 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Pepperoni');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (4,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (4,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (5,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (5,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (6,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (6,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (7,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (7,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (8,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (8,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (9,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (9,(SELECT @top2),0);

--	Order Price
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id >= 4 AND
		pizza_id <= 9),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id >= 4 AND
		pizza_id <= 9)
WHERE order_id = 4;


--	PIZZA 10,11,12,13
--	ORDER 5

INSERT INTO ORDER_ (order_id,status) VALUES (5,1);

-- delivery customer
INSERT INTO DELIVERY_CUSTOMER (customer_id,address)
VALUES (1,'115 Party Blvd, Anderson SC 29631');

-- delivery order
INSERT INTO DELIVERY_ORDER (order_id,customer_id) VALUES (5,1);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (10,14.50,5.59,'2020-03-05 19:11:00','Ready',5,'X-Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (11,17,5.59,'2020-03-05 19:11:00','Ready',5,'X-Large','Original');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (12,14,5.68,'2020-03-05 19:11:00','Ready',5,'X-Large','Original');

-- list toppings
SET @top0 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Four Cheese Blend');
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Chicken');
SET @top2 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Green Pepper');
SET @top3 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Onion');
SET @top4 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Mushrooms');
SET @top5 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Jalapenos');
SET @top6 =
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Bacon');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (10,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (10,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (11,(SELECT @top3),1);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (11,(SELECT @top4),1);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (12,(SELECT @top5),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (12,(SELECT @top6),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (10,(SELECT @top0),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (11,(SELECT @top0),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (12,(SELECT @top0),0);

INSERT INTO D_APPLY_P (pizza_id,discount_id)
VALUES (11,
	(SELECT discount_id FROM DISCOUNT WHERE name LIKE 'Specialty'));
UPDATE PIZZA
SET price = price -
	(SELECT dollar_off FROM DISCOUNT WHERE name LIKE 'Specialty')
WHERE pizza_id = 11;

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id >= 10 AND
		pizza_id <= 12),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id >= 10 AND
		pizza_id <= 12)
WHERE order_id = 5;

-- discount price
INSERT INTO D_APPLY_O (order_id,discount_id)
VALUES (5,
	(SELECT discount_id FROM DISCOUNT WHERE name LIKE 'Gameday special'));
UPDATE ORDER_
SET total_price = total_price * ((100 - (SELECT percent_off FROM DISCOUNT
	WHERE name LIKE 'Gameday special'))/100)
WHERE order_id = 5;


--	ORDER 6
--	PIZZA 13

INSERT INTO ORDER_ (order_id,status) VALUES (6,1);

-- pickup customer
INSERT INTO PICKUP_CUSTOMER (customer_id,name,phone_number)
VALUES (2,'Matt Engers','864-474-9953');

-- pickup order
INSERT INTO PICKUP_ORDER (order_id,customer_id) VALUES (6,2);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (13,16.85,7.85,'2020-03-02 17:30:00','Ready',6,'X-Large','Gluten-Free');

SET @top0 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Goat Cheese');
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Green Pepper');
SET @top2 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Onion');
SET @top3 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Roma tomato');
SET @top4 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Mushrooms');
SET @top5 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Black Olives');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top0),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top3),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top4),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (13,(SELECT @top5),0);

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 13),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 13)
WHERE order_id = 6;


--	ORDER 7
--	PIZZA 14
INSERT INTO ORDER_ (order_id,status) VALUES (7,1);

INSERT INTO PICKUP_CUSTOMER (customer_id,name,phone_number)
VALUES (3,'Frank Turner','864-232-8944');

-- pickup order
INSERT INTO PICKUP_ORDER (order_id,customer_id) VALUES (7,3);

-- delivery
INSERT INTO DELIVERY_CUSTOMER (customer_id,address)
VALUES (3,'6745 Wessex St Anderson SC 29621');

INSERT INTO DELIVERY_ORDER (order_id,customer_id) VALUES (7,3);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (14,13.25,3.20,'2020-03-02 18:17:00','Ready',7,'Large','Thin');

SET @top0 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Four Cheese Blend');
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Green Pepper');
SET @top2 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Onion');
SET @top3 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Chicken');
SET @top4 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Mushrooms');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (14,(SELECT @top0),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (14,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (14,(SELECT @top2),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (14,(SELECT @top3),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (14,(SELECT @top4),0);

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 14),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 14)
WHERE order_id = 7;


--	ORDER 8
--	PIZZA 15
INSERT INTO ORDER_ (order_id,status) VALUES (8,1);

INSERT INTO PICKUP_CUSTOMER (customer_id,name,phone_number)
VALUES (4,'Milo Auckerman','864-878-5679');

-- pickup order
INSERT INTO PICKUP_ORDER (order_id,customer_id) VALUES (8,4);

-- delivery
INSERT INTO DELIVERY_CUSTOMER (customer_id,address)
VALUES (4,'8879 Suburban Home, Anderson, SC 29621');

INSERT INTO DELIVERY_ORDER (order_id,customer_id) VALUES (8,4);

-- pizza
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (15,12,3.75,'2020-03-02 20:32:00','Ready',8,'Large','Thin');
INSERT INTO PIZZA (pizza_id,price,cost,tstamp,status,order_id,size_,crust)
VALUES (16,12,2.55,'2020-03-02 20:32:00','Ready',8,'Large','Thin');

SET @top0 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Four Cheese Blend');
SET @top1 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Regular Cheese');
SET @top2 = 
	(SELECT topping_id FROM TOPPING WHERE name LIKE 'Pepperoni');
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (15,(SELECT @top0),1);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (15,(SELECT @top1),0);
INSERT INTO HAS (pizza_id,topping_id,extra)
VALUES (16,(SELECT @top2),1);

-- order
UPDATE ORDER_
SET total_price = (SELECT SUM(price) FROM PIZZA WHERE pizza_id = 15
		OR pizza_id = 16),
	total_cost = (SELECT SUM(cost) FROM PIZZA WHERE pizza_id = 15
		OR pizza_id = 16)
WHERE order_id = 8;

-- discount
UPDATE ORDER_
SET total_price = total_price * ((100 -
	(SELECT percent_off FROM DISCOUNT WHERE name LIKE 'employee'))/100)
WHERE order_id = 8;
