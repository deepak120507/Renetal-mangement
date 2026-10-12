CREATE DATABASE IF NOT EXISTS rental_house_db;
USE rental_house_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    role ENUM('TENANT', 'OWNER', 'ADMIN') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS properties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    property_type ENUM('APARTMENT', 'HOUSE', 'VILLA', 'STUDIO', 'DUPLEX', 'COMMERCIAL') NOT NULL,
    monthly_rent DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL,
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    area INT NOT NULL,
    furnishing_status ENUM('FURNISHED', 'SEMI_FURNISHED', 'UNFURNISHED') NOT NULL,
    availability BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500),
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_properties_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_property_location (location),
    INDEX idx_property_type (property_type),
    INDEX idx_property_availability (availability)
);

CREATE TABLE IF NOT EXISTS rental_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    move_in_date DATE NOT NULL,
    message TEXT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_rental_tenant FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rental_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    INDEX idx_rental_status (status)
);

CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('NEW', 'READ', 'REPLIED') NOT NULL DEFAULT 'NEW',
    CONSTRAINT fk_contact_tenant FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_contact_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_contact_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

INSERT INTO users (full_name, email, phone, password, address, role, status, created_at)
VALUES 
(
    'System Administrator',
    'admin@rentalhouse.com',
    '+1234567890',
    '$2a$10$8xZJZpZc6z5MUMc3H6R2UO3w7QYdMBzHq7xq6R3M6yBv5g4W2ccmG',
    'Head Office',
    'ADMIN',
    'ACTIVE',
    NOW()
),
(
    'Sarah Owner',
    'owner@example.com',
    '+1122334455',
    '$2a$10$8xZJZpZc6z5MUMc3H6R2UO3w7QYdMBzHq7xq6R3M6yBv5g4W2ccmG',
    '456 Elm Street',
    'OWNER',
    'ACTIVE',
    NOW()
),
(
    'John Tenant',
    'tenant@example.com',
    '+1987654321',
    '$2a$10$8xZJZpZc6z5MUMc3H6R2UO3w7QYdMBzHq7xq6R3M6yBv5g4W2ccmG',
    '123 Baker Street',
    'TENANT',
    'ACTIVE',
    NOW()
)
ON DUPLICATE KEY UPDATE email = email;

-- Sample properties owned by Sarah Owner
INSERT INTO properties (title, description, address, city, state, location, property_type, monthly_rent, security_deposit, bedrooms, bathrooms, area, furnishing_status, availability, image_url, owner_id)
SELECT 
    'Luxury 2BHK Apartment',
    'Spacious apartment with balcony and modern amenities in the heart of Downtown.',
    '101 Skyline Towers',
    'New York',
    'NY',
    'Downtown',
    'APARTMENT',
    2400.00,
    4800.00,
    2,
    2,
    1100,
    'FURNISHED',
    TRUE,
    'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=960&q=80',
    u.id
FROM users u WHERE u.email = 'owner@example.com'
LIMIT 1;

INSERT INTO properties (title, description, address, city, state, location, property_type, monthly_rent, security_deposit, bedrooms, bathrooms, area, furnishing_status, availability, image_url, owner_id)
SELECT 
    'Modern 3BHK Villa',
    'Beautiful private villa with garden, garage, and quiet neighborhood.',
    '55 Green Meadows Road',
    'Austin',
    'TX',
    'Suburbs',
    'VILLA',
    3500.00,
    7000.00,
    3,
    3,
    2200,
    'SEMI_FURNISHED',
    TRUE,
    'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=960&q=80',
    u.id
FROM users u WHERE u.email = 'owner@example.com'
LIMIT 1;

INSERT INTO properties (title, description, address, city, state, location, property_type, monthly_rent, security_deposit, bedrooms, bathrooms, area, furnishing_status, availability, image_url, owner_id)
SELECT 
    'Cozy Studio Apartment',
    'Perfect for students or professionals. Close to metro station and restaurants.',
    '88 Central Ave',
    'Chicago',
    'IL',
    'Midtown',
    'STUDIO',
    1200.00,
    2400.00,
    1,
    1,
    550,
    'FURNISHED',
    TRUE,
    'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=960&q=80',
    u.id
FROM users u WHERE u.email = 'owner@example.com'
LIMIT 1;

SELECT 'Database initialized successfully with sample users and properties';

