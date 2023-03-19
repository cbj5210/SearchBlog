/* SEARCH_COUNT */
DROP TABLE IF EXISTS search_count;

CREATE TABLE search_count
(
    keyword VARCHAR(100)  not null,
    count   int default 0 not null,
    constraint search_count_pk
        primary key (keyword)
);

CREATE INDEX search_count_count_index on search_count (count desc);

/* SEARCH_HISTORY */
DROP TABLE IF EXISTS search_history;

CREATE TABLE search_history
(
    id       int auto_increment,
    keyword  VARCHAR(100) not null,
    datetime DATETIME     null,
    constraint search_history_pk
        primary key (id)
);