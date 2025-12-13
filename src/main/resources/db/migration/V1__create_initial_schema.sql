-- 1. Create Passenger Table
CREATE TABLE IF NOT EXISTS passenger (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         name VARCHAR(255),
                                         email VARCHAR(255),
                                         password VARCHAR(255),
                                         phone_number VARCHAR(255),
                                         created_at DATETIME(6),
                                         updated_at DATETIME(6),
                                         PRIMARY KEY (id)
);

-- 2. Create Driver Table
CREATE TABLE IF NOT EXISTS driver (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      name VARCHAR(255),
                                      license_number VARCHAR(255),
                                      created_at DATETIME(6),
                                      updated_at DATETIME(6),
                                      PRIMARY KEY (id)
);

-- 3. Create Booking Table
CREATE TABLE IF NOT EXISTS booking (
                                       id BIGINT NOT NULL AUTO_INCREMENT,
                                       booking_status VARCHAR(255),
                                       start_time DATETIME(6),
                                       end_time DATETIME(6),
                                       total_distance BIGINT,
                                       passenger_id BIGINT,
                                       driver_id BIGINT,
                                       created_at DATETIME(6),
                                       updated_at DATETIME(6),
                                       PRIMARY KEY (id),
                                       CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES passenger(id),
                                       CONSTRAINT fk_booking_driver FOREIGN KEY (driver_id) REFERENCES driver(id)
);

-- 4. Create Review Table
-- (Assuming this is the new structure you wanted where Review is linked to Booking)
CREATE TABLE IF NOT EXISTS review (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      content VARCHAR(255),
                                      rating DOUBLE,
                                      booking_id BIGINT,  -- This handles the "shifting mapping" you were trying to do
                                      created_at DATETIME(6),
                                      updated_at DATETIME(6),
                                      PRIMARY KEY (id),
                                      CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
);