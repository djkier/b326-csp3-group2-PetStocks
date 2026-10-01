-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 01, 2026 at 07:58 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `petstock_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `is_archived` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`, `is_archived`) VALUES
(1, 'Food', 'Edible products such as dry food, wet food, treats, and snacks', 0),
(2, 'Toys', 'Products used for play, exercise, and pet stimulation', 0),
(3, 'Accessories', 'Items such as collars, leashes, bowls, beds, carriers, and clothing', 0),
(4, 'Grooming', 'Products used for cleaning, brushing, trimming, and general grooming', 0),
(5, 'Health and Wellness', 'Vitamins, supplements, hygiene products, and basic pet care items', 0),
(6, 'Housing', 'Cages, tanks, enclosures, kennels, and other pet housing products', 0),
(7, 'Training', 'Products used for behavior training and pet discipline', 0),
(8, 'Cleaning Supplies', 'Products used for cleaning pet areas, cages, tanks, and accessories', 0),
(9, 'Feeding Supplies', NULL, 1),
(10, 'Travel Supplies', 'Products used when transporting or traveling with pets', 1);

-- --------------------------------------------------------

--
-- Table structure for table `dispatches`
--

CREATE TABLE `dispatches` (
  `id` int(11) NOT NULL,
  `inventory_id` int(11) NOT NULL,
  `datetime_dispatched` datetime NOT NULL,
  `quantity_dispatched` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `dispatches`
--

INSERT INTO `dispatches` (`id`, `inventory_id`, `datetime_dispatched`, `quantity_dispatched`, `user_id`) VALUES
(1, 1, '2026-09-10 10:00:00', 5, 5),
(2, 1, '2026-09-20 14:15:00', 5, 6),
(3, 2, '2026-09-18 11:20:00', 5, 5),
(4, 3, '2026-09-15 09:30:00', 10, 6),
(5, 4, '2026-09-17 16:00:00', 5, 5),
(6, 8, '2026-09-22 13:30:00', 8, 6),
(7, 10, '2026-09-24 10:45:00', 5, 5),
(8, 15, '2026-09-25 15:00:00', 3, 6),
(9, 1, '2026-10-01 10:57:15', 2, 5),
(10, 1, '2026-10-01 12:52:35', 1, 5),
(11, 1, '2026-10-01 13:40:40', 1, 5);

-- --------------------------------------------------------

--
-- Table structure for table `inventories`
--

CREATE TABLE `inventories` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `expiration` date DEFAULT NULL,
  `batch_code` varchar(100) NOT NULL,
  `remark` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `inventories`
--

INSERT INTO `inventories` (`id`, `product_id`, `quantity`, `expiration`, `batch_code`, `remark`) VALUES
(1, 1, 41, '2027-05-15', 'CDF-2026-001', 'Regular stock'),
(2, 1, 15, '2027-01-20', 'CDF-2026-002', 'Older batch'),
(3, 2, 30, '2027-03-10', 'TWF-2026-001', NULL),
(4, 3, 25, NULL, 'CB-2026-001', 'Non-expiring item'),
(5, 4, 18, NULL, 'FW-2026-001', NULL),
(6, 5, 20, NULL, 'AC-2026-001', NULL),
(7, 6, 35, NULL, 'SFB-2026-001', 'Non-expiring item'),
(8, 7, 12, '2027-06-30', 'PS-2026-001', NULL),
(9, 8, 8, NULL, 'PB-2026-001', 'Low stock'),
(10, 9, 10, '2026-12-15', 'MVD-2026-001', 'Nearer expiration'),
(11, 10, 16, '2027-02-28', 'FTS-2026-001', NULL),
(12, 11, 6, NULL, 'SAC-2026-001', 'Low stock'),
(13, 12, 14, NULL, 'TC-2026-001', NULL),
(14, 13, 22, '2027-08-01', 'PAD-2026-001', NULL),
(15, 14, 9, '2027-04-20', 'AQC-2026-001', 'Low stock');

-- --------------------------------------------------------

--
-- Table structure for table `pet_types`
--

CREATE TABLE `pet_types` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `is_archived` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pet_types`
--

INSERT INTO `pet_types` (`id`, `name`, `description`, `is_archived`) VALUES
(1, 'Dog', 'Labrador, Shih Tzu, German Shepherd', 0),
(2, 'Cat', 'Persian, Siamese, British Shorthair', 0),
(3, 'Bird', 'Parakeet, Cockatiel, Lovebird', 0),
(4, 'Fish', NULL, 0),
(5, 'Rabbit', 'Holland Lop, Lionhead, Mini Rex', 0),
(6, 'Hamster', 'Syrian, Dwarf, Roborovski', 0),
(7, 'Guinea Pig', 'American, Abyssinian, Peruvian', 1),
(8, 'Reptile', 'Gecko, Turtle, Bearded Dragon', 1);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `is_archived` tinyint(1) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `brand`, `description`, `is_archived`, `category_id`) VALUES
(1, 'Chicken Dry Food 1kg', 'PetChoice', 'Dry chicken-flavored pet food', 0, 1),
(2, 'Tuna Wet Food 400g', 'PetChoice', 'Wet tuna-flavored pet food', 0, 1),
(3, 'Chew Ball', 'PawPlay', 'Rubber ball used for play and chewing', 0, 2),
(4, 'Feather Wand', 'PawPlay', 'Interactive feather toy', 0, 2),
(5, 'Adjustable Collar', 'PetGear', 'Adjustable collar for household pets', 0, 3),
(6, 'Stainless Food Bowl', 'PetGear', 'Stainless steel feeding bowl', 0, 3),
(7, 'Pet Shampoo 250ml', 'CleanPaws', 'Shampoo used for cleaning pet fur', 0, 4),
(8, 'Pet Brush', 'CleanPaws', 'Brush used for grooming pet fur', 0, 4),
(9, 'Multivitamin Drops', 'PetHealth', 'Vitamin supplement for pets', 0, 5),
(10, 'Flea and Tick Spray', 'PetHealth', 'Spray used for flea and tick control', 0, 5),
(11, 'Small Animal Cage', 'PetHome', 'Housing cage for small household pets', 0, 6),
(12, 'Training Clicker', 'PetTrainer', 'Handheld clicker used for pet training', 0, 7),
(13, 'Pet Area Disinfectant', 'CleanPaws', 'Cleaning solution for pet areas and equipment', 0, 8),
(14, 'Aquarium Cleaner', 'AquaCare', 'Cleaning solution for aquarium maintenance', 0, 8),
(15, 'Old Pet Carrier', 'PetGear', 'Pet carrier previously sold by the store', 1, 10);

-- --------------------------------------------------------

--
-- Table structure for table `product_pet_types`
--

CREATE TABLE `product_pet_types` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `pet_type_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_pet_types`
--

INSERT INTO `product_pet_types` (`id`, `product_id`, `pet_type_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 2),
(4, 3, 1),
(5, 4, 2),
(6, 5, 1),
(7, 5, 2),
(8, 6, 1),
(9, 6, 2),
(10, 6, 5),
(11, 6, 6),
(12, 7, 1),
(13, 7, 2),
(14, 8, 1),
(15, 8, 2),
(16, 8, 5),
(17, 9, 1),
(18, 9, 2),
(19, 9, 3),
(20, 10, 1),
(21, 10, 2),
(22, 11, 5),
(23, 11, 6),
(24, 12, 1),
(25, 12, 2),
(26, 13, 1),
(27, 13, 2),
(28, 13, 3),
(29, 13, 5),
(30, 13, 6),
(31, 14, 4),
(32, 15, 1),
(33, 15, 2);

-- --------------------------------------------------------

--
-- Table structure for table `restocks`
--

CREATE TABLE `restocks` (
  `id` int(11) NOT NULL,
  `inventory_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `datetime_delivered` datetime NOT NULL DEFAULT current_timestamp(),
  `quantity_delivered` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `restocks`
--

INSERT INTO `restocks` (`id`, `inventory_id`, `supplier_id`, `datetime_delivered`, `quantity_delivered`, `user_id`) VALUES
(1, 1, 1, '2026-09-01 09:00:00', 50, 5),
(2, 2, 1, '2026-08-15 10:30:00', 20, 6),
(3, 3, 2, '2026-09-03 11:00:00', 40, 5),
(4, 4, 3, '2026-09-05 13:15:00', 30, 6),
(5, 8, 2, '2026-09-07 09:45:00', 20, 5),
(6, 10, 2, '2026-09-10 14:00:00', 15, 5),
(7, 15, 4, '2026-09-12 15:30:00', 12, 6),
(8, 1, 3, '2026-10-01 11:45:08', 5, 5);

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(200) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `is_archived` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`id`, `name`, `address`, `contact_number`, `email`, `is_archived`) VALUES
(1, 'PetSource Trading', 'Quezon City, Metro Manila', '09171234567', 'petsource@example.com', 0),
(2, 'AnimalCare Distribution', 'Caloocan City, Metro Manila', '09182345678', 'animalcare@example.com', 0),
(3, 'PawMart Wholesale', 'Manila, Metro Manila', '09193456789', 'pawmart@example.com', 0),
(4, 'AquaPet Supplies', 'Pasig City, Metro Manila', '09204567890', 'aquapet@example.com', 0),
(5, 'Old Pet Supply Co.', 'Makati City, Metro Manila', '09215678901', NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password_hash`, `role`) VALUES
(4, 'admin', '$2a$10$VFm5GYRSYIEh.sMbEmpOo.zEbXaHFa.NasvVuUKYgO4UDKVXkz9X.', 'ADMIN'),
(5, 'staff01', '$2a$10$U9eOAhMKu1nmCDX/shx2z.mRk3yfhdduTh9N2GtoU8wZRZguWQrKC', 'STAFF'),
(6, 'staff02', '$2a$10$1JpiM6bkJn/Sbey.uskymutYSP8jCIy8XO63T5RAbABz0.vqK99ee', 'STAFF');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `dispatches`
--
ALTER TABLE `dispatches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_dispatches_inventories` (`inventory_id`),
  ADD KEY `fk_dispatches_users` (`user_id`);

--
-- Indexes for table `inventories`
--
ALTER TABLE `inventories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_inventories_products` (`product_id`);

--
-- Indexes for table `pet_types`
--
ALTER TABLE `pet_types`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_products_categories` (`category_id`);

--
-- Indexes for table `product_pet_types`
--
ALTER TABLE `product_pet_types`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_product_pet_types_products` (`product_id`),
  ADD KEY `fk_product_pet_types_pet_types` (`pet_type_id`);

--
-- Indexes for table `restocks`
--
ALTER TABLE `restocks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_restocks_inventories` (`inventory_id`),
  ADD KEY `fk_restocks_suppliers` (`supplier_id`),
  ADD KEY `fk_restocks_users` (`user_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `dispatches`
--
ALTER TABLE `dispatches`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `inventories`
--
ALTER TABLE `inventories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `pet_types`
--
ALTER TABLE `pet_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `product_pet_types`
--
ALTER TABLE `product_pet_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `restocks`
--
ALTER TABLE `restocks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `dispatches`
--
ALTER TABLE `dispatches`
  ADD CONSTRAINT `fk_dispatches_inventories` FOREIGN KEY (`inventory_id`) REFERENCES `inventories` (`id`),
  ADD CONSTRAINT `fk_dispatches_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `inventories`
--
ALTER TABLE `inventories`
  ADD CONSTRAINT `fk_inventories_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_products_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Constraints for table `product_pet_types`
--
ALTER TABLE `product_pet_types`
  ADD CONSTRAINT `fk_product_pet_types_pet_types` FOREIGN KEY (`pet_type_id`) REFERENCES `pet_types` (`id`),
  ADD CONSTRAINT `fk_product_pet_types_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `restocks`
--
ALTER TABLE `restocks`
  ADD CONSTRAINT `fk_restocks_inventories` FOREIGN KEY (`inventory_id`) REFERENCES `inventories` (`id`),
  ADD CONSTRAINT `fk_restocks_suppliers` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`),
  ADD CONSTRAINT `fk_restocks_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Nullable optional fields (also apply these statements to existing databases)
--
ALTER TABLE `categories`
  MODIFY COLUMN `description` varchar(250) NULL;

ALTER TABLE `inventories`
  MODIFY COLUMN `expiration` date NULL,
  MODIFY COLUMN `remark` varchar(250) NULL;

ALTER TABLE `pet_types`
  MODIFY COLUMN `description` varchar(250) NULL;

ALTER TABLE `products`
  MODIFY COLUMN `description` varchar(250) NULL;

ALTER TABLE `suppliers`
  MODIFY COLUMN `email` varchar(50) NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
