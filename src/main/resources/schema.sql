CREATE TABLE IF NOT EXISTS outsourcingDB.users
(
    id           BIGINT AUTO_INCREMENT,
    type         VARCHAR(31)  NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    profile_url  VARCHAR(255) NULL,
    role         VARCHAR(31)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    deleted_at   DATETIME(6)  NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_phone_number UNIQUE (phone_number)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.customers
(
    id       BIGINT       NOT NULL,
    nickname VARCHAR(255) NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id),
    CONSTRAINT uq_customers_nickname UNIQUE (nickname),
    CONSTRAINT fk_customers_users_id FOREIGN KEY (id)
        REFERENCES outsourcingDB.users (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.addresses
(
    id          BIGINT AUTO_INCREMENT,
    address     VARCHAR(255) NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    customer_id BIGINT       NOT NULL,
    CONSTRAINT pk_addresses PRIMARY KEY (id),
    CONSTRAINT fk_addresses_customers_id FOREIGN KEY (customer_id)
        REFERENCES outsourcingDB.customers (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.owners
(
    id                BIGINT       NOT NULL,
    store_count       INT          NOT NULL,
    constant_nickname VARCHAR(255) NOT NULL,
    CONSTRAINT pk_owners PRIMARY KEY (id),
    CONSTRAINT fk_owners_users_id FOREIGN KEY (id)
        REFERENCES outsourcingDB.users (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.stores
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    store_name         VARCHAR(255)          NOT NULL,
    store_address      VARCHAR(255)          NOT NULL,
    store_phone_number VARCHAR(255)          NOT NULL,
    store_profile_url  VARCHAR(255)          NULL,
    store_status       VARCHAR(31)           NOT NULL,
    min_price          DECIMAL(38, 2)        NOT NULL,
    opened_at          TIME(6)               NOT NULL,
    closed_at          TIME(6)               NOT NULL,
    created_at         DATETIME(6)           NOT NULL,
    updated_at         DATETIME(6)           NOT NULL,
    owner_id           BIGINT                NOT NULL,
    CONSTRAINT pk_stores PRIMARY KEY (id),
    CONSTRAINT uq_stores_store_name UNIQUE (store_name),
    CONSTRAINT uq_stores_store_address UNIQUE (store_address),
    CONSTRAINT uq_stores_store_phone_number UNIQUE (store_phone_number),
    CONSTRAINT fk_stores_owners_id FOREIGN KEY (owner_id) REFERENCES outsourcingDB.owners (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.menus
(
    id          BIGINT AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    price       INT          NOT NULL,
    description VARCHAR(255) NULL,
    image_url   VARCHAR(255) NULL,
    status      VARCHAR(20)  NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    deleted_at  DATETIME(6)  NULL,
    store_id    BIGINT       NOT NULL,
    CONSTRAINT pk_menus PRIMARY KEY (id),
    CONSTRAINT fk_menus_stores_id FOREIGN KEY (store_id) REFERENCES outsourcingDB.stores (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.orders
(
    id               BIGINT AUTO_INCREMENT,
    total_price      INT          NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    status           VARCHAR(31)  NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    store_id         BIGINT       NOT NULL,
    user_id          BIGINT       NOT NULL,
    CONSTRAINT fk_orders_users_id FOREIGN KEY (user_id) REFERENCES outsourcingDB.users (id),
    CONSTRAINT fk_orders_stores_id FOREIGN KEY (store_id) REFERENCES outsourcingDB.stores (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.order_items
(
    id       BIGINT AUTO_INCREMENT,
    price    INT    NOT NULL,
    quantity INT    NOT NULL,
    menu_id  BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_orders_id FOREIGN KEY (order_id) REFERENCES outsourcingDB.orders (id),
    CONSTRAINT fk_order_items_menus_id FOREIGN KEY (menu_id) REFERENCES outsourcingDB.menus (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.reviews
(
    id         BIGINT AUTO_INCREMENT,
    content    VARCHAR(500) NOT NULL,
    rating     INT          NOT NULL,
    user_id    BIGINT       NOT NULL,
    order_id   BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT uq_reviews_order_id UNIQUE (order_id),
    CONSTRAINT fk_reviews_users_id FOREIGN KEY (user_id) REFERENCES outsourcingDB.users (id),
    CONSTRAINT fk_reviews_orders_id FOREIGN KEY (order_id) REFERENCES outsourcingDB.orders (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.images
(
    id         BIGINT AUTO_INCREMENT,
    image_url  VARCHAR(255) NULL,
    review_id  BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    CONSTRAINT pk_images PRIMARY KEY (id),
    CONSTRAINT fk_images_reviews_id FOREIGN KEY (review_id) REFERENCES outsourcingDB.reviews (id)
);

CREATE TABLE IF NOT EXISTS outsourcingDB.review_replies
(
    id         BIGINT AUTO_INCREMENT,
    content    VARCHAR(500) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    owner_id   BIGINT       NOT NULL,
    review_id  BIGINT       NOT NULL,
    CONSTRAINT pk_review_replies PRIMARY KEY (id),
    CONSTRAINT uq_review_replies_review_id UNIQUE (review_id),
    CONSTRAINT fk_review_replies_users_id FOREIGN KEY (owner_id) REFERENCES outsourcingDB.users (id),
    CONSTRAINT fk_review_replies_reviews_id FOREIGN KEY (review_id) REFERENCES outsourcingDB.reviews (id)
);
