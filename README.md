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

## 
