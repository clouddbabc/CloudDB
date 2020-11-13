create table if not exists public.onlineretail
    (invoice char(8), 
     stockcode varchar(18), 
     description varchar(64), 
     quantity int, 
     invoiceDate timestamp, 
     price numeric(10,2),
     customerid int, 
     country varchar(32));
