-- MySQL schema for Online Food Ordering System
CREATE DATABASE IF NOT EXISTS ofos_db;
USE ofos_db;

-- Users (very simple)
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  role ENUM('USER','ADMIN') DEFAULT 'USER'
);

-- Menu items
CREATE TABLE IF NOT EXISTS menu_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(500),
  price DECIMAL(10,2) NOT NULL,
  available BOOLEAN DEFAULT TRUE
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items
CREATE TABLE IF NOT EXISTS order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  menu_item_id INT NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Sample data
INSERT INTO users (username, password, role) VALUES
('user1','password','USER'),
('admin','admin123','ADMIN');

INSERT INTO menu_items (name, description, price, available) VALUES
('Cheese Burger','Tasty cheese burger',120.00,TRUE),
('Fried Chicken','Crispy fried chicken',200.00,TRUE),
('Veg Pizza','Cheesy veg pizza',250.00,TRUE);
