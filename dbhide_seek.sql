-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 26, 2025 at 03:57 PM
-- Server version: 8.4.3
-- PHP Version: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbhide_seek`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbenefit`
--

CREATE TABLE `tbenefit` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `skor` int DEFAULT '0',
  `peluru_meleset` int DEFAULT '0',
  `sisa_peluru` int DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tbenefit`
--

INSERT INTO `tbenefit` (`id`, `username`, `skor`, `peluru_meleset`, `sisa_peluru`) VALUES
(11, 'aji', 3160, 765, 16),
(12, 'test', 0, 0, 0),
(13, 'test2', 400, 102, 52),
(14, 'test3', 50, 14, 8),
(15, 'test4', 80, 18, 8);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbenefit`
--
ALTER TABLE `tbenefit`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbenefit`
--
ALTER TABLE `tbenefit`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
