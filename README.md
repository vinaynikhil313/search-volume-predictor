# Search Volume Predictor
* This is an API that accepts any keywords (product names) and gives out a prediction on how much the search volume could be for that particular product.
* Makes use of the Amazon's Autocomplete API in order to generate the predictions - 

https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=apple iphone 13

### Documentation

What assumptions did you make?

* The keywords that are passed are actual products that can be found on Amazon. Some random text can be used to test the scenarios of getting a score close to 0.
* The keywords are case-insensitive.
* Only the presence of the exact keyword in the Autcomplete API response is considered as a match. If the predictions contain the keyword with a prefix or a suffix and not the keyword itself, then this is not considered a match.


How does your algorithm work?

* On a high level, the search volume prediction score will be high (tending towards 100) for products which are in high demand.
* When the demand for a particular product is high, the Amazon Autocomplete API will return the full name of the product while entering the first few characters of the name itself.
* By using this logic we can conclude that if the full search keyword is returned by the Amazon Autocomplete API when the first few characters of the keyword are searched then the product is in high demand and hence the search volume is also high for it. Thus, the search volume predictions score will be 100 or tending towards 100 for these type of products.
* We can also apply the reverse logic for the products which are not in demand and hence might be returned as part of the API response when I search with almost their full keyword text. Or it might not be returned at all in the API response even after searching for the entire keyword. These type of predictions will have a 0 value or tending towards 0.
* So the algorithm works in a way in which it queries for multiple different substrings of the provided keyword in order to find the smallest possible substring in which the entire keyword is returned as a prediction by the Amazon Autocomplete API.
* In order to minimize the API calls and optimize the response SLA of the microservice, I am using a Binary search approach to query the substrings.
* As part of this, the entire keyword is initially broken down at the midpoint and if that substring returns the entire keyword as a prediction then the substring is again broken down into its half size and so on until I find the smallest possible substring which gives the entire keyword as a prediction.
* If a particular substring does not give the entire keyword as a prediction, then the substring is extended by half size and the same process is repeated.
* After finding the smallest possible substring, the size of substring is divided by the total size and multiplied by 100 in order to find the search volume prediction score.

Do you think the (*hint) that we gave you earlier is correct and if so - why?

* I think the hint is correct and works according to my algorithm as I am just looking if the keyword is predicted when a substring is passed to the Autocomplete API.
* There is no comparison process with other strings as we only need to predict the occurrence of a particular keyword in accordance to it search volume without comparing it with other keywords (products).
* Hence, as per my algorithm, if the keyword is not returned as part of the Autocomplete API response for a particular substring, then it must not be in the top 10 predictions for that substring and hence it should have a lower search volume score. The algorithm ensures that a lower score is given to this kind of scenario as the substring has to be expanded and this decreases the prediction score.

How precise do you think your outcome is and why?

* I think the outcome would be precise for keywords/products which have a decent sized name (more than 10 characters).
* This is because the search scope is big, and it has more distinct prediction scores. This would result in more accurate calculations.

### Build and run
* Prerequisites
  * Java 8+
  * Maven
* To build - 
  * mvn clean install
* To run - 
  * java -jar target/search-volume-predictor-0.0.1-SNAPSHOT.jar

### Test the API
* To test the microservice deployed locally - 
  * curl --location --request GET 'http://localhost:8080/estimate?keyword=Apple iPhone 13'
* To test the microservice deployed on Heroku - 
  * curl --location --request GET 'https://search-volume-predictor.herokuapp.com/estimate?keyword=Apple iPhone 13'