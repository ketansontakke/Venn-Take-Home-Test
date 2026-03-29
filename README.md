# Venn Take Home Test Submission by Ketan Sontakke

This is a Spring Boot application that processes financial load transactions and enforces the following velocity limits:

- Max $5,000 per day
- Max $20,000 per week
- Max 3 loads per day

The application supports:
- Batch processing from an input file
- Real-time transaction processing via REST API
- Interactive API testing using Swagger 


## How to Run the Project After Cloning Locally

### 1. Run the Application

In Eclipse:
- Right-click `VennTakeHomeTestKetanSontakkeApplication.java` in src/main/java/com/example/demo.
- Click **Run As -> Spring Boot App**

Or via terminal:

`mvn spring-boot:run`

### 2. What Happens on Startup

The app loads transactions from:

src/main/resources/input/expectedInput.txt
These are processed and stored in an in-memory database

Output is written to:

data/output/actualOutput.txt

### 3. Once the app has finished processing the load transactions, open:

http://localhost:8080/swagger-ui.html

You can now:

- Submit new transactions
- See real-time responses
- Test edge cases easily

## Endpoints
### POST: /loads

**Example Request**
```
{
  "id": "15887",
  "customer_id": "528",
  "load_amount": "$112.24",
  "time": "2000-01-01T00:00:00Z"
}
```
**Example Responses**

Accepted Transaction
```
{
  "id": "15887",
  "customer_id": "528",
  "accepted": true
}
```
Rejected Transaction
```
{
  "id": "15887",
  "customer_id": "528",
  "accepted": false
}
```

## Tests
Unit tests were created with JUnit to verifiy the functionality of the application. The tests can be found in src/test/java. In Eclipse, you can right-click on the folder (src/test/java) and run as a JUnit test. I also created a test suite called AllTestsSuite.java under com.example.demo, and that can also be used to run all tests at once. You can also run `mvn test` in the terminal.

## In-Memory Database (H2)

This project uses H2 in-memory database as a simple, fast storage mechanism. It gives us the ability to validate load transactions as they are posted.

# Summary
I have created a simple backend processor that validates load transactions via an inputed text file. Then as the server remains open, the user is able to POST additional transactions via Swagger. The project structure is simple, and is easily extendable to accommodate new features. A user interface can now retrieve data from this backend application via a POST request. 

In a separate branch (return-rejection-reason), I did some work to show that a response code can be returned as reasoning for a rejected transaction, but this starts to over-complicate things and so I left it out of the main branch. However, it is an important consideration when a user interface wants more granularity to present to the user, or we want to apply other business logic in the event of a specific rejection.

## Future considerations to improve the application:
- Add an authentication layer.
- Use SQL aggregation to reduce memory usage and improve performance at scale.
- Introduce caching to avoid repeated database queries.
- In the event of larger files, it would be nice to parallelly batch-process them instead of line-by-line.
