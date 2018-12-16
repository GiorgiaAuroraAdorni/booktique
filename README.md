# Booktique

**Booktique** is a system that implements an online book store.

#### Repository

The source code is available on GitLab at
[https://gitlab.com/GiorgiaAuroraAdorni/3rdAssignment/](https://gitlab.com/GiorgiaAuroraAdorni/3rdAssignment/).

#### Contributors

This project has been developed by Giorgia Adorni (806787) .

## Installation

```
$ git clone https://gitlab.com/GiorgiaAuroraAdorni/3rdAssignment.git
$ cd booktique
$ docker-compose up
```

<!--Alternative to `docker-compose up` it's possible to rebuild the app using  `docker-compose up --build`.-->  
The application will be available at [http://localhost:8080/](http://localhost:8080/)
In addition, the `docker-compose.yml` file also starts an instance of pgAdmin to
access the database directly: you can reach it at [http://localhost:5432/](http://localhost:5432/).

Docker Compose also allows to locally run the unit tests:

```
$ docker-compose run app mvn test
```

## Application

Booktique is a system that implements an online book store. <!-- spiegazione generale -->   
The basic interface involves querying books according to the fields ISBN, title, author, publisher, language etc. We support services for buying books. We build a personal profile page which is used for handling customer orders. We implement a ”recommendation system” for recommending related books (prequels and sequels) based on those searched and purchased. 

#### Entities

The model is composed of 9 entities:

- **book** - items available in the online store. The book object provides a set of information such as title, ISBN, authors, publisher, format etc. A unique id is used as the primary key, despite this the ISBN provides a perfect primary key because every book has a different ISBN.

- **person** - a real person. The person object provides personal information such as name, surname, fiscal code, date of birth, email and telephone number. A unique id is used as a primary key, in addition the fiscal code is marked as a natural identifier as it identifies a person in the real world.

  <!-- If a **person** object is deleted, -->

- **author** - the author of the book. Because an author is also a person, the object inherits the same attributes of the person superclass. More information about the author is provided, for example an optional biography and the web site URL.

- **customer** – people who buy  books in the online store. Every customer has a basic set of data inherits from the person superclass such as for the authors. Mandatory attributes, such as login credentials and postal address, is added to customers, but can be also provided VAT number.

- **employee** – is a person who carries out activities related to orders. An employee has the same properties of its parent class, in addition he is associated whit another employee as a supervisor. More information about the employee is given, such as the login credentials, the hire date and his postal address.

- **supplier** - organization that provides book to the online store. To the supplier object is associated a unique id as primary key, the company name and other information such as email, telephone number and postal address.

- **item** – articles selected by the customer for purchase in online store. To each item is assigned the reference to the book in the catalog that the customer want to buy, its unit price, the desired quantity and its supplier.

- **purchase** - items ordered. To every purchase is associated a hypothetically unlimited number of items, the customer, the employee who takes charge of the order, order and shipping dates, the total amount,  order status and the transaction information (payment type and date).

- **address** - postal information about customer and supplier. In this object is stored the street address, the postal code, the city, region and country name.

## 
