create table binary_contents
(
    created_at   timestamp(6) with time zone not null,
    size         bigint                      not null,
    id           uuid                        not null
        primary key,
    content_type varchar(100)                not null,
    file_name    varchar(255)                not null,
    bytes        bytea                       not null
);

alter table binary_contents
    owner to discodeit_user;

create table channels
(
    created_at  timestamp(6) with time zone not null,
    updated_at  timestamp(6) with time zone,
    type        varchar(10)                 not null
        constraint channels_type_check
            check ((type)::text = ANY ((ARRAY ['PUBLIC'::character varying, 'PRIVATE'::character varying])::text[])),
    id          uuid                        not null
        primary key,
    name        varchar(100),
    description varchar(500)
);

alter table channels
    owner to discodeit_user;

create table users
(
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    id         uuid                        not null
        primary key,
    profile_id uuid
        unique
        constraint fktbudycgrip49xdptogmhfqnso
            references binary_contents
            on delete set null,
    username   varchar(50)                 not null
        unique,
    email      varchar(100)                not null
        unique,
    password   varchar(255)                not null
);

alter table users
    owner to discodeit_user;

create table messages
(
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone,
    author_id  uuid
        constraint fkowtlim26svclkatusptbgi7u1
            references users
            on delete set null,
    channel_id uuid                        not null
        constraint fk3u3ckbhwq9se1cmopk2pq05b2
            references channels
            on delete cascade,
    id         uuid                        not null
        primary key,
    content    text
);

alter table messages
    owner to discodeit_user;

create table message_attachments
(
    attachment_id uuid not null
        unique
        constraint fksd1m8rb8jcpbcnb7rpdue7ctc
            references binary_contents
            on delete cascade,
    message_id    uuid not null
        constraint fkj7twd218e2gqw9cmlhwvo1rth
            references messages
            on delete cascade,
    primary key (attachment_id, message_id)
);

alter table message_attachments
    owner to discodeit_user;

create table read_statuses
(
    created_at   timestamp(6) with time zone not null,
    last_read_at timestamp(6) with time zone not null,
    updated_at   timestamp(6) with time zone,
    channel_id   uuid
        constraint fka38ri44ml4gfdpklx4ahqr8gd
            references channels
            on delete cascade,
    id           uuid                        not null
        primary key,
    user_id      uuid
        constraint fkml3k4wr9sj5yxrmj6d0aoib2e
            references users
            on delete cascade,
    constraint uk_read_status_user_channel
        unique (user_id, channel_id)
);

alter table read_statuses
    owner to discodeit_user;

create table user_statuses
(
    created_at     timestamp(6) with time zone not null,
    last_active_at timestamp(6) with time zone not null,
    updated_at     timestamp(6) with time zone,
    id             uuid                        not null
        primary key,
    user_id        uuid
        unique
        constraint fk4lfl3ei2ubchgcxrrpo3pw4mm
            references users
            on delete cascade
);

alter table user_statuses
    owner to discodeit_user;

