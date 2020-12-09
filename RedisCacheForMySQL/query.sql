select * from rosebowl where date = '1999-01-01';
select * from rosebowl where year(date) = '1999';
select count(*) from rosebowl where winner = 'Wisconsin'; 
select date, winner, opponent from rosebowl where winner = 'Wisconsin'; 
select count(*) as wins, winner from rosebowl group by winner order by wins desc;
select count(*) as loss, opponent from rosebowl group by opponent order by loss desc;
select date, winner, opponent from rosebowl where winner = 'Wisconsin' and year(date) between 1998 and 2001;



Insert into rosebowl values("1990-01-01","USC","Michigan");
Insert into rosebowl values("1989-01-02","Michigan","USC");
