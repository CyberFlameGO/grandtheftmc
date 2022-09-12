/*****
** Table Description:
** Represents GTM gang information.
**
** id is primary auto increment so we can change gang names.
** server_key is the server that this gang is on
** name is the name of the gang
** owner is the uuid of the player that owns this gang
** 
** Reasoning for structure:
** PK is the (`id`) field, as every gang needs its own unique key. We
** index the server_key so we can lookup gangs by server_type.
*****/
CREATE TABLE IF NOT EXISTS gtm_gang(
id INT NOT NULL AUTO_INCREMENT, 
server_key VARCHAR(10) NOT NULL, 
name VARCHAR(36) NOT NULL, 
owner BINARY(16) NOT NULL, 
description VARCHAR(255) NOT NULL, 
max_members INT(11) NOT NULL, 
PRIMARY KEY (id), 
INDEX (server_key)
);


/*****
** Table Description:
** Represents GTM gang member information, every gang will 
** have multiple records, one for each member.
**
** gang_id is foreign key reference to gtm_gang table
** uuid is the uuid of the member
** 
** Reasoning for structure:
** PK is the (`gang_id`, `uuid`) pair, as a member can only exist in that gang once.
*****/
CREATE TABLE IF NOT EXISTS gtm_gang_member(
gang_id INT NOT NULL, 
uuid BINARY(16) NOT NULL,
role TINYINT(6) NOT NULL DEFAULT 0,
PRIMARY KEY (gang_id, uuid), 
FOREIGN KEY (gang_id) REFERENCES gtm_gang(id) ON DELETE CASCADE
);


/*****
** Table Description:
** Represents GTM gang relationships.
**
** gang_id is owner of the relationship
** other_id is the other gang in this relationship
** relation is the relationship they share
** 
** Reasoning for structure:
** PK is the (`gang_id`, `other_id`) pair, as a gang can only have
** ONE unique relationship between them.
*****/
CREATE TABLE IF NOT EXISTS gtm_gang_relation(
gang_id INT NOT NULL, 
other_id INT NOT NULL, 
relation VARCHAR(10), 
PRIMARY KEY (gang_id, other_id), 
FOREIGN KEY (gang_id) REFERENCES gtm_gang(id) ON DELETE CASCADE, 
FOREIGN KEY (other_id) REFERENCES gtm_gang(id) ON DELETE CASCADE
);