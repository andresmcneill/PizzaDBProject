CREATE TABLE BASE (
	size_ VARCHAR(20),
	crust VARCHAR(30),
	price DECIMAL(6,2) DEFAULT 0 CHECK (price >= 0),
	cost DECIMAL(6,2) DEFAULT 0 CHECK (price >= 0),
	CONSTRAINT BASEPK
		PRIMARY KEY(size_, crust)
);

CREATE TABLE ORDER_ (
	order_id INT,
	status SMALLINT DEFAULT 0 CHECK (extra >= 0 AND extra <=1),
	total_price DECIMAL(6,2) DEFAULT 0 CHECK (total_price >= 0),
	total_cost DECIMAL(6,2) DEFAULT 0 CHECK (total_cost >= 0),
	CONSTRAINT ORDERPK
		PRIMARY KEY(order_id)
);

CREATE TABLE PIZZA (
	pizza_id INT,
	price DECIMAL(6,2) DEFAULT 0 CHECK (price >= 0),
	cost DECIMAL(6,2) DEFAULT 0 CHECK (cost >= 0),
	tstamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	status VARCHAR(9) DEFAULT 'Prepping',
	order_id INT,
	size_ VARCHAR(20),
	crust VARCHAR(30),
	CONSTRAINT PIZZAPK
		PRIMARY KEY(pizza_id),
	CONSTRAINT PIZZAFKORDER
		FOREIGN KEY (order_id)
			REFERENCES ORDER_(order_id)
			ON DELETE SET NULL,
	CONSTRAINT PIZZAFKBASE
		FOREIGN KEY (size_, crust)
			REFERENCES BASE(size_, crust)
			ON DELETE SET NULL
);

CREATE TABLE TOPPING (
	topping_id INT,
	name VARCHAR(40),
	price DECIMAL(6,2) DEFAULT 0 CHECK (price >= 0),
	cost DECIMAL(6,2) DEFAULT 0 CHECK (cost >= 0),
	inventory DECIMAL(7,2) CHECK (inventory >= 0),
	small DECIMAL(6,2),
	medium DECIMAL(6,2),
	large DECIMAL(6,2),
	x_large DECIMAL(6,2),
	CONSTRAINT TOPPINGPK
		PRIMARY KEY(topping_id)
);

CREATE TABLE HAS (
	pizza_id INT,
	topping_id INT,
	extra SMALLINT DEFAULT 0 CHECK (extra >= 0 AND extra <=1),
	CONSTRAINT HASFKPIZZA
		FOREIGN KEY (pizza_id)
			REFERENCES PIZZA(pizza_id)
			ON DELETE CASCADE,
	CONSTRAINT HASFKTOPPING
		FOREIGN KEY (topping_id)
			REFERENCES TOPPING(topping_id)
			ON DELETE CASCADE,
	CONSTRAINT HASPK
		PRIMARY KEY (pizza_id, topping_id)
);

CREATE TABLE DISCOUNT (
	discount_id INT,
	name VARCHAR(40),
	percent_off DECIMAL(5,2) CHECK (percent_off >= 0),
	dollar_off DECIMAL(5,2) CHECK (dollar_off >= 0),
	CONSTRAINT DISCOUNTPK
		PRIMARY KEY(discount_id)
);

CREATE TABLE D_APPLY_P (
	pizza_id INT,
	discount_id INT,
	CONSTRAINT D_APPLY_PPFKPIZZA
		FOREIGN KEY (pizza_id)
			REFERENCES PIZZA(pizza_id)
			ON DELETE CASCADE,
	CONSTRAINT D_APPLY_PFKDISCOUNT
		FOREIGN KEY (discount_id)
			REFERENCES DISCOUNT(discount_id)
			ON DELETE CASCADE,
	CONSTRAINT D_APPLY_PPK
		PRIMARY KEY(pizza_id, discount_id)
);

CREATE TABLE D_APPLY_O (
	order_id INT,
	discount_id INT,
	CONSTRAINT D_APPLY_OFKORDER
		FOREIGN KEY (order_id)
			REFERENCES ORDER_(order_id)
			ON DELETE CASCADE,
	CONSTRAINT D_APPLY_OFKDISCOUNT
		FOREIGN KEY (discount_id)
			REFERENCES DISCOUNT(discount_id)
			ON DELETE CASCADE,
	CONSTRAINT D_APPLY_OPK
		PRIMARY KEY(order_id, discount_id)
);

CREATE TABLE DINE_IN_ORDER (
	order_id INT,
	table_number INT(4),
	CONSTRAINT DINE_IN_ORDERFKSUPER
		FOREIGN KEY (order_id)
			REFERENCES ORDER_(order_id)
			ON DELETE CASCADE,
	CONSTRAINT DINE_IN_ORDERPK
		PRIMARY KEY (order_id)
);

CREATE TABLE SEAT_NUMBER (
	order_id INT,
	seat_number INT(4),
	CONSTRAINT SEAT_NUMBERFKSUPER
		FOREIGN KEY (order_id)
			REFERENCES DINE_IN_ORDER(order_id)
			ON DELETE CASCADE,
	CONSTRAINT PRIMARY KEY (order_id, seat_number)
);

CREATE TABLE PICKUP_CUSTOMER (
	customer_id INT,
	name VARCHAR(50) NOT NULL,
	phone_number VARCHAR(20) NOT NULL,
	CONSTRAINT PICKUP_CUSTOMERPK
		PRIMARY KEY (customer_id)
);

CREATE TABLE DELIVERY_CUSTOMER (
	customer_id INT,	
	address VARCHAR(70) NOT NULL,
	CONSTRAINT DELIVERY_CUSTOMERFKSUPER
		FOREIGN KEY (customer_id)
			REFERENCES PICKUP_CUSTOMER(customer_id)
			ON DELETE CASCADE,
	CONSTRAINT DELIVER_CUSTOMERPK
		PRIMARY KEY (customer_id)
);

CREATE TABLE PICKUP_ORDER (
	order_id INT,
	customer_id INT,
	CONSTRAINT PICKUP_ORDERFKCUSTOMER
		FOREIGN KEY (customer_id)
			REFERENCES PICKUP_CUSTOMER(customer_id)
			ON DELETE SET NULL,
	CONSTRAINT PICKUP_ORDERFKSUPER
		FOREIGN KEY (order_id)
			REFERENCES ORDER_(order_id)
			ON DELETE CASCADE,
	CONSTRAINT PICKUP_ORDERPK
		PRIMARY KEY (order_id)
);

CREATE TABLE DELIVERY_ORDER (
	order_id INT,
	customer_id INT,
	CONSTRAINT DELIVER_ORDERFKCUSTOMER
		FOREIGN KEY (customer_id)
			REFERENCES DELIVERY_CUSTOMER(customer_id)
			ON DELETE SET NULL,
	CONSTRAINT DELIVERY_ORDERFKSUPER
		FOREIGN KEY (order_id)
			REFERENCES ORDER_(order_id)
			ON DELETE CASCADE,
	CONSTRAINT DELIVERY_ORDERPK
		PRIMARY KEY (order_id)
);