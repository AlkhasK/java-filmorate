# java-filmorate
## Database schema
![Untitled (1)](https://user-images.githubusercontent.com/108098535/203623670-b49a883b-abc8-4528-aaa2-5414ad6b5529.png)
***Get all films:***
```sql
SELECT *
FROM film;
```
***Get film by id:***
```sql
SELECT *
FROM film
WHERE id = value_id;
```
***Create film:***
```sql
INSERT INTO film (name, description, release_date, duration, genre, rating)
VALUES(value_name, value_description, value_release_date, value_duration, value_genre, value_rating);
```
***Update film:***
```sql
UPDATE film
SET name = value_name, description = value_description, release_date = value_release_date, duration = value_duration, genre = value_genre, rating = value_rating
WHERE id = value_id;
```

***Delete film:***
```sql
DELETE FROM film 
WHERE id = value_id;
```

***Get all likes:***
```sql
SELECT *
FROM like;
```

***Get like by id:***
```sql
SELECT *
FROM like
WHERE film_id = value_film_id 
      AND user_id = value_user_id;
```

***Get likes for film:***
```sql
SELECT *
FROM like
WHERE film_id = value_film_id;
```

***Create like:***
```sql
INSERT INTO like (film_id, user_id)
VALUES(value_film_id, value_user_id);
```

***Delete like:***
```sql
DELETE FROM like 
WHERE film_id = value_film_id 
      AND user_id = value_user_id;
```
	  
***Top N films:***
```sql
SELECT f.*
FROM film AS f
LEFT JOIN like AS l ON l.fim_id = f.id
GROUP BY f.id
ORDER BY COUNT(l.user_id) DESC
LIMIT value_N;
```
***Get all users:***
```sql
SELECT *
FROM user;
```

***Get user by id:***
```sql
SELECT *
FROM user
WHERE id = value_id;
```

***Create user:***
```sql
INSERT INTO film (email, login, name, birthday)
VALUES(value_email, value_login, value_name, value_birthday);
```

***Update user:***
```sql
UPDATE user
SET email = value_email, login = value_login, name = value_name, birthday = value_birthday
WHERE id = value_id;
```

***Delete user:***
```sql
DELETE FROM user
WHERE id = value_id;
```

***Get all friends for user:***
```sql
SELECT *
FROM friend
WHERE (user_one_id = value_id
      OR user_two_id = value_id) 
	  AND is_approved = true;
```

***Add friends:***
```sql
INSERT INTO friend (user_one_id, user_two_id, is_approved)
VALUES(value_user_one_id, value_user_two_id, value_is_approved);
```

***Delete friends:***
```sql
DELETE FROM friend
WHERE (user_one_id = value_user_one_id 
       AND user_two_id = value_user_two_id)
	   OR (user_one_id = value_user_two_id 
       AND user_two_id = value_user_one_id);
```

***Get friend:***
```sql
SELET *
FROM friend
WHERE user_one_id = value_user_one_id 
      AND user_two_id = value_user_two_id;
```

***Get common friends user1 and user2:***
```sql
(SELECT user_two_id
FROM friend
WHERE user_one_id = user1_id
UNION
SELECT user_one_id
FROM friend
WHERE user_two_id = user1_id)
INTERSECT
(SELECT user_two_id
FROM friend
WHERE user_one_id = user2_id
UNION
SELECT user_one_id
FROM friend
WHERE user_two_id = user2_id);
```

