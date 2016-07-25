- Province
create table Province(
    id integer PRIMARY KEY autoincrement,
     province_name text,
     province_code text
 )

- City
create table City(
    id integer PRIMARY KEY autoincrement,
    city_name text,
    city_code text,
    province_id integer
)

- County
create table County(
   id integer PRIMARY KEY autoincrement,
   county_name text,
   county_code text,
   city_id integer
)
