# ema
English Morphological Analyzer

This instructs how to integrate the English Morphological Analyzer
(ema) Java library.

************************************************************
I. Setting up the Environment
************************************************************

1. Download and install Java 8 (Other versions won't work).

2. Download and install Maven 3+. See

   https://maven.apache.org/what-is-maven.html

   for instructions if this is the first time you use maven.

3. Download and install Postgres 9.2 or higher version. If you are
   using Linux, you should already know how to install it. If you are
   using Windows, things become a little more complicated. In the
   latter case, see

   http://www.postgresql.org/download/

   for instructions.

4. Unzip the compressed file

   1). If you are using linux, the command is super simple:

         tar xf ema.jar

   2). If you are using windows, refer the following page for help:

         https://wiki.haskell.org/How_to_unpack_a_tar_file_in_Windows
   

5. After installing the posgres:

  1). Go to the folder containing the nlp.sql file, which is located
      right under ema root directory

  2) Log into postgres as the user called 'postgres':

     psql -U postgres

     When prompted for password, use the password you set

  3) Create a database named nlp:

     create database nlp

  4) Upload the database content to your newly created nlp database:

     \i nlp.sql

6. Change db.properties file which is located in

   /ema/src/main/resources/db.properties

   The following two lines need to be changed to use your settings:

   # change 'your-pass-word' to your actual password
   database.hibernate.password=your-pass-word

   # If you are using local database, then there is no need to change;
   # Otherwise, you need to change to the location of the server where
   # nlp database is located.
   database.hibernate.url=jdbc:postgresql://localhost:5432/nlp

7. Install ema maven library

   1). Go to the root directory of ema
       cd ema/

   2). Install it
       mvn clean install

8. That's it.

    If you have not used Maven, Postgres, Spring before, ask a guy
who is familiar with them for help.


************************************************************
II. How to use ema library.
************************************************************

     I wrote an example program called ema-client to illustrate how to
use it. By studying the sample code, you should be able to use ema
library.


************************************************************
III. Report any bugs you find
************************************************************

     Please report any bugs you find while you use it, which will help
to enhance the program.

Wish you great success in your research and hope ema can be of help.



Jiayun Han
PhD

February 29, 2016
Montreal, Canada





