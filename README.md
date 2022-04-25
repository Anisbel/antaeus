## Solution exercice
The main idea of my solution is to consider the whole process as a state machine. 
<a href="https://ibb.co/v11G6HY"><img src="https://i.ibb.co/y001cXS/Screen-Shot-2022-04-23-at-6-07-59-PM.png" alt="Screen-Shot-2022-04-23-at-6-07-59-PM" border="0" /></a>
- PENDING to PROGRESS : happens when a transaction is being handled (this is so that we don't charge twice a costumer if our server gets down while provider.charge() has charged the client.
- PROGRESS to FAILED : happens when a customer doesn't have enough money or any business-related reason. In this case, I retry once a day.
- PROGRESS to ERROR : this happens when we have an exception that doesn't allow us to charge the client. Depending on the ERROR, we either :
--     ERROR_NETWORK: retry 3 times for transient error 
--     ERROR_CURRENCY: retry once with the right currency value
--     ERROR_NOT_FOUND: don't retry. need Investigation ( wrong data input, etc.)

## Implementation
- I have exposed a rest endpoint to process a single Invoice (test purpose)
- I used the fixedRateTimer from the Kotlin library to schedule a process
- retries have been implemented using try-catch statement

## Improvement
- Should Add Multi-threading to handle many invoices at the same time if perfomance is an issue.
- Unit-test should be more exhaustive.
- Although this solution works well, in a production environnement, I would have used a Pub/Sub mechanism like Kafka to handle an invoice as it's known to be fault-tolerant and has a good mechanism to replay transactions instead of implementing my own try-catch-retry mechanism.
## Discussion
- I had fun playing with this exercise. It took me the  weekend to : set up the environment,implement/run/test/document it. 


## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!
