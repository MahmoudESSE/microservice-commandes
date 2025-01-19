drop table if exists orders;
create table orders
(
    id           int auto_increment primary key,
    description  varchar(255) not null,
    quantity     int          not null,
    created_date timestamp    not null,
    price        float        not null,
    product_id   int          not null
);