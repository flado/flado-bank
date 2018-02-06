## Suncorp Technical Test - Florin Adochiei - 06/02/2018
 
# Question 1
For the following operations, please design a set of endpoint/verb combinations 
that would neatly model the scenario with a RESTful approach.

NB: No need to do any actual coding here,
 just document the API in a Word or text file.

1.	Open a new bank account.
2.	Deposit funds.
3.	Withdraw funds.
4.	Make a payment to another account.
5.	View transactions.

Where:

*	a bank account has the following attributes:
*	First Name
*	Last Name
*	Date of Birth
*	Account Creation Date
*	Type of Account [Deposit|Savings]
*	....all other resources, you can make up your own appropriate attributes


# `FladoBank` Account REST API ``` /fladobank/api/v1/accounts ```

## Overview

For simplicity I consider the customer identification is done 
through a 3rd party authentication & authorization mechanism (eg: OAuth2)

### Security assumption

The `/fladobank/api` must be secured:

* must be exposed only over HTTPS
* must have a security token mechanism in place (eg. OAuth) for authorization purposes; the security token can be send for each request in the HTTP Authorization header. The token can be generated on a 3rd party service and can include permissions as well as a custom TTL (time-to-live). Each API must check the token for validity before stating the operation requested. In case the token is not valid, the APIs can return HTTP 401 Unauthorized as described below. 

### Generic response codes
For almost any REST endpoint, in case of an unsuccessful operation, we can return some error HTTP status codes (depending on the situation/operation):

* if user not authorized to perform the operation (eg. based on security token provided):
```
	HTTP 401 Unauthorized, Body: none
```
* if this API is not available at the moment:
```
	HTTP 503 Service Unavailable, Response Body: { 'message': 'service is currently unavailable. please try again later'}
```
* if the operation has failed unexpectedly:
```
	HTTP 500 Internal Server Error, Body: none
```

* The above error response codes can be included in all the REST APIs (in case the operation could not be fulfilled).

Below I describe the APIs on success and other scenarios not covered by the HTTP status codes above (401, 503, 500).

---
## 1. Open account:
---

#### Request:
```
	HTTP POST /fladobank/api/v1/accounts, 
	Content-Type: application/json
	Body: { 'firstName': 'Florin', 'lastName': 'Adochiei', dob: '07/11/1978', 'accountType': 'Deposit' }
``` 
#### Response:

```
    Location: /fladobank/api/v1/accounts/{accountNumber}
	HTTP 201 Created, Response Body: none
	HTTP 400 Bad Request, Body: { 'message', 'invalid request' }
```

+ The account details could be retrieved by:
```
	HTTP GET /fladobank/api/v1/accounts/{accountNumber}
```

---
## 2. Deposit funds
---

### Request:
```
    Content-Type: application/json
	HTTP POST /fladobank/api/v1/accounts/{accountNumber}/transactions, Body: { 'amount': 200, 'txnType': 'credit' }
``` 

### Response:

```
    Location: /fladobank/api/v1/accounts/{accountNumber}/transactions/{transactionId}
	HTTP 201 Created, Response Body: none
	HTTP 400 Bad Request, Body: { 'message', 'invalid request'}
	HTTP 404 Not Found, Body: { 'message', 'account not found' }
```

+ The transaction details could be retrieved by:
```
	HTTP GET /fladobank/api/v1/accounts/{accountNumber}/transactions/{transactionId}
```

## 3. Withdraw funds

### Request:
```
    Content-Type: application/json
	HTTP POST /fladobank/api/v1/account/{accountNumber}/transactions, Body: { 'amount': 200, 'txnType': 'debit' }
``` 
### Response:

```
    Location: /fladobank/api/v1/accounts/{accountNumber}/transactions/{transactionId}
	HTTP 201 Created, Response Body: none
	HTTP 400 Bad Request, Body: { 'message', 'invalid request' }
	HTTP 404 Not Found, Body: { 'message', 'account not found' }
	HTTP 409 Conflict, Response Body: { 'message': 'insufficient funds'}	
```

## 4. Make a payment to another account 

### Request:
```
	HTTP POST /fladobank/api/v1/account/{accountNumber}/transactions, Body: { 'amount': 200, 'toAccount': 563626 }
``` 
### Response:
```
    Location: /fladobank/api/v1/accounts/{accountNumber}/transactions/{transactionId}
	HTTP 201 Created, Response Body: none
	HTTP 400 Bad Request, Body: { 'message', 'invalid request' }
	HTTP 404 Not Found, Body: { 'message', 'account not found' }
	HTTP 409 Conflict, Response Body: { error: 'insufficient funds' }
```

## 4. View Transactions 

### Request:
```
	HTTP GET /fladobank/api/v1/account/{accountNumber}/transactions?[pageNumber={pageNumber}]&[pageSize={pageSize}]
``` 
* pageNumber & pageSize query parameters are optional. The service implementation could use a default value for pageSize and always return first page + total number of transactions

### Response:
```
	HTTP 200 OK, Body:
	{ 
		'accountNumber': '{accountNumber}', 
	  	'transactions': [
			{ 'txnId': '23361123', 'txnType': 'debit', 'amount': 100, 'date': '2018-02-12 11:04:00', 'fromAccountId': {accountNumber}, 'toAccount' : 2332323 },
			{ 'txnId': '289323666', 'txnType': 'credit', 'amount': 50, 'date': '2018-03-13 09:32:11', 'fromAccountId': 12455336, 'toAccount' : {accountNumber}  },
			...
		],
		'pageNumber': 1,
		'pageSize': 100,
		'size': 1200,
		
	}
	HTTP 400 Bad Request, Body: { 'message', 'invalid request' }
	HTTP 404 Not Found, Body: { 'message', 'account not found' }

```

# Question 2

### Code Design
This section is designed to test your ability to design and write clean code. The implementation is not as important as the design and layout (packages, interfaces, classes, methods, etc.)

Implement a Maven-based project acting as a very limited, thread-safe, bank-in-a-box. Provide the ability for the following operations:
1.	Open a new bank account.
2.	Deposit funds.
3.	Withdraw funds.
4.	Make a payment to another account.
5.	View transactions.
#### NB: No need to write any external RESTful API for this code.

## Solution Overview

Designing this solution I've made the following assumptions & choices:

- fladobank implementation requires Java 8 
- a deposit/withdrawal is actually a transaction on a single account
- a transfer is made on two accounts ( deposit on destination account & withdrawal on source account)
- each account maintains it's own list of immutable transactions
- internal model is hidden from clients by using the `FladoBankService` facade
- even if the internal `Account` implementation is thread-safe, the bank clients must use the `FladoBankService` facade only
- the `FladoBankService` facade is exposed as a `@Service` Spring bean 

## Compile, test and generate the library 

```
    $ mvn clean install
```


